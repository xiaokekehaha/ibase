package com.wzk.mvc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Spider3 {
	
	public static void main(String[] args) {
		System.out.println(getRankBall());
//		multiple(8);
	}
	public static String getRankBall(){
		/* 10个数字根据奇偶分成2组
		 * 
		 * a: 0 2 4 6 8
		 * b: 1 3 5 7 9
		 * 上期跨度杀本期个位
		 * 上期开奇数，跟去除当期号码后的奇数，偶数一样
		 * 再根据奇偶，大小，升降，振幅选取优选高胜率的号码，加一倍其他号码一倍
		 * 
		 * 如果不中，则根据以上规则加倍，加至3倍后如不中，从1倍开始
		 
		 * 
		 * 冷号倍投方案
		 * 初始5倍
		 * 倍数	5		5		5		5		5		5		
		 * 本金	10		20		30		40		50		60
		 * 奖金	92.5		92.5	92.5	92.5	92.5	92.5
		 * 盈利	82.5		72.5	62.5	52.5	42.5	32.5
		 * 关注	遗漏位数为1，5，7，9，如连两期不中（1-2；5-2；7-2；9-1），则停追2期，再追2期，如不中停追5期，如不中，看情况追（如果上40，则到51开追，此时30倍追，如上50，则到60开追，此时可50倍追）再追3期,至多追15
		 * 1	1	1	2	2	3	4	5	6	7
		 * 1--->8 2--->4 3--->3
		 * 单期总金额不可大于30
		 * 倍: 1+2+3+4+6+9+14+21+31+47
		 * 资金 2+6+12+20+32+50+78+120+182+276 十期才276 元
		 * 
		 * 一天暂定：止损200，止盈400
		 * */
		/**
		 * 2
		 * 1		1		1		1		2		2		3		4		5		6		7
		 * 4		4		4		4		8		8		12		16		20		24		28
		 * 18.5		18.5	18.5	18.5	37		37		55.5	74		92.5	111		129.5
		 * 14.5		10.5	6.5		2.5		13		5		11.5	14		12.5	7		
		 * 															3
		 * 															12
		 * 															55.5
		 * 															-0.5
		 */
		String rankball = "";
		try {
			Document document = Jsoup.connect("http://caipiao.163.com/award/cqssc/"+DateUtil.getDateTime("yyyyMMdd")+".html").timeout(60*1000).get();
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
			sql = "select * from my_openball_info order by expect desc limit 1";
			sta = con.prepareStatement(sql);
			rs = sta.executeQuery();
			if(rs.next()){
				List<Integer> oddNumberList= new ArrayList<Integer>();
				List<Integer> evenNumberList= new ArrayList<Integer>();
				Map<Integer,String> killMap = new HashMap<Integer,String>();
				initData(oddNumberList,evenNumberList,killMap);
				Integer lastball = rs.getInt(5);
				/*计算杀码*/
				String realball = rs.getString(4);
				Long temp = Long.valueOf(realball)*Long.valueOf(realball.substring(0, 3));
				int firstball = Integer.valueOf(temp.toString().substring(0,1));
				Integer sum = firstball; 
				int count = 0;//加几次
				for(int i=0;i<temp.toString().length();i++){
					if(count > 2){
						break;
					}
					int second = Integer.parseInt(temp.toString().charAt(i)+"");
					if( second != firstball){
						sum += second;
						count ++;
					}
				}
				Integer kill = Integer.parseInt((sum+"").charAt(sum.toString().length()-1)+"") + 10 - Integer.parseInt(realball.substring(1,2));
				System.out.println("杀码1："+(kill+"").charAt(kill.toString().length()-1)+"");
				System.out.println("杀码2："+killMap.get(lastball));
//				int kill = Integer.parseInt(sum.)
				
				if(oddNumberList.contains(lastball)){
					oddNumberList.remove(lastball);
					rankball = oddNumberList.toString().replace("[", "").replace("]", "").replaceAll(",", "").replaceAll(" ", "");
				}else{
					evenNumberList.remove(lastball);
					rankball = evenNumberList.toString().replace("[", "").replace("]", "").replaceAll(",", "").replaceAll(" ", "");
				}
				System.out.println();
			}
			con.commit();
			rs.close();
			sta.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rankball;
	}
	
	private static void initData(List<Integer> oddNumberList,List<Integer> evenNumberList,Map<Integer,String> killMap){
		oddNumberList.add(1);
		oddNumberList.add(3);
		oddNumberList.add(5);
		oddNumberList.add(7);
		oddNumberList.add(9);
		evenNumberList.add(0);
		evenNumberList.add(2);
		evenNumberList.add(4);
		evenNumberList.add(6);
		evenNumberList.add(8);
		killMap.put(0, "5,9,1");
		killMap.put(1, "0,2,6");
		killMap.put(2, "1,3,7");
		killMap.put(3, "2,4,8");
		killMap.put(4, "3,5,9");
		killMap.put(5, "4,6,0");
		killMap.put(6, "5,7,1");
		killMap.put(7, "6,8,2");
		killMap.put(8, "7,9,3");
		killMap.put(9, "8,0,4");
	}
	
	/**
	 * 倍投公式
	 * @return
	 */
	public static void multiple(int first){
		/**
		 * 2
		 * 1		1		1		1		2		2		3		4		5		6		7
		 * 4		4		4		4		8		8		12		16		20		24		28
		 * 18.5		18.5	18.5	18.5	37		37		55.5	74		92.5	111		129.5
		 * 14.5		10.5	6.5		2.5		13		5		11.5	14		12.5	7		
		 * 															3
		 * 															12
		 * 															55.5
		 * 															-0.5
		 */
		int k = 1;
		double count = 0;
		double earning =0; 
		for(int i=0;i<40;i++){
			
			count += first*k;
			while(18.5*k-count < 0){
				System.out.println("k----------------------------------->"+k);
				k++;
				count +=first;
			}
			if(i>=5 && i%5==0){
				System.out.println("*****************************************"+i);
			}
			earning = 18.5*k - count;
			System.out.println(count + "===============================>"+earning);
		}
	}
}
