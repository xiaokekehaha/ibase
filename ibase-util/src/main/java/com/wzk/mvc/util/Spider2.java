package com.wzk.mvc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.wzk.mvc.dto.Cqssc;

public class Spider2 {
	
	public static void main(String[] args) {
		countLastBallFromPage();
	}
	
	public static void countLastBallFromPage(){
		try {
			Document document = Jsoup.connect("http://caipiao.163.com/award/cqssc/20170210.html").timeout(60*1000).get();
			Elements elements = document.select("tbody").get(0).select(".start");
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:9700/cqssc", "wzk", "wzk");
			con.setAutoCommit(false);
			PreparedStatement sta = null;
			ResultSet rs = null;
			String sql = "";
			for(Element element :elements){
				if(element.hasAttr("data-win-number")){
					System.out.println(element.toString());
					String expect = "20"+element.attr("data-period");
					if(!"20170203001".equals(expect)){
						String opencode = element.attr("data-win-number").replaceAll(" ", "");
						sql = "select * from my_openball_info where expect = ?";
						sta = con.prepareStatement(sql);
						sta.setLong(1,Long.valueOf(expect));
						rs = sta.executeQuery();
						if (!rs.next()) {
							sql = "insert into my_openball_info (expect,opendate,opencode,lastcode) values (?,?,?,?)";
							sta = con.prepareStatement(sql);
							sta.setLong(1, Long.valueOf(expect));
							sta.setLong(2, Long.valueOf(expect.substring(0, 8)));
							sta.setString(3, opencode);
							sta.setInt(4, Integer.parseInt(opencode.substring(4,5)));
							sta.executeUpdate();
						}
					}
				}
			}
			con.commit();
			rs.close();
			sta.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void countLastBallFromApi(){
		String s = HttpUtils.get("http://f.apiplus.cn/cqssc-50.json", "");
		Cqssc cqssc = JSON.parseObject(s, Cqssc.class);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:9700/cqssc", "wzk", "wzk");
			con.setAutoCommit(false);
			List<Cqssc.OpenballInfo> list =cqssc.getData();
			PreparedStatement sta = null;
			ResultSet rs = null;
			String sql = "";
			for(Cqssc.OpenballInfo co:list){
				sql = "select * from my_openball_info where expect = ?";
				sta = con.prepareStatement(sql);
				sta.setLong(1, co.getExpect());
				rs = sta.executeQuery();
				if (!rs.next()) {
					sql = "insert into my_openball_info (expect,opencode,lastcode) values (?,?,?)";
					sta = con.prepareStatement(sql);
					sta.setLong(1, co.getExpect());
					sta.setString(2, co.getOpencode().replaceAll(",", ""));
					sta.setInt(3, Integer.parseInt(co.getOpencode().substring(8,9)));
					sta.executeUpdate();
				}
			}
			con.commit();
			rs.close();
			sta.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
