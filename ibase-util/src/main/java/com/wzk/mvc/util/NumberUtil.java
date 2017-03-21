package com.wzk.mvc.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberUtil {

	/**
	 * 最多显示小数点后两位，省略价格末尾为0的位数
	 * @param bg
	 * @return
	 */
	public static BigDecimal formatMoney(BigDecimal bg){
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#.00");
		if(bg == null){
			return new BigDecimal(df.format(0.00));
		}
		return new BigDecimal(df.format(bg));
	}
	/**
	 * 格式化数字
	 * @param f
	 * @return
	 */
	public static String formatNumber(float f){
		DecimalFormat df = new DecimalFormat("0.0");
		return df.format(f);
	}
	
	/**
	 * 格式化数字
	 * @param f
	 * @param p
	 * @return
	 */
	public static String formatNumber(Number f,String p){
		DecimalFormat df = new DecimalFormat(p);
		return df.format(f);
	}
}
