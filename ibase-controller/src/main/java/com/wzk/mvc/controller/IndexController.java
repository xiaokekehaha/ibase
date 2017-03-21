package com.wzk.mvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.wzk.mvc.dao.LogIcecoldmonitorMapper;
import com.wzk.mvc.dto.DataInfo;
import com.wzk.mvc.util.EsUtil;

@Controller
@RequestMapping("/es")
public class IndexController {

	private static Logger log = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	private LogIcecoldmonitorMapper logIcecoldmonitorMapper;

	@RequestMapping("/index")
	@ResponseBody
	public String index(HttpServletRequest request) throws Exception {
		logIcecoldmonitorMapper.hashCode();
		log.debug("4566");
		// int logId = Integer.parseInt(request.getParameter("logId"));
		// LogIcecoldmonitor logIcecoldmonitor =
		// logIcecoldmonitorMapper.selectByPrimaryKey(logId);
		String nameS = request.getParameter("name");
		EsUtil esUtil = EsUtil.getInstance();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("pm_lin", nameS);
		/*List<Map<String,Object>> lm  = new ArrayList<Map<String,Object>>();
		
		List<String[]> dd = ExcelImortUtil.excelToArrayList("H:/商品.xlsx"); 
		for(String[] sa :dd){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("ssp_id", sa[0]);
			map.put("seb_id", sa[1]);
			map.put("pm_id", sa[2]);
			map.put("bm_id", sa[3]);
			map.put("sku_num", sa[4]);
			map.put("cost_price", sa[5]);
			map.put("pm_price", sa[6]);
			map.put("seb_begin", sa[7]);
			map.put("seb_end", sa[8]);
			map.put("state", sa[9]);
			map.put("valid", sa[10]);
			map.put("stocks", sa[11]);
			map.put("realtime_stock", sa[12]);
			map.put("is_recommended", sa[13]);
			map.put("user_only_buy", sa[14]);
			map.put("creater", sa[15]);
			map.put("created", sa[16]);
			map.put("modifier", sa[17]);
			map.put("modified", sa[18]);
			map.put("sm_id", sa[18]);
			map.put("show_type", sa[19]);
			map.put("list_diagram", sa[20]);
			map.put("pm_name", sa[21]);
			map.put("pm_lin", sa[22]);
			lm.add(map);
		}
		paramMap.put("age", "22");
		
		esUtil.prepareBulkRequest("index-demo", "type-demo", lm);*/
		SearchHit[] shs = esUtil.search("index-demo", "type-demo", 0, paramMap);
		List<DataInfo> list = new ArrayList<DataInfo>();
		DataInfo di = null;
		for (int i = 0; i < shs.length; i++) {
			SearchHit hit = shs[i];
			// 将文档中的每一个对象转换json串值
			String json = hit.getSourceAsString();
			// 将json串值转换成对应的实体对象
			di = JSON.parseObject(json,DataInfo.class);
			// 获取对应的高亮域
			Map<String, HighlightField> result = hit.highlightFields();
			// 从设定的高亮域中取得指定域
			HighlightField titleField = result.get("pm_lin");
			// 取得定义的高亮标签
			Text[] titleTexts = titleField.fragments();
			// 为name串值增加自定义的高亮标签
			String name = "";
			for (Text text : titleTexts) {
				name += text;
			}
			// 将追加了高亮标签的串值重新填充到对应的对象
			di.setName(name);
			// 打印高亮标签追加完成后的实体对象
			System.out.println(di);
			list.add(di);
		}
		/*for (SearchHit sh : shs) {
			di = new DataInfo();
			Map<String, Object> map = sh.getSource();
			di.setName(sh.getHighlightFields().get("name").getFragments()[0].toString());
			di.setAge(Integer.parseInt(map.get("age").toString()));
			list.add(di);
		}*/
		return JSON.toJSONString(list);
	}
}
