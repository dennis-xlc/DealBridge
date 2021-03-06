package com.paypal.dealbridge.web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paypal.dealbridge.service.DiscountService;
import com.paypal.dealbridge.service.FavoriteService;
import com.paypal.dealbridge.service.ShareService;
import com.paypal.dealbridge.storage.domain.Discount;

@Controller
public class DiscountController {

	@Autowired
	private DiscountService discountService;
	@Autowired
	private ShareService shareService;
	@Autowired
	private FavoriteService favoriteService;

	@RequestMapping(path = "/api/discount/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Discount getDiscount(@PathVariable("id") int id) {
		return discountService.getDiscountById(id);
	}

	@RequestMapping(path = "/api/discount/top_click_rate", method = RequestMethod.GET)
	@ResponseBody
	public List<Discount> getTopDiscount(@RequestParam("limitNumber") int limitNumber) {
		return discountService.getTopDiscount(limitNumber);
	}
	


	@RequestMapping(path = "/discount/{id}", method = RequestMethod.GET)
	public String showDiscount(@PathVariable("id") int id, Model model, HttpSession session) {
		int userId = (int)session.getAttribute("userId");
		double latitude = (double)session.getAttribute("latitude");
		double longitude = (double)session.getAttribute("longitude");
		Discount discount = discountService.getDiscountById(id);
		Integer favoriteId = favoriteService.existFavorite(userId, id);
		discountService.updateClickRate(id);
		int shareTime = shareService.countSharedTimes(id);
		model.addAttribute("discount", discount);
		model.addAttribute("shareTime", shareTime);
		model.addAttribute("userId", userId);
		model.addAttribute("favoriteId", favoriteId);
		model.addAttribute("latitude", latitude);
		model.addAttribute("longitude", longitude);
		return "discount_detail";
	}
}
