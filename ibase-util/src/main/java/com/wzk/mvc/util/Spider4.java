package com.wzk.mvc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public class Spider4 {
	public static void main(String[] args) {
		
	}
	public static void rank(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:9700/cqssc", "wzk", "wzk");
			con.setAutoCommit(false);
			PreparedStatement sta = null;
			ResultSet rs = null;
			Random random = new Random();
			int start = random.nextInt(1000);
			String sql = "select * from my_openball_info order by expect desc limit ?,30";
			sta = con.prepareStatement(sql);
			sta.setInt(1, start);
			rs = sta.executeQuery();
			while(rs.next()){
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
