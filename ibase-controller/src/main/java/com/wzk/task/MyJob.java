package com.wzk.task;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wzk.mvc.service.IMyOpenballInfoService;

@Component
public class MyJob {
	
	@Autowired
	private IMyOpenballInfoService myOpenballInfoService;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	
	public void execute1(){
		myOpenballInfoService.insertdata();
        System.out.printf("Task: %s, 当前时间: %s\n", 1, sdf.format(LocalDateTime.now()));
    }

}
