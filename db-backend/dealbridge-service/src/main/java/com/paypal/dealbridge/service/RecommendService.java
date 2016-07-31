package com.paypal.dealbridge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.dealbridge.storage.domain.Discount;
import com.paypal.dealbridge.storage.mapper.RecommendMapper;

@Service
public class RecommendService {
	
	@Autowired
	private RecommendMapper recommendMapper;
	
	public List<Discount> getDiscountByUserId(int userId, Integer startIndex, Integer limitNumber){
		return recommendMapper.getDiscountByUserId(userId, startIndex, limitNumber);
	}
	

}
