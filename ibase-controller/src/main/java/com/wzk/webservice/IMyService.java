package com.wzk.webservice;

import javax.jws.WebService;

@WebService
public interface IMyService {

	public int plus(int a,int b);
	
	public int minus(int a,int b);
}
