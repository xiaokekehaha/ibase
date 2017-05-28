package com.wzk.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wzk.mvc.service.IMyOpenballInfoService;

@Component
public class MyJob {
	
	@Autowired
	private IMyOpenballInfoService myOpenballInfoService;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	
	public void execute1(){
		ConcurrentNavigableMap map = new ConcurrentSkipListMap(); 
		myOpenballInfoService.insertdata();
        System.out.printf("任务: %s, 当前时间: %s\n", 1, sdf.format(new Date()));
    }

}
