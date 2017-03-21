package com.wzk.mvc.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * 
 * @author zk
 *
 */
public final class PropertiesUtil {
	public static String get(String key) {
		try {
			Properties prop = PropertiesLoaderUtils.loadAllProperties("/properties/conf.properties");
			return prop.getProperty(key, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
