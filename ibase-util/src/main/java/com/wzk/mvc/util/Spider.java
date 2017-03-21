package com.wzk.mvc.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Spider {
	public static void main(String[] args) {
		/*适用时间15:00-17:00*/   
		//http://kaijiang.500.com/ssc.shtml
		//
		Spider.getMoney();
//		long i = Long.valueOf("20170209120");
//		System.out.println(i);
	}
	public static void getMoney() {
		//3381-3385 3466-3480 
		/*初始数据S*/
		List<Integer> pageList = new ArrayList<Integer>();
		pageList = initPageList(pageList);
		String cycels = "";//期数
		int[][] seq= new int[20][10];//号码矩阵
		float[][] per = new float[20][10];//胜率矩阵
		int x=0;//seq坐标
		float rank = 0.1f;//概率
		String ball = "";//推荐组合索引
		int index=0;//获取的推荐组合索引
		Document document = null;// 文档对象，用来接收html页面
		String line = null;//筛选出来的元素
		Pattern pattern1 = Pattern.compile("\\d{3}-\\d{3}┊[\u4E00-\u9FA5]{4}┊&lt;\\d{8}&gt;┊正在进行第\\d{1}期┊……");
		Pattern pattern2 = Pattern.compile("\\d{8}");
		Pattern pattern3 = Pattern.compile("\\d{3}-\\d{3}┊[\u4E00-\u9FA5]{4}┊&lt;\\d{8}&gt;┊\\d{3}期\\d{5}┊(√|×)(\\+|-)(\\d{1}|-)<br>\\d{3}-\\d{3}┊[\u4E00-\u9FA5]{4}┊&lt;\\d{8}&gt;┊正在进行第\\d{1}期┊……");
		Pattern pattern4 = Pattern.compile("\\d{5}┊");
		Matcher matcher1 = null;
		Matcher matcher2 = null;
		String now = DateUtil.format(new Date(),DateUtil.DATE_PATTERN.YYYYMMDD);
		/*初始数据E*/
		String s = "";//临时变量
		String lastRealBall = "";
		for(Integer page:pageList){
			index = 0;
			// 获取指定网址的页面内容
			try {
				document = Jsoup.connect("http://jihua.ttssc.net/cqssc_" + page + "_1.html").timeout(60*1000).get();
				line = document.select("#content").get(0).html().toString().replaceAll(" ", "").replaceAll("\n", "");
				matcher1 = pattern1.matcher(line);  
				if(matcher1.find()) {
					s = matcher1.group();
					if(x == 0){
						cycels = now + s.substring(0, 3);
						matcher2 = pattern3.matcher(line);
						if(matcher2.find()){
							lastRealBall = matcher2.group();
							lastRealBall = lastRealBall.split("<br>")[0];
							matcher2 = pattern4.matcher(lastRealBall);
							if(matcher2.find()){
								lastRealBall = matcher2.group().substring(0, 5);
							}
						}
					}
					System.out.println(s);
					matcher2 = pattern2.matcher(s);
					if(matcher2.find()){
						ball = matcher2.group();
						while(index < 8){
							switch (Integer.parseInt(String.valueOf(ball.charAt(index)))) {
							case 0:
								seq[x][0]=1;
								per[x][0]=rank;
								break;
							case 1:
								seq[x][1]=1;
								per[x][1]=rank;
								break;
							case 2:
								seq[x][2]=1;
								per[x][2]=rank;
								break;
							case 3:
								seq[x][3]=1;
								per[x][3]=rank;
								break;
							case 4:
								seq[x][4]=1;
								per[x][4]=rank;
								break;
							case 5:
								seq[x][5]=1;
								per[x][5]=rank;
								break;
							case 6:
								seq[x][6]=1;
								per[x][6]=rank;
								break;
							case 7:
								seq[x][7]=1;
								per[x][7]=rank;
								break;
							case 8:
								seq[x][8]=1;
								per[x][8]=rank;
								break;
							case 9:
								seq[x][9]=1;
								per[x][9]=rank;
								break;
							}
							index++;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			x++;
		}
		float[] rang = new float[11];//胜率集合
		for(int m=0;m<per.length;m++){
			float[] arr2 = per[m];
			for(int n=0;n<arr2.length;n++){
				rang[n]+=arr2[n];
			}
		}
		Map<Integer,Float> map = new HashMap<Integer,Float>();//胜率map
		for(int i=0;i<10;i++){
			map.put(i, rang[i]);
		}
		map = MapUtil.sortByValueDesc(map);
		System.out.println("-----------第"+cycels+"胜率排序---------------");
		String rankball = "";
		for(Integer i:map.keySet()){
			System.out.println(i+"==================>"+NumberUtil.formatNumber(map.get(i)));
			rankball += i;
		}
		System.out.println("rankball==================>"+rankball.substring(0, 5));
		insertRankball(rankball, cycels,lastRealBall);
	}
	
	private static List<Integer> initPageList(List<Integer> pageList){
		pageList.add(3381);
		pageList.add(3382);
		pageList.add(3383);
		pageList.add(3384);
		pageList.add(3385);
		pageList.add(3466);
		pageList.add(3467);
		pageList.add(3468);
		pageList.add(3469);
		pageList.add(3470);
		pageList.add(3471);
		pageList.add(3472);
		pageList.add(3473);
		pageList.add(3474);
		pageList.add(3475);
		pageList.add(3476);
		pageList.add(3477);
		pageList.add(3478);
		pageList.add(3479);
		pageList.add(3480);
		return pageList;
	}
	private static void insertRankball(String rankball,String cycels,String lastRealball){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:9700/cqssc", "wzk", "wzk");
			con.setAutoCommit(false);
			// 注意★
			String sql = "select * from my_percent where cycels = ?";
			PreparedStatement sta = con.prepareStatement(sql);
			sta.setString(1, cycels);
			ResultSet rs = sta.executeQuery();
			// 注意：当现实一条记录时：while可以换成if。★
			if (rs.next()) {
				System.out.println("第"+cycels+"期已存在...");
			}else{
				sql = "insert into my_percent (cycels,rankball) values (?,?)";
				sta = con.prepareStatement(sql);
				sta.setString(1, cycels);
				sta.setString(2, rankball);
				int i = sta.executeUpdate();
				if(i > 0){
					System.out.println("第"+cycels+"期已插入...");
				}
			}
			sql = "select * from my_percent where cycels = ? and is_win=0";
			sta = con.prepareStatement(sql);
			sta.setString(1, (Long.valueOf(cycels)-1)+"");
			rs = sta.executeQuery();
			if(rs.next()){
				String lastRankBall = rs.getString(3);
				int id=rs.getInt(1);
				int hitindex = lastRankBall.indexOf(lastRealball.substring(4, 5))+1;
				int isWin = 1;
				if(hitindex > 5){
					isWin  = 2;
				}
				sql = "update my_percent set realball=?,is_win=?,hitindex=?,modify_time=now() where id=?";
				sta = con.prepareStatement(sql);
				sta.setString(1, lastRealball);
				sta.setInt(2, isWin);
				sta.setInt(3, hitindex);
				sta.setInt(4, id);
				sta.executeUpdate();
				if(hitindex <= 5){
					System.out.println("第"+(Long.valueOf(cycels)-1)+"期已中奖...|第"+hitindex+"位命中");
				}else{
					System.out.println("第"+(Long.valueOf(cycels)-1)+"期未中奖...|第"+hitindex+"位命中");
				}
				System.out.println("第"+(Long.valueOf(cycels)-1)+"期已更新...");
			}
			con.commit();
			rs.close();
			sta.close();
			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
