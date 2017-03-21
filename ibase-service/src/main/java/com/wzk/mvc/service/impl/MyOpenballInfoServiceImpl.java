package com.wzk.mvc.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wzk.mvc.dao.MyOpenballInfoMapper;
import com.wzk.mvc.model.MyOpenballInfo;
import com.wzk.mvc.service.IMyOpenballInfoService;
import com.wzk.mvc.util.DateUtil;
import com.wzk.mvc.util.PropertiesUtil;
import com.wzk.mvc.util.StringUtils;

@Service
public class MyOpenballInfoServiceImpl implements IMyOpenballInfoService{
	
	String oddStr = ",1,3,5,7,9,";// 单数
	String bigStr = ",5,6,7,8,9,";// 大数
	String primeStr = ",1,2,3,5,7,";// 质数
	
	int lastOddGapNum = 0;// 个位单数连续
	int lastEvenGapNum = 1;// 个位双数连续
	int lastBigGapNum = 2;// 个位大数连续
	int lastSmallGapNum = 0;// 个位小数连续
	int lastPrimeGapNum = 0;//个位质数间隔
	int lastCompositeGapNum = 0;//个位合数间隔
	int lastEvenOddGapNum = 1;// 个位单双连续
	int lastBigSmallGapNum = 0;// 个位大小连续
	boolean preLastEvenOddFlag = true;// 个位单双连续标识 上期单：true 双：false
	boolean preLastBigSmallFlag = true;// 个位大小连续标识 上期大：true 小：false
	boolean preLastPrimeCompositeFlag = true;// 个位质合连续标识 上期质：true 合：false
	int fourthOddGapNum = 0;// 十位单数连续
	int fourthEvenGapNum = 1;// 十位双数连续
	int fourthBigGapNum = 1;// 十位大数连续
	int fourthSmallGapNum = 0;// 十位小数连续
	int fourthPrimeGapNum = 0;//十位质数间隔
	int fourthCompositeGapNum = 0;//十位合数间隔
	int fourthEvenOddGapNum = 1;// 十位单双连续
	int fourthBigSmallGapNum = 7;// 十位大小连续
	boolean preFourthEvenOddFlag = true;// 十位单双连续标识 上期单：true 双：false
	boolean preFourthBigSmallFlag = true;// 十位大小连续标识 上期大：true 小：false
	boolean preFourthPrimeCompositeFlag = true;// 十位质合连续标识 上期质：true 合：false
	
	int bigBigGapNum = 0;//大大间隔
	int bigSmallGapNum = 0;//大小间隔
	int bigOddGapNum = 0;//大单间隔
	int bigEvenGapNum = 0;//大双间隔

	int smallBigGapNum = 0;//小大间隔
	int smallSmallGapNum = 0;//小小间隔
	int smallOddGapNum = 0;//小单间隔
	int smallEvenGapNum = 0;//小双间隔
	
	int oddBigGapNum = 0;//单大间隔
	int oddSmallGapNum = 0;//单小间隔
	int oddOddGapNum = 0;//单单间隔
	int oddEvenGapNum = 0;//单双间隔
	
	int evenBigGapNum = 0;//单大间隔
	int evenSmallGapNum = 0;//单小间隔
	int evenOddGapNum = 0;//单单间隔
	int evenEvenGapNum = 0;//单双间隔
	
	int lastZeroGapNum = 0;//个0间隔
	int lastOneGapNum = 0;//个1间隔
	int lastTwoGapNum = 0;//个2间隔
	int lastThreeGapNum = 0;//个3间隔
	int lastFourGapNum = 0;//个4间隔
	int lastFiveGapNum = 0;//个5间隔
	int lastSixGapNum = 0;//个6间隔
	int lastSevenGapNum = 0;//个7间隔
	int lastEightGapNum = 0;//个8间隔
	int lastNineGapNum = 0;//个9间隔
	
	int spanNineGapNum = 0;//跨度9间隔
	int roadZeroGapNum = 0;//0路间隔
	int roadOneGapNum = 0;//1路间隔
	int roadTwoGapNum = 0;//2路间隔
	
	int zeroZeroGapNum = 0;//00间隔
	int oneOneGapNum = 0;//11间隔
	int twoTwoGapNum = 0;//22路间隔
	int threeThreeGapNum = 0;//33路间隔
	int fourFourGapNum = 0;//44间隔
	int fiveFiveGapNum = 0;//55间隔
	int sixSixGapNum = 0;//66路间隔
	int sevenSevenGapNum = 0;//77路间隔
	int eightEightGapNum = 0;//88路间隔
	int nineNineGapNum = 0;//99路间隔

	MyOpenballInfo lastCode = null;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	private MyOpenballInfoMapper myOpenballInfoMapper;
	
	@Override
	public void insertdata() {
		try {
			lastCode = myOpenballInfoMapper.selectOneByLast();
			
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
					getData(sdf.format(start));
				}
				start = DateUtil.addDate(start, Calendar.DAY_OF_MONTH, 1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getData(String page) throws IOException, ParseException {

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
					lastCode = myOpenballInfoMapper.selectOneByLast();
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
	private void insertData(String period, Map<String, String> treeMap, MyOpenballInfo validCode) {

		lastOddGapNum = validCode.getLastOddGap();
		lastEvenGapNum = validCode.getLastEvenGap();
		lastBigGapNum = validCode.getLastBigGap();
		lastSmallGapNum = validCode.getLastSmallGap();
		lastEvenOddGapNum = validCode.getLastEvenOddGap();
		lastBigSmallGapNum = validCode.getLastBigSmallGap();
		lastPrimeGapNum = validCode.getLastPrimeGap();
		lastCompositeGapNum = validCode.getLastCompositeGap();
		preLastEvenOddFlag = validCode.getLastcode() % 2 != 0 ? true : false;
		preLastBigSmallFlag = validCode.getLastcode() >= 5 ? true : false;
		preLastPrimeCompositeFlag = primeStr.indexOf(validCode.getLastcode()+"")>0?true:false;
		
		fourthOddGapNum = validCode.getFourthOddGap();
		fourthEvenGapNum = validCode.getFourthEvenGap();
		fourthBigGapNum = validCode.getFourthBigGap();
		fourthSmallGapNum = validCode.getFourthSmallGap();
		fourthEvenOddGapNum = validCode.getFourthEvenOddGap();
		fourthBigSmallGapNum = validCode.getFourthBigSmallGap();
		fourthPrimeGapNum = validCode.getFourthPrimeGap();
		fourthCompositeGapNum = validCode.getFourthCompositeGap();
		preFourthEvenOddFlag = validCode.getFourthcode() % 2 != 0 ? true : false;
		preFourthBigSmallFlag = validCode.getFourthcode() >= 5 ? true : false;
		preFourthPrimeCompositeFlag = primeStr.indexOf(validCode.getFourthcode()+"")>0?true:false;
		
		
		bigBigGapNum = validCode.getBigBigGap();
		bigSmallGapNum = validCode.getBigSmallGap();
		bigOddGapNum = validCode.getBigOddGap();
		bigEvenGapNum = validCode.getBigEvenGap();

		smallBigGapNum = validCode.getSmallBigGap();
		smallSmallGapNum = validCode.getSmallSmallGap();
		smallOddGapNum = validCode.getSmallOddGap();
		smallEvenGapNum = validCode.getSmallEvenGap();
		
		oddBigGapNum = validCode.getOddBigGap();
		oddSmallGapNum = validCode.getOddSmallGap();
		oddOddGapNum = validCode.getOddOddGap();
		oddEvenGapNum = validCode.getOddEvenGap();
		
		evenBigGapNum = validCode.getEvenBigGap();
		evenSmallGapNum = validCode.getEvenSmallGap();
		evenOddGapNum = validCode.getEvenOddGap();
		evenEvenGapNum = validCode.getEvenEvenGap();
		
		lastZeroGapNum = validCode.getLastZeroGap();
		lastOneGapNum = validCode.getLastOneGap();
		lastTwoGapNum = validCode.getLastTwoGap();
		lastThreeGapNum = validCode.getLastThreeGap();
		lastFourGapNum = validCode.getLastFourGap();
		lastFiveGapNum = validCode.getLastFiveGap();
		lastSixGapNum = validCode.getLastSixGap();
		lastSevenGapNum = validCode.getLastSevenGap();
		lastEightGapNum = validCode.getLastEightGap();
		lastNineGapNum = validCode.getLastNineGap();
		
		spanNineGapNum = validCode.getSpanNineGap();
		roadZeroGapNum = validCode.getRoadZeroGap();
		roadOneGapNum = validCode.getRoadOneGap();
		roadTwoGapNum = validCode.getRoadTwoGap();

		zeroZeroGapNum = validCode.getZeroZeroGap();
		oneOneGapNum = validCode.getOneOneGap();
		twoTwoGapNum = validCode.getTwoTwoGap();
		threeThreeGapNum = validCode.getThreeThreeGap();
		fourFourGapNum = validCode.getFourFourGap();
		fiveFiveGapNum = validCode.getFiveFiveGap();
		sixSixGapNum = validCode.getSixSixGap();
		sevenSevenGapNum = validCode.getSevenSevenGap();
		eightEightGapNum = validCode.getEightEightGap();
		nineNineGapNum = validCode.getNineNineGap();
		
		String winNumber = treeMap.get(period);
		MyOpenballInfo myOpencodeInfo = new MyOpenballInfo();
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
		if (oddStr.indexOf(lastcodeStr) >= 0) {// 个位单数连续
			lastOddGapNum++;
			myOpencodeInfo.setLastOddGap(lastOddGapNum);
			if (preLastEvenOddFlag) {// 上期是单数
				myOpencodeInfo.setLastEvenGap(lastEvenGapNum);
				lastEvenGapNum = 0;
			} else {
				lastEvenGapNum = 0;
				myOpencodeInfo.setLastEvenGap(lastEvenGapNum);
			}

		} else {// 个位双数连续
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
		
		if (primeStr.indexOf(lastcodeStr) >= 0) {// 个位质数连续
			lastPrimeGapNum++;
			myOpencodeInfo.setLastPrimeGap(lastPrimeGapNum);
			if (preLastPrimeCompositeFlag) {
				myOpencodeInfo.setLastCompositeGap(lastCompositeGapNum);
				lastCompositeGapNum = 0;
			} else {
				lastCompositeGapNum = 0;
				myOpencodeInfo.setLastCompositeGap(lastCompositeGapNum);
			}

		} else {// 个位合数连续
			lastCompositeGapNum++;
			myOpencodeInfo.setLastCompositeGap(lastCompositeGapNum);
			if (!preLastPrimeCompositeFlag) {
				myOpencodeInfo.setLastPrimeGap(lastPrimeGapNum);
				lastPrimeGapNum = 0;
			} else {
				lastPrimeGapNum = 0;
				myOpencodeInfo.setLastPrimeGap(lastPrimeGapNum);
			}
		}
		
		if (primeStr.indexOf(lastcodeStr) >= 0) {
			preLastPrimeCompositeFlag = true;
		} else {
			preLastPrimeCompositeFlag = false;
		}
		
		if (preLastEvenOddFlag != oddStr.indexOf(lastcodeStr) >= 0) {// 个位单双单双连续
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
		if (oddStr.indexOf(fourthcodeStr) >= 0) {// 十位单数连续
			fourthOddGapNum++;
			myOpencodeInfo.setFourthOddGap(fourthOddGapNum);
			if (preFourthEvenOddFlag) {// 上期是单数
				myOpencodeInfo.setFourthEvenGap(fourthEvenGapNum);
				fourthEvenGapNum = 0;
			} else {
				fourthEvenGapNum = 0;
				myOpencodeInfo.setFourthEvenGap(fourthEvenGapNum);
			}
		} else {// 十位双数连续
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
		
		
		if (primeStr.indexOf(fourthcodeStr) >= 0) {// 十位质数连续
			fourthPrimeGapNum++;
			myOpencodeInfo.setFourthPrimeGap(fourthPrimeGapNum);
			if (preFourthPrimeCompositeFlag) {// 上期是大数
				myOpencodeInfo.setFourthCompositeGap(fourthCompositeGapNum);
				fourthCompositeGapNum = 0;
			} else {
				fourthCompositeGapNum = 0;
				myOpencodeInfo.setFourthCompositeGap(fourthCompositeGapNum);
			}
		} else {
			fourthCompositeGapNum++;
			myOpencodeInfo.setFourthCompositeGap(fourthCompositeGapNum);
			if (!preFourthPrimeCompositeFlag) {
				myOpencodeInfo.setFourthPrimeGap(fourthPrimeGapNum);
				fourthPrimeGapNum = 0;
			} else {
				fourthPrimeGapNum = 0;
				myOpencodeInfo.setFourthPrimeGap(fourthPrimeGapNum);
			}
		}
		
		if (primeStr.indexOf(fourthcodeStr) >= 0) {
			preFourthPrimeCompositeFlag = true;
		} else {
			preFourthPrimeCompositeFlag = false;
		}

		if (preFourthEvenOddFlag != oddStr.indexOf(fourthcodeStr) >= 0) {// 十位单双连续
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
		
		if(fourthcode > 4 && lastcode > 4){//大大间隔
			bigBigGapNum = 0;
			myOpencodeInfo.setBigBigGap(bigBigGapNum);
		}else{
			bigBigGapNum++;
			myOpencodeInfo.setBigBigGap(bigBigGapNum);
		}
		if(fourthcode > 4 && lastcode <= 4){//大小间隔
			bigSmallGapNum = 0;
			myOpencodeInfo.setBigSmallGap(bigSmallGapNum);
		}else{
			bigSmallGapNum++;
			myOpencodeInfo.setBigSmallGap(bigSmallGapNum);
		}
		if(fourthcode > 4 && lastcode % 2 != 0){//大单间隔
			bigOddGapNum = 0;
			myOpencodeInfo.setBigOddGap(bigOddGapNum);
		}else{
			bigOddGapNum++;
			myOpencodeInfo.setBigOddGap(bigOddGapNum);
		}
		
		if(fourthcode > 4 && lastcode % 2 == 0){//大双间隔
			bigEvenGapNum = 0;
			myOpencodeInfo.setBigEvenGap(bigEvenGapNum);
		}else{
			bigEvenGapNum++;
			myOpencodeInfo.setBigEvenGap(bigEvenGapNum);
		}
		
		if(fourthcode <= 4 && lastcode > 4){//小大间隔
			smallBigGapNum = 0;
			myOpencodeInfo.setSmallBigGap(smallBigGapNum);
		}else{
			smallBigGapNum++;
			myOpencodeInfo.setSmallBigGap(smallBigGapNum);
		}
		if(fourthcode <= 4 && lastcode <= 4){//小小间隔
			smallSmallGapNum = 0;
			myOpencodeInfo.setSmallSmallGap(smallSmallGapNum);
		}else{
			smallSmallGapNum++;
			myOpencodeInfo.setSmallSmallGap(smallSmallGapNum);
		}
		if(fourthcode <= 4 && lastcode % 2 != 0){//小单间隔
			smallOddGapNum = 0;
			myOpencodeInfo.setSmallOddGap(smallOddGapNum);
		}else{
			smallOddGapNum++;
			myOpencodeInfo.setSmallOddGap(smallOddGapNum);
		}
		
		if(fourthcode <= 4 && lastcode % 2 == 0){//小双间隔
			smallEvenGapNum = 0;
			myOpencodeInfo.setSmallEvenGap(smallEvenGapNum);
		}else{
			smallEvenGapNum++;
			myOpencodeInfo.setSmallEvenGap(smallEvenGapNum);
		}
		
		if(fourthcode % 2 != 0 && lastcode > 4){//单大间隔
			oddBigGapNum = 0;
			myOpencodeInfo.setOddBigGap(oddBigGapNum);
		}else{
			oddBigGapNum++;
			myOpencodeInfo.setOddBigGap(oddBigGapNum);
		}
		if(fourthcode % 2 != 0 && lastcode <= 4){//单小间隔
			oddSmallGapNum = 0;
			myOpencodeInfo.setOddSmallGap(oddSmallGapNum);
		}else{
			oddSmallGapNum++;
			myOpencodeInfo.setOddSmallGap(oddSmallGapNum);
		}
		
		if(fourthcode % 2 != 0 && lastcode % 2 != 0){//单单间隔
			oddOddGapNum = 0;
			myOpencodeInfo.setOddOddGap(oddOddGapNum);
		}else{
			oddOddGapNum++;
			myOpencodeInfo.setOddOddGap(oddOddGapNum);
		}
		
		if(fourthcode % 2 != 0 && lastcode % 2 == 0){//单双间隔
			oddEvenGapNum = 0;
			myOpencodeInfo.setOddEvenGap(oddEvenGapNum);
		}else{
			oddEvenGapNum++;
			myOpencodeInfo.setOddEvenGap(oddEvenGapNum);
		}
		
		if(fourthcode % 2 == 0 && lastcode > 4){//双大间隔
			evenBigGapNum = 0;
			myOpencodeInfo.setEvenBigGap(evenBigGapNum);
		}else{
			evenBigGapNum++;
			myOpencodeInfo.setEvenBigGap(evenBigGapNum);
		}
		if(fourthcode % 2 == 0 && lastcode <= 4){//双小间隔
			evenSmallGapNum = 0;
			myOpencodeInfo.setEvenSmallGap(evenSmallGapNum);
		}else{
			evenSmallGapNum++;
			myOpencodeInfo.setEvenSmallGap(evenSmallGapNum);
		}
		
		if(fourthcode % 2 == 0 && lastcode % 2 != 0){//双单间隔
			evenOddGapNum = 0;
			myOpencodeInfo.setEvenOddGap(evenOddGapNum);
		}else{
			evenOddGapNum++;
			myOpencodeInfo.setEvenOddGap(evenOddGapNum);
		}
		
		if(fourthcode % 2 == 0 && lastcode % 2 == 0){//双双间隔
			evenEvenGapNum = 0;
			myOpencodeInfo.setEvenEvenGap(evenEvenGapNum);
		}else{
			evenEvenGapNum++;
			myOpencodeInfo.setEvenEvenGap(evenEvenGapNum);
		}
		
		if(lastcode == 0){
			lastZeroGapNum = 0;
			myOpencodeInfo.setLastZeroGap(lastZeroGapNum);
		}else{
			lastZeroGapNum++;
			myOpencodeInfo.setLastZeroGap(lastZeroGapNum);
		}
		if(lastcode == 1){
			lastOneGapNum = 0;
			myOpencodeInfo.setLastOneGap(lastOneGapNum);
		}else{
			lastOneGapNum++;
			myOpencodeInfo.setLastOneGap(lastOneGapNum);
		}
		
		if(lastcode == 2){
			lastTwoGapNum = 0;
			myOpencodeInfo.setLastTwoGap(lastTwoGapNum);
		}else{
			lastTwoGapNum++;
			myOpencodeInfo.setLastTwoGap(lastTwoGapNum);
		}
		
		if(lastcode == 3){
			lastThreeGapNum = 0;
			myOpencodeInfo.setLastThreeGap(lastThreeGapNum);
		}else{
			lastThreeGapNum++;
			myOpencodeInfo.setLastThreeGap(lastThreeGapNum);
		}
		
		if(lastcode == 4){
			lastFourGapNum = 0;
			myOpencodeInfo.setLastFourGap(lastFourGapNum);
		}else{
			lastFourGapNum++;
			myOpencodeInfo.setLastFourGap(lastFourGapNum);
		}
		
		if(lastcode == 5){
			lastFiveGapNum = 0;
			myOpencodeInfo.setLastFiveGap(lastFiveGapNum);
		}else{
			lastFiveGapNum++;
			myOpencodeInfo.setLastFiveGap(lastFiveGapNum);
		}
		if(lastcode == 6){
			lastSixGapNum = 0;
			myOpencodeInfo.setLastSixGap(lastSixGapNum);
		}else{
			lastSixGapNum++;
			myOpencodeInfo.setLastSixGap(lastSixGapNum);
		}
		if(lastcode == 7){
			lastSevenGapNum = 0;
			myOpencodeInfo.setLastSevenGap(lastSevenGapNum);
		}else{
			lastSevenGapNum++;
			myOpencodeInfo.setLastSevenGap(lastSevenGapNum);
		}
		if(lastcode == 8){
			lastEightGapNum = 0;
			myOpencodeInfo.setLastEightGap(lastEightGapNum);
		}else{
			lastEightGapNum++;
			myOpencodeInfo.setLastEightGap(lastEightGapNum);
		}
		if(lastcode == 9){
			lastNineGapNum = 0;
			myOpencodeInfo.setLastNineGap(lastNineGapNum);
		}else{
			lastNineGapNum++;
			myOpencodeInfo.setLastNineGap(lastNineGapNum);
		}
		if(Math.abs(lastcode-validCode.getLastcode())==9){
			spanNineGapNum = 0;
			myOpencodeInfo.setSpanNineGap(spanNineGapNum);
		}else{
			spanNineGapNum++;
			myOpencodeInfo.setSpanNineGap(spanNineGapNum);
		}
		if(lastcode%3 == 0){
			roadZeroGapNum = 0;
			myOpencodeInfo.setRoadZeroGap(roadZeroGapNum);
		}else{
			roadZeroGapNum++;
			myOpencodeInfo.setRoadZeroGap(roadZeroGapNum);
		}
		if(lastcode%3 == 1){
			roadOneGapNum = 0;
			myOpencodeInfo.setRoadOneGap(roadOneGapNum);
		}else{
			roadOneGapNum++;
			myOpencodeInfo.setRoadOneGap(roadOneGapNum);
		}
		if(lastcode%3 == 2){
			roadTwoGapNum = 0;
			myOpencodeInfo.setRoadTwoGap(roadTwoGapNum);
		}else{
			roadTwoGapNum++;
			myOpencodeInfo.setRoadTwoGap(roadTwoGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 0){
			zeroZeroGapNum = 0;
			myOpencodeInfo.setZeroZeroGap(zeroZeroGapNum);
		}else{
			zeroZeroGapNum++;
			myOpencodeInfo.setZeroZeroGap(zeroZeroGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 1){
			oneOneGapNum = 0;
			myOpencodeInfo.setOneOneGap(oneOneGapNum);
		}else{
			oneOneGapNum++;
			myOpencodeInfo.setOneOneGap(oneOneGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 2){
			twoTwoGapNum = 0;
			myOpencodeInfo.setTwoTwoGap(twoTwoGapNum);
		}else{
			twoTwoGapNum++;
			myOpencodeInfo.setTwoTwoGap(twoTwoGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 3){
			threeThreeGapNum = 0;
			myOpencodeInfo.setThreeThreeGap(threeThreeGapNum);
		}else{
			threeThreeGapNum++;
			myOpencodeInfo.setThreeThreeGap(threeThreeGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 4){
			fourFourGapNum = 0;
			myOpencodeInfo.setFourFourGap(fourFourGapNum);
		}else{
			fourFourGapNum++;
			myOpencodeInfo.setFourFourGap(fourFourGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 5){
			fiveFiveGapNum = 0;
			myOpencodeInfo.setFiveFiveGap(fiveFiveGapNum);
		}else{
			fiveFiveGapNum++;
			myOpencodeInfo.setFiveFiveGap(fiveFiveGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 6){
			sixSixGapNum = 0;
			myOpencodeInfo.setSixSixGap(sixSixGapNum);
		}else{
			sixSixGapNum++;
			myOpencodeInfo.setSixSixGap(sixSixGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 7){
			sevenSevenGapNum = 0;
			myOpencodeInfo.setSevenSevenGap(sevenSevenGapNum);
		}else{
			sevenSevenGapNum++;
			myOpencodeInfo.setSevenSevenGap(sevenSevenGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 8){
			eightEightGapNum = 0;
			myOpencodeInfo.setEightEightGap(eightEightGapNum);
		}else{
			eightEightGapNum++;
			myOpencodeInfo.setEightEightGap(eightEightGapNum);
		}
		
		if(lastcode == fourthcode && lastcode == 9){
			nineNineGapNum = 0;
			myOpencodeInfo.setNineNineGap(nineNineGapNum);
		}else{
			nineNineGapNum++;
			myOpencodeInfo.setNineNineGap(nineNineGapNum);
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
		if(bigBigGapNum >= 12){
			mesg += "大大已间隔:" + bigBigGapNum + "\n";
		}
		if(bigSmallGapNum >= 12){
			mesg += "大小已间隔:" + bigSmallGapNum + "\n";
		}
		if(bigOddGapNum >= 12){
			mesg += "大单已间隔:" + bigOddGapNum + "\n";
		}
		if(bigEvenGapNum >= 12){
			mesg += "大双已间隔:" + bigEvenGapNum + "\n";
		}
		if(smallBigGapNum >= 12){
			mesg += "小大已间隔:" + smallBigGapNum + "\n";
		}
		if(smallSmallGapNum >= 12){
			mesg += "小小已间隔:" + smallSmallGapNum + "\n";
		}
		if(smallOddGapNum >= 12){
			mesg += "小单已间隔:" + smallOddGapNum + "\n";
		}
		if(smallEvenGapNum >= 12){
			mesg += "小双已间隔:" + smallEvenGapNum + "\n";
		}
		if(oddBigGapNum >= 12){
			mesg += "单大已间隔:" + oddBigGapNum + "\n";
		}
		if(oddSmallGapNum >= 12){
			mesg += "单小已间隔:" + oddSmallGapNum + "\n";
		}
		if(oddOddGapNum >= 12){
			mesg += "单单已间隔:" + oddOddGapNum + "\n";
		}
		if(oddEvenGapNum >= 12){
			mesg += "单双已间隔:" + oddEvenGapNum + "\n";
		}
		if(evenBigGapNum >= 12){
			mesg += "双大已间隔:" + evenBigGapNum + "\n";
		}
		if(evenSmallGapNum >= 12){
			mesg += "双小已间隔:" + evenSmallGapNum + "\n";
		}
		if(evenOddGapNum >= 12){
			mesg += "双单已间隔:" + evenOddGapNum + "\n";
		}
		if(evenEvenGapNum >= 12){
			mesg += "双双已间隔:" + evenEvenGapNum + "\n";
		}
		if(roadZeroGapNum >= 9){
			mesg += "o路已间隔:" + roadZeroGapNum + "\n";
		}
		if(roadOneGapNum >= 9){
			mesg += "1路已间隔:" + roadOneGapNum + "\n";
		}
		if(roadTwoGapNum >= 11){
			mesg += "2路已间隔:" + roadTwoGapNum + "\n";
		}
		if(lastPrimeGapNum >= 5){
			mesg += "个位质数已间隔:" + lastPrimeGapNum + "\n";
		}
		if(lastCompositeGapNum >= 5){
			mesg += "个位合数已间隔:" + lastCompositeGapNum + "\n";
		}
		if(fourthPrimeGapNum >= 5){
			mesg += "十位质数已间隔:" + fourthPrimeGapNum + "\n";
		}
		if(fourthCompositeGapNum >= 5){
			mesg += "十位合数已间隔:" + fourthCompositeGapNum + "\n";
		}
		if(StringUtils.isNotEmpty(mesg)){
			mesg = "##########第20" + period +"期##########\n###\t开奖号码：" +winNumber +"\t\t###\n推荐关注：\n"+ mesg;
//			MailUtil.sendEmail(mesg);
		}
		if(StringUtils.isEmpty(mesg)){
			mesg = "##########第20" + period +"期##########\n###\t开奖号码：" +winNumber +"\t\t###\n推荐关注：暂无推荐";
		}
		System.out.println(mesg);
		myOpenballInfoMapper.insertSelective(myOpencodeInfo);
	}

}
