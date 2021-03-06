# encoding=utf-8

import jieba
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.cluster import KMeans
import re
import random
import datetime
import MySQLdb

#
# Fields of a transaction record that I am going leverage in this system are bellow
# bank_name, merchant, location, amount, time
#

# db = MySQLdb.connect(host="localhost", user="root", passwd="", db="deal_bridge", charset="utf8")  # local database
db = MySQLdb.connect(host="10.22.96.25", user="dealbridge_user", passwd="dealbridge_pwd", db="deal_bridge", charset="utf8")
cursor = db.cursor()
sql = "SELECT discount_id, bank_name, summary, description, merchant_description, " \
      "merchant_location, summary FROM discount where area != '全国'" # limit 0, 1000 "
discounts = []
indices = []
summaries = []
banks = []
try:
    cursor.execute(sql)
    results = cursor.fetchall()
    # discounts = ["%s %s %s" % (result[1], result[2], result[3]) for result in results]
    discounts = ["%s %s %s" % (result[2], result[3], result[5]) for result in results]
    indices = [result[0] for result in results]
    banks = [result[1] for result in results]
    banks = list(set(banks))       # remove the duplicate banks
    merchants = [result[5] + " " + result[4] for result in results]
    summaries = [result[6] for result in results]
except:
    print "Error, unable to fetch data"

# for bank in banks:
#     print bank
discounts_after_cut = [re.sub("[\d]+", "", " ".join(jieba.cut(discount))) for discount in discounts]
vectorizer = TfidfVectorizer()
discounts_tfidf = vectorizer.fit_transform(discounts_after_cut)

cluster_cnt = 30
clf = KMeans(n_clusters=cluster_cnt)
s = clf.fit(discounts_tfidf)
# print s
# 20个中心点
# print(clf.cluster_centers_)
# 每个样本所属的簇
# print(clf.labels_)

# cluster the discounts info
clusters = []
for cluster in xrange(cluster_cnt):
    cnt = 0
    c = []
    for i in xrange(len(clf.labels_)):
        if clf.labels_[i] == cluster:
            cnt += 1
            c.append(i)
            # print indices[i], discounts[i], "merchant ", merchants[i]
    clusters.append(c)
    # print "cluster %d has %d nodes" % (cluster, cnt)

# 用来评估簇的个数是否合适，距离越小说明簇分的越好，选取临界点的簇个数
# print cluster_cnt, clf.inertia_
# for c in clusters:
#     print c


# return 2 or 3 clusters or banks
def get2or3():
    if random.random() > 0.5:
        return 2
    else:
        return 3


# extract the location from the merchant description
def extract_location(index):
    segs = jieba.cut(merchants[index])
    segs = reversed(list(segs))    # reverse the generator
    # sql = "select name, parent_id, level from locations where locations.name = %s"
    for seg in segs:
        try:
            cursor.execute("select name, parent_id, level from locations where locations.name = %s", seg)
            r = cursor.fetchone()
            if r[2] == 3:         # district
                cursor.execute("select name, parent_id from locations where locations.id like %s_", r[1])
                result_city = cursor.fetchone()
                cursor.execute("select name from locations where locations.id=%s", result_city[1])
                result_province = cursor.fetchone()
                return result_province[0] + result_city[0] + r[0]
            elif r[2] == 2:      # city
                cursor.execute("select name from locations where locations.id=%s", r[1])
                result_province = cursor.fetchone()
                return result_province[0] + r[0]
            elif r[2] == 1:
                return r[0]
            else:
                continue
        except:
            print "Fetch %d error" % indices[index]
    return None


# extract the merchant from the merchant description
def extract_merchant(index):
    return summaries[index]

sql = "insert into mock_transaction(user_id, bank_name, merchant, location, amount, transaction_time)" \
      "values(%s,%s,%s,%s,%s,%s)"

cnt_banks = get2or3()
cnt_clusters = get2or3()
for i in xrange(cnt_banks):
    bank_name = banks[int(random.random()*len(banks))]
    for j in xrange(cnt_clusters):
        cluster_index = int(random.random()*cluster_cnt)
        for k in xrange(int(random.random()*100)):
            try:
                _time = datetime.datetime.now() - datetime.timedelta(days=int(random.random()*365), hours=int(random.random()*24), minutes=int(60*random.random()))
                merchant_index = clusters[cluster_index][int(random.random()*len(clusters[cluster_index]))]
                # des = merchants[merchant_index]
                m = extract_merchant(merchant_index)
                l = extract_location(merchant_index)
                cursor.execute(sql, (1, bank_name, m, l, "%.1f" % (2000*random.random() + 200), _time.strftime('%Y-%m-%d %H:%M:%S')))
                db.commit()
            except:
                print "error detected during inserting data %d" % indices[merchant_index]
                db.rollback()

db.close()
