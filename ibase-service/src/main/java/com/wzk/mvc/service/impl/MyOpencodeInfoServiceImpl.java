package com.wzk.mvc.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wzk.mvc.dao.MyOpencodeInfoMapper;
import com.wzk.mvc.model.MyOpencodeInfo;
import com.wzk.mvc.service.IMyOpencodeInfoService;
import com.wzk.mvc.util.DateUtil;
import com.wzk.mvc.util.MailUtil;
import com.wzk.mvc.util.PropertiesUtil;
import com.wzk.mvc.util.StringUtils;

@Service
public class MyOpencodeInfoServiceImpl implements IMyOpencodeInfoService {

	String oddStr = ",1,3,5,7,9,";// 奇数
	String bigStr = ",5,6,7,8,9,";// 大数
	int lastOddGapNum = 0;// 个位奇数连续
	int lastEvenGapNum = 1;// 个位偶数连续
	int lastBigGapNum = 2;// 个位大数连续
	int lastSmallGapNum = 0;// 个位小数连续
	int lastEvenOddGapNum = 1;// 个位奇偶连续
	int lastBigSmallGapNum = 0;// 个位大小连续
	boolean preLastEvenOddFlag = true;// 个位奇偶连续标识 上期奇：true 偶：false
	boolean preLastBigSmallFlag = true;// 个位大小连续标识 上期大：true 小：false
	int fourthOddGapNum = 0;// 十位奇数连续
	int fourthEvenGapNum = 1;// 十位偶数连续
	int fourthBigGapNum = 1;// 十位大数连续
	int fourthSmallGapNum = 0;// 十位小数连续
	int fourthEvenOddGapNum = 1;// 十位奇偶连续
	int fourthBigSmallGapNum = 7;// 十位大小连续
	boolean preFourthEvenOddFlag = true;// 十位奇偶连续标识 上期奇：true 偶：false
	boolean preFourthBigSmallFlag = true;// 十位大小连续标识 上期大：true 小：false

	MyOpencodeInfo lastCode = null;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	@Autowired
	private MyOpencodeInfoMapper myOpencodeInfoMapper;

	@Override
	public void insertdata() {
		try {
			lastCode = myOpencodeInfoMapper.selectOneByLast();
			
			Date start = sdf.parse(lastCode.getOpendate() + "");
			if (lastCode.getExpect().toString().endsWith("120")) {// 天数加一
				start = DateUtil.addDate(start, Calendar.DAY_OF_MONTH, 1);
			}
			Date end = sdf.parse(sdf.format(new Date()));

			Date lastYearGapStart = sdf.parse("20160207");
			Date lastYearGapEnd = sdf.parse("20160213");
			Date currYearGapStart = sdf.parse("20170127");
			Date currYearGapEnd = sdf.parse("20170202");

			while (start.getTime() <= end.getTime()) {
				if ((lastYearGapStart.getTime() <= start.getTime() && start.getTime() <= lastYearGapEnd.getTime())
						|| (currYearGapStart.getTime() <= start.getTime()
								&& start.getTime() <= currYearGapEnd.getTime())) {
				} else {
					insertCode(sdf.format(start));
				}
				start = DateUtil.addDate(start, Calendar.DAY_OF_MONTH, 1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertCode(String page) throws IOException, ParseException {
		Document document = Jsoup.connect("http://caipiao.163.com/award/cqssc/" + page + ".html").timeout(60 * 1000).get();
		Elements elements = document.select(".start");

		Map<String, String> treeMap = new TreeMap<String, String>();

		String maxKey = "";
		for (Element element : elements) {
			String period = element.attr("data-period");
			String winNumber = element.attr("data-win-number").trim().replace(" ", "");
			if (StringUtils.isNotEmpty(period) && StringUtils.isNotEmpty(winNumber)) {
				treeMap.put(period, winNumber);
				if (maxKey.compareTo(period) < 0) {
					maxKey = period;
				}
			}
		}

		String key = maxKey;
		if (StringUtils.isEmpty(key)) {
		} else {
			if (lastCode.getExpect().longValue() == Long.valueOf("20" + (Long.parseLong(key) - 1)).longValue()) {
				insertData(key, treeMap, lastCode);
			} else {
				for (String s : treeMap.keySet()) {
					lastCode = myOpencodeInfoMapper.selectOneByLast();
					if (s.endsWith("001")) {// 当天第一期，不减
						String yesterday = sdf.format(DateUtil.addDate(sdf.parse(page), Calendar.DAY_OF_MONTH, -1));
						if (lastCode.getExpect().longValue() == Long.valueOf(yesterday + "120").longValue()) {
							insertData(s, treeMap, lastCode);
						}
					} else {
						if (lastCode.getExpect().longValue() == Long.valueOf("20" + (Long.parseLong(s) - 1))
								.longValue()) {
							insertData(s, treeMap, lastCode);
						} else {
							if (lastCode.getExpect().longValue() < Long.valueOf("20" + (Long.parseLong(s) - 1))
									.longValue()) {
								insertData(s, treeMap, lastCode);
							}
						}
					}

				}
			}
		}
	}

	private void insertData(String period, Map<String, String> treeMap, MyOpencodeInfo validCode) {

		lastOddGapNum = validCode.getLastOddGap();
		lastEvenGapNum = validCode.getLastEvenGap();
		lastBigGapNum = validCode.getLastBigGap();
		lastSmallGapNum = validCode.getLastSmallGap();
		lastEvenOddGapNum = validCode.getLastEvenOddGap();
		lastBigSmallGapNum = validCode.getLastBigSmallGap();
		preLastEvenOddFlag = validCode.getLastcode() % 2 != 0 ? true : false;
		preLastBigSmallFlag = validCode.getLastcode() >= 5 ? true : false;
		fourthOddGapNum = validCode.getFourthOddGap();
		fourthEvenGapNum = validCode.getFourthEvenGap();
		fourthBigGapNum = validCode.getFourthBigGap();
		fourthSmallGapNum = validCode.getFourthSmallGap();
		fourthEvenOddGapNum = validCode.getFourthEvenOddGap();
		fourthBigSmallGapNum = validCode.getFourthBigSmallGap();
		preFourthEvenOddFlag = validCode.getFourthcode() % 2 != 0 ? true : false;
		preFourthBigSmallFlag = validCode.getFourthcode() >= 5 ? true : false;

		String winNumber = treeMap.get(period);
		MyOpencodeInfo myOpencodeInfo = new MyOpencodeInfo();
		myOpencodeInfo.setExpect(Long.valueOf("20" + period));
		myOpencodeInfo.setOpendate(Long.valueOf("20" + period.substring(0, 6)));
		myOpencodeInfo.setOpencode(winNumber);
		myOpencodeInfo.setFirstcode(Integer.valueOf(winNumber.substring(0, 1)));
		myOpencodeInfo.setSecondcode(Integer.valueOf(winNumber.substring(1, 2)));
		myOpencodeInfo.setThirdcode(Integer.valueOf(winNumber.substring(2, 3)));
		int fourthcode = Integer.valueOf(winNumber.substring(3, 4));
		int lastcode = Integer.valueOf(winNumber.substring(4, 5));
		myOpencodeInfo.setFourthcode(fourthcode);
		myOpencodeInfo.setLastcode(lastcode);
		myOpencodeInfo.setCreateTime(new Date());
		myOpencodeInfo.setModifyTime(new Date());

		String lastcodeStr = "," + lastcode + ",";
		if (oddStr.indexOf(lastcodeStr) >= 0) {// 个位奇数连续
			lastOddGapNum++;
			myOpencodeInfo.setLastOddGap(lastOddGapNum);
			if (preLastEvenOddFlag) {// 上期是奇数
				myOpencodeInfo.setLastEvenGap(lastEvenGapNum);
				lastEvenGapNum = 0;
			} else {
				lastEvenGapNum = 0;
				myOpencodeInfo.setLastEvenGap(lastEvenGapNum);
			}

		} else {// 个位偶数连续
			lastEvenGapNum++;
			myOpencodeInfo.setLastEvenGap(lastEvenGapNum);
			if (!preLastEvenOddFlag) {
				myOpencodeInfo.setLastOddGap(lastOddGapNum);
				lastOddGapNum = 0;
			} else {
				lastOddGapNum = 0;
				myOpencodeInfo.setLastOddGap(lastOddGapNum);
			}

		}
		if (bigStr.indexOf(lastcodeStr) >= 0) {// 个位大数连续
			lastBigGapNum++;
			myOpencodeInfo.setLastBigGap(lastBigGapNum);
			if (preLastBigSmallFlag) {
				myOpencodeInfo.setLastSmallGap(lastSmallGapNum);
				lastSmallGapNum = 0;
			} else {
				lastSmallGapNum = 0;
				myOpencodeInfo.setLastSmallGap(lastSmallGapNum);
			}

		} else {// 个位小数连续
			lastSmallGapNum++;
			myOpencodeInfo.setLastSmallGap(lastSmallGapNum);
			if (!preLastBigSmallFlag) {
				myOpencodeInfo.setLastBigGap(lastBigGapNum);
				lastBigGapNum = 0;
			} else {
				lastBigGapNum = 0;
				myOpencodeInfo.setLastBigGap(lastBigGapNum);
			}
		}
		if (preLastEvenOddFlag != oddStr.indexOf(lastcodeStr) >= 0) {// 个位奇偶奇偶连续
			lastEvenOddGapNum++;
			myOpencodeInfo.setLastEvenOddGap(lastEvenOddGapNum);
		} else {
			lastEvenOddGapNum = 0;
			myOpencodeInfo.setLastEvenOddGap(lastEvenOddGapNum);
		}
		if (oddStr.indexOf(lastcodeStr) >= 0) {
			preLastEvenOddFlag = true;
		} else {
			preLastEvenOddFlag = false;
		}

		if (preLastBigSmallFlag != bigStr.indexOf(lastcodeStr) >= 0) {// 个位大小大小连续
			lastBigSmallGapNum++;
			myOpencodeInfo.setLastBigSmallGap(lastBigSmallGapNum);
		} else {
			lastBigSmallGapNum = 0;
			myOpencodeInfo.setLastBigSmallGap(lastBigSmallGapNum);
		}

		if (bigStr.indexOf(lastcodeStr) >= 0) {
			preLastBigSmallFlag = true;
		} else {
			preLastBigSmallFlag = false;
		}

		String fourthcodeStr = "," + fourthcode + ",";
		if (oddStr.indexOf(fourthcodeStr) >= 0) {// 十位奇数连续
			fourthOddGapNum++;
			myOpencodeInfo.setFourthOddGap(fourthOddGapNum);
			if (preFourthEvenOddFlag) {// 上期是奇数
				myOpencodeInfo.setFourthEvenGap(fourthEvenGapNum);
				fourthEvenGapNum = 0;
			} else {
				fourthEvenGapNum = 0;
				myOpencodeInfo.setFourthEvenGap(fourthEvenGapNum);
			}
		} else {// 十位偶数连续
			fourthEvenGapNum++;
			myOpencodeInfo.setFourthEvenGap(fourthEvenGapNum);
			if (!preFourthEvenOddFlag) {
				myOpencodeInfo.setFourthOddGap(fourthOddGapNum);
				fourthOddGapNum = 0;
			} else {
				fourthOddGapNum = 0;
				myOpencodeInfo.setFourthOddGap(fourthOddGapNum);
			}
		}
		if (bigStr.indexOf(fourthcodeStr) >= 0) {// 十位大数连续
			fourthBigGapNum++;
			myOpencodeInfo.setFourthBigGap(fourthBigGapNum);
			if (preFourthBigSmallFlag) {// 上期是大数
				myOpencodeInfo.setFourthSmallGap(fourthSmallGapNum);
				fourthSmallGapNum = 0;
			} else {
				fourthSmallGapNum = 0;
				myOpencodeInfo.setFourthSmallGap(fourthSmallGapNum);
			}
		} else {
			fourthSmallGapNum++;
			myOpencodeInfo.setFourthSmallGap(fourthSmallGapNum);
			if (!preFourthBigSmallFlag) {
				myOpencodeInfo.setFourthBigGap(fourthBigGapNum);
				fourthBigGapNum = 0;
			} else {
				fourthBigGapNum = 0;
				myOpencodeInfo.setFourthBigGap(fourthBigGapNum);
			}
		}

		if (preFourthEvenOddFlag != oddStr.indexOf(fourthcodeStr) >= 0) {// 十位奇偶连续
			fourthEvenOddGapNum++;
			myOpencodeInfo.setFourthEvenOddGap(fourthEvenOddGapNum);
		} else {
			fourthEvenOddGapNum = 0;
			myOpencodeInfo.setFourthEvenOddGap(fourthEvenOddGapNum);
		}

		if (oddStr.indexOf(fourthcodeStr) >= 0) {
			preFourthEvenOddFlag = true;
		} else {
			preFourthEvenOddFlag = false;
		}

		if (preFourthBigSmallFlag != bigStr.indexOf(fourthcodeStr) >= 0) {// 十位大小连续
			fourthBigSmallGapNum++;
			myOpencodeInfo.setFourthBigSmallGap(fourthBigSmallGapNum);
		} else {
			fourthBigSmallGapNum = 0;
			myOpencodeInfo.setFourthBigSmallGap(fourthBigSmallGapNum);
		}

		if (bigStr.indexOf(fourthcodeStr) >= 0) {
			preFourthBigSmallFlag = true;
		} else {
			preFourthBigSmallFlag = false;
		}
		String mesg = "";
		if (lastOddGapNum >= Integer.valueOf(PropertiesUtil.get("conf.lastOddGapNumFlag"))) {
			mesg += "个位单数已连续:" + lastOddGapNum + "\n";
		}
		if (lastEvenGapNum >= Integer.valueOf(PropertiesUtil.get("conf.lastEvenGapNumFlag"))) {
			mesg += "个位双数已连续:" + lastEvenGapNum + "\n";
		}
		if (lastBigGapNum >= Integer.valueOf(PropertiesUtil.get("conf.lastBigGapNumFlag"))) {
			mesg += "个位大数已连续:" + lastBigGapNum + "\n";
		}
		if (lastSmallGapNum >= Integer.valueOf(PropertiesUtil.get("conf.lastSmallGapNumFlag"))) {
			mesg += "个位小数已连续:" + lastSmallGapNum + "\n";
		}
		if (lastEvenOddGapNum >= Integer.valueOf(PropertiesUtil.get("conf.lastEvenOddGapNumFlag"))) {
			mesg += "个位单双已连续:" + lastEvenOddGapNum + "\n";
		}
		if (lastBigSmallGapNum >= Integer.valueOf(PropertiesUtil.get("conf.lastBigSmallGapNumFlag"))) {
			mesg += "个位大小已连续:" + lastBigSmallGapNum + "\n";
		}
		if (fourthOddGapNum >= Integer.valueOf(PropertiesUtil.get("conf.fourthOddGapNumFlag"))) {
			mesg += "十位单数已连续:" + fourthOddGapNum + "\n";
		}
		if (fourthEvenGapNum >= Integer.valueOf(PropertiesUtil.get("conf.fourthEvenGapNumFlag"))) {
			mesg += "十位双数已连续:" + fourthEvenGapNum + "\n";
		}
		if (fourthBigGapNum >= Integer.valueOf(PropertiesUtil.get("conf.fourthBigGapNumFlag"))) {
			mesg += "十位大数已连续:" + fourthBigGapNum + "\n";
		}
		if (fourthSmallGapNum >= Integer.valueOf(PropertiesUtil.get("conf.fourthSmallGapNumFlag"))) {
			mesg += "十位小数已连续:" + fourthSmallGapNum + "\n";
		}
		if (fourthEvenOddGapNum >= Integer.valueOf(PropertiesUtil.get("conf.fourthEvenOddGapNumFlag"))) {
			mesg += "十位单双已连续:" + fourthEvenOddGapNum + "\n";
		}
		if (fourthBigSmallGapNum >= Integer.valueOf(PropertiesUtil.get("conf.fourthBigSmallGapNumFlag"))) {
			mesg += "十位大小已连续:" + fourthBigSmallGapNum + "\n";
		}
		if(StringUtils.isNotEmpty(mesg)){
			mesg = "第20" + period +"期：\n" + mesg;
			sendEmail(mesg);
		}
		myOpencodeInfoMapper.insertSelective(myOpencodeInfo);
	}

	private void sendEmail(String mesg) {
		try{
			Map<String, String> map = new HashMap<String, String>();
			MailUtil mail = new MailUtil("891620702@qq.com", "ospkpxjrsqyubejh");
			map.put("mail.smtp.host", "smtp.qq.com");
			map.put("mail.smtp.auth", "true");
			map.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			map.put("mail.smtp.port", "465");
			map.put("mail.smtp.socketFactory.port", "465");
			mail.setPros(map);
			mail.initMessage();
			/*
			 * 添加收件人有三种方法： 1,单人添加(单人发送)调用setRecipient(str);发送String类型
			 * 2,多人添加(群发)调用setRecipients(list);发送list集合类型
			 * 3,多人添加(群发)调用setRecipients(sb);发送StringBuffer类型
			 */
			List<String> list = new ArrayList<String>();
			list.add("leipp@zssc.com");
			list.add("ivekes@sina.com");
			mail.setRecipients(list);
			mail.setSubject("重庆时时彩提醒");
			mail.setDate(new Date());
			mail.setFrom("ivekes");
			mail.setContent(mesg + "\n敬请关注", "text/html; charset=UTF-8");
			System.out.println(mail.sendMessage());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date start = sdf.parse(20170306 + "");
		Date end = sdf.parse(sdf.format(new Date()));
		System.out.println(start.getTime() <= end.getTime());
	}

}
