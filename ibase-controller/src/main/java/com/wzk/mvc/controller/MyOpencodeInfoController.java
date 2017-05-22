package com.wzk.mvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wzk.mvc.service.IMyOpenballInfoService;
import com.wzk.mvc.service.IMyOpencodeInfoService;

@Controller
@RequestMapping("/opencode")
public class MyOpencodeInfoController {

	@Autowired
	private IMyOpencodeInfoService myOpencodeInfoService;
	
	@Autowired
	private IMyOpenballInfoService myOpenballInfoService;
	
	@RequestMapping("/insert")
	public String opencodeData(){
		myOpencodeInfoService.insertdata();
		return "";
	}
	
	public void test(){
		System.out.println("777777777777777777777777");
	}
	
	@RequestMapping("/ggggggg")
	public String ggggggg(){
		myOpenballInfoService.inserttest();
		return "";
	}
}
