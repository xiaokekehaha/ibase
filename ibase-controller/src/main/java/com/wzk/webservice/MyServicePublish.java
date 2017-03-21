package com.wzk.webservice;

import javax.xml.ws.Endpoint;

public class MyServicePublish {

	public static void main(String[] args) {
		String address = "http://localhost:9999/ms";
		Endpoint.publish(address, new MyServiceImpl());
	}
}
