package com.wzk.mvc.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Spider5PingPing {
	public static void rankBall(){
		try{
			Document document = Jsoup.connect("http://caipiao.163.com/award/cqssc/20160105.html").timeout(60*1000).get();
			/*1-40*/
			
			Elements elements = document.select(".start");
//			Elements elements = document.select(".award-winNum");
			/*for(int i=1;i<=40;i++){
				System.out.println(elements.get(0));
			}*/

			System.out.println(elements.size());
			for(Element element :elements){
				if(StringUtils.isNotEmpty(element.attr("data-period"))&&StringUtils.isNotEmpty(element.attr("data-win-number"))){
					
				}
				System.out.println(element.attr("data-period")+"====>"+element.attr("data-win-number").replaceAll(" ", "").replaceAll("\n", ""));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		rankBall();
	}
}
