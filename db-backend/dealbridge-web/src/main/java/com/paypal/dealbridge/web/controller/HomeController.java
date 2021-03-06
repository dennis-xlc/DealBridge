package com.paypal.dealbridge.web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.paypal.dealbridge.web.util.GPSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.paypal.dealbridge.service.DiscountService;
import com.paypal.dealbridge.service.SearchService;
import com.paypal.dealbridge.storage.domain.Discount;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
	@Autowired
	private DiscountService discountService;
	@Autowired
	private SearchService searchService;
	
	public static final int TOP_DISCOUNT_NUM = 6;

	@RequestMapping(path = "/welcome")
	public String welcome() {
		return "index.html";
	}

	@RequestMapping(path="/home", method=RequestMethod.GET)
	public String showHomePage(HttpSession session, Model model, @RequestParam(value = "lat", required = false) Double latitude, @RequestParam(value = "lng", required = false) Double longitude) {
		int userId = (int)session.getAttribute("userId");
		String area = (String)session.getAttribute("area");
		if (latitude != null && longitude != null) {
			area = GPSUtil.geoDecoder(latitude, longitude).replace("市", "");
			session.setAttribute("area", area);
			session.setAttribute("latitude", latitude);
			session.setAttribute("longitude", longitude);
			System.out.println(latitude + " " + longitude + " " + area);

		}
		List<Discount> hots = discountService.getTopDiscount(TOP_DISCOUNT_NUM);
		List<String> hotKeywords = searchService.getHotKeywords(9);
		List<String> searchHistories = searchService.getUserHistory(3, 10);
		model.addAttribute("searchHistories", searchHistories);
		model.addAttribute("hotKeywords", hotKeywords);
		model.addAttribute("hots", hots);
		model.addAttribute("userId", userId);
		model.addAttribute("area", area);
		model.addAttribute("latitude", latitude);
		model.addAttribute("longitude", longitude);

		return "home";
	}
	
}
