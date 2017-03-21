package com.wzk.webservice;

import javax.jws.WebService;

@WebService(endpointInterface="com.wzk.service.IMyService")
public class MyServiceImpl implements IMyService {

	@Override
	public int plus(int a, int b) {
		System.out.println(a + " + " + b + " = " + (a + b));
		return a+b;
	}

	@Override
	public int minus(int a, int b) {
		System.out.println(a + " - " + b + " = " + (a - b));
		return a-b;
	}

}
