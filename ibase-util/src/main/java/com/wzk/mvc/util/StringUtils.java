package com.wzk.mvc.util;


/**
 * 字符串操作辅助类
 * @author zk
 *
 */
public class StringUtils {

	public static boolean isEmpty(Object str) {
		return (str == null || "".equals(str));
	}
	
	public static boolean isNotEmpty(Object str) {
		return !isEmpty(str);
	}
}
