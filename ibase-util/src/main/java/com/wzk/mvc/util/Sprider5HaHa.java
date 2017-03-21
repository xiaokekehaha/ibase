package com.wzk.mvc.util;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Sprider5HaHa {
	/**
	 * http://hahassc.com/data.php?ac=2
	 * 
	 * [ 驴友计划 ] 062-064┊驴友后一┊<02468>┊062期┊第1期┊数钱中...
	 */
	public static void main(String[] args) {
		rankBall();
	}
	
	
	public static String rankBall(){
		try{
			Document document = Jsoup.connect("http://hahassc.com/data.php?ac=2").timeout(60*1000).get();
//			Pattern pattern1 = Pattern.compile("\\d{3}-\\d{3}┊[\u4E00-\u9FA5]{4}┊&lt;\\d{5}&gt;┊第\\d{1}期┊数钱中...");
			Elements elements = document.select("a");
			Map<Integer,Integer> map = new HashMap<Integer,Integer>();
			map.put(0, 0);
			map.put(1, 0);
			map.put(2, 0);
			map.put(3, 0);
			map.put(4, 0);
			map.put(5, 0);
			map.put(6, 0);
			map.put(7, 0);
			map.put(8, 0);
			map.put(9, 0);
			for(Element e:elements){
				String line = e.html().toString().replaceAll(" ", "").replaceAll("\n", "");
				System.out.println(line);
				String rankBall = line.substring(23, 28);
				for(int i=0;i<rankBall.length();i++){
					switch (Integer.parseInt(String.valueOf(rankBall.charAt(i)))) {
					case 0:
						map.put(0, map.get(0)+1);
						break;
					case 1:
						map.put(1, map.get(1)+1);
						break;
					case 2:
						map.put(2, map.get(2)+1);
						break;
					case 3:
						map.put(3, map.get(3)+1);
						break;
					case 4:
						map.put(4, map.get(4)+1);
						break;
					case 5:
						map.put(5, map.get(5)+1);
						break;
					case 6:
						map.put(6, map.get(6)+1);
						break;
					case 7:
						map.put(7, map.get(7)+1);
						break;
					case 8:
						map.put(8, map.get(8)+1);
						break;
					case 9:
						map.put(9, map.get(9)+1);
						break;
					}
				}
				
			}
			System.out.println(map);
			map = MapUtil.sortByValueDesc(map);
			System.out.println(map);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
}
