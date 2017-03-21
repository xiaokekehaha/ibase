package com.wzk.mvc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSONObject;
/**
 * http工具类
 * @author zk
 *
 */
public class HttpUtils {

	public static final String CODE_TYPE = HTTP.UTF_8;
	public static ExecutorService executor = Executors.newFixedThreadPool(5);

	/**
	 * 同步 post
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doPost(String url, String params,String mesg) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 3000); // 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				5000);
		; // 读取超时
		HttpPost httpPost = new HttpPost(url);
		try {
//			RequestEntity
			HttpEntity httpEntity = new StringEntity(params);
			httpPost.setEntity(httpEntity);//setEntity(new UrlEncodedFormEntity(params, CODE_TYPE));
			String response = httpClient.execute(httpPost,
					new BasicResponseHandler());
			System.out.println("请求" + mesg + "接口返回值：" + response);
			return response;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 * 同步 post
	 * 
	 * @param url
	 * @param content
	 * @return
	 */
	public static String post(String url, String content, String mesg) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setEntity(new StringEntity(content, CODE_TYPE));
			String response = httpClient.execute(httpPost,
					new BasicResponseHandler());
			System.out.println("请求" + mesg + "接口返回值：" + response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 异步 post
	 * 
	 * @param url
	 * @param params
	 */
	/*public static void asyDoPost(final String url,
			final List<NameValuePair> params, final String mesg) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				doPost(url, params, mesg);
			}
		});
	}
*/
	/**
	 * 同步 get
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String get(String reqUrl, String mesg) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(reqUrl);
		try {
			String response = httpClient.execute(get,
					new BasicResponseHandler());
			System.out.println("请求" + mesg + "接口返回值：" + response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
//		HttpURLUtils hq = new HttpURLUtils();
		Map<String ,Object> map = new HashMap<String, Object>();
		Map<String ,Object> map1 = new HashMap<String, Object>();
		Map<String ,Object> map2 = new HashMap<String ,Object>();
		Map<String ,Object> map3 = new HashMap<String ,Object>();
		Map<String ,Object> map4 = new HashMap<String ,Object>();
		List<Map<String ,Object>> list = new ArrayList<Map<String ,Object>>();
		map.put("query", map1);
		map.put("from", 0);
		map.put("size", 1);
		map1.put("bool", map2);
		map3.put("query_string", map4);
		map4.put("default_field", "_all");
		map4.put("query", "errorBCVBVC");
		list.add(map3);
		map2.put("must", list);
		
		String sq = JSONObject.toJSONString(map);
		System.out.println(sq);
//		String sr = hq.doGet("/_search?q=张三");
//		String sr = hq.doGet("indexdemo/_analyze?field=name");
		String sr = doPost("http://192.168.1.222:9200/indexdemo/typedemo/_search?pretty", sq,"测试");
		System.out.println(sr);
	}
}
