package com.paypal.dealbridge.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.paypal.dealbridge.service.LocationRecordService;

@Controller
public class CityListController {
	@Autowired
	private LocationRecordService lrs;
	
	public static final int HOT_CITY_NUM = 3;
	
	@RequestMapping(path = "/api/citylist", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getHotCity(){
		return lrs.getHotLocation(HOT_CITY_NUM);
	}
	
	@RequestMapping(path = "/citylist", method = RequestMethod.GET)
	public String showCityList(Model model){
		List<String> hotCity = lrs.getHotLocation(HOT_CITY_NUM);
		model.addAttribute("hotcity", hotCity);
		return "citylist";
	}
}