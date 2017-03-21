package com.wzk.webservice;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class MyServiceClient {

	public static void main(String[] args) {
		try {
			URL url = new URL("http://localhost:9999/ms?wsdl");
			QName qName = new QName("http://service.wzk.com/", "MyServiceImplService");
			Service service = Service.create(url,qName);
			IMyService myService = service.getPort(IMyService.class);
			System.out.println(myService.plus(10, 5));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
