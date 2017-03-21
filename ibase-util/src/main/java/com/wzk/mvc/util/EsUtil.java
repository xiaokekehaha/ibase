package com.wzk.mvc.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
/**
 * es2.4工具类
 * 
 * @author zk
 *
 */
public class EsUtil {

	private static EsUtil esUtil = null;
	
	private Client client;

	private EsUtil() {
		Settings settings = Settings.settingsBuilder().put("cluster.name", "mh-es").put("client.transport.sniff", true).put("client.transport.ping_timeout", "10s").build();
		try {
			client = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.251"), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static EsUtil getInstance(){
		if(esUtil == null){
			esUtil = new EsUtil();
			return esUtil;
		}else{
			return esUtil;
		}
	}
	/**
	 * 创建指定索引
	 * 
	 * @param index
	 *            必须全为小写
	 * @param type
	 * @param id
	 * @param paramMap
	 *            key:value ==> columnName:columnValue
	 * @throws IOException
	 */
	public void createOne(String index, String type, String id, Map<String, Object> paramMap) throws IOException {
		if (paramMap != null) {
			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
			for (String key : paramMap.keySet()) {
				contentBuilder.field(key, paramMap.get(key));
			}
			contentBuilder.endObject();
			//需要注意的是不设置.setRefresh(true)第一次建立索引查找不到数据，设置增加服务器压力
			IndexRequestBuilder requestBuilder = client.prepareIndex(index, type, id).setRefresh(true);
			requestBuilder.setSource(contentBuilder).execute().actionGet();
		}
	}

	/**
	 * 删除指定id
	 * 
	 * @param index
	 * @param type
	 * @param id
	 */
	public void deleteOne(String index, String type, String id) {
		client.prepareDelete().setIndex(index).setType(type).setId(id).execute();
	}

	/**
	 * 更新指定id
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param paramMap
	 *            key:value ==> columnName:columnValue
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void updateOneById(String index, String type, String id, Map<String, Object> paramMap) throws IOException, InterruptedException, ExecutionException {
		if (paramMap != null) {

			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
			for (String key : paramMap.keySet()) {
				contentBuilder.field(key, paramMap.get(key));
			}
			contentBuilder.endObject();

			UpdateRequest updateRequest = new UpdateRequest(index, type, id);
			updateRequest.doc(contentBuilder);
			client.update(updateRequest).get();
		}
	}

	/**
	 * 预更新指定id
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param paramMap
	 *            key:value ==> columnName:columnValue
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void prepareUpdateOneById(String index, String type, String id, Map<String, Object> paramMap) throws IOException {
		if (paramMap != null) {

			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
			for (String key : paramMap.keySet()) {
				contentBuilder.field(key, paramMap.get(key));
			}
			contentBuilder.endObject();

			client.prepareUpdate(index, type, id).setDoc(contentBuilder).get();
		}
	}

	/**
	 * 更新指定id（如果id不存在，则创建）
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param paramMap
	 *            key:value ==> columnName:columnValue
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void upsertOneById(String index, String type, String id, Map<String, Object> paramMap) throws IOException, InterruptedException, ExecutionException {
		if (paramMap != null) {
			XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
			for (String key : paramMap.keySet()) {
				contentBuilder.field(key, paramMap.get(key));
			}
			contentBuilder.endObject();
			IndexRequest indexRequest = new IndexRequest(index, type, id).source(contentBuilder);
			UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(contentBuilder).upsert(indexRequest);
			client.update(updateRequest).get();
		}
	}

	/**
	 * 查询单条指定id
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public GetResponse getOneById(String index, String type, String id) {
		GetResponse response = client.prepareGet(index, type, id).get();
		return response;
	}

	/**
	 * 查询多条指定id
	 * 
	 * @param index
	 * @param type
	 * @param paramList
	 * @return
	 */
	public MultiGetResponse getMultiById(String index, String type, List<String> paramList) {
		MultiGetRequestBuilder multiGetRequestBuilder = client.prepareMultiGet();
		for (String s : paramList) {
			multiGetRequestBuilder.add(index, type, s);
		}
		MultiGetResponse multiGetItemResponses = multiGetRequestBuilder.get();
		/*
		 * for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
		 * GetResponse response = itemResponse.getResponse(); if
		 * (response.isExists()) { String json = response.getSourceAsString(); }
		 * }
		 */
		return multiGetItemResponses;
	}

	/**
	 * 批量增加
	 * 
	 * @param index
	 * @param type
	 * @param paramList
	 *            map:{key:value ==> columnName:columnValue}
	 * @return
	 * @throws IOException
	 */
	public boolean prepareBulkRequest(String index, String type, List<Map<String, Object>> paramList) throws IOException {
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		for (Map<String, Object> map : paramList) {// 可以添加多个增加和删除
			bulkRequest.add(client.prepareIndex(index, type).setSource(map));
		}
		BulkResponse bulkResponse = bulkRequest.get();
		if (bulkResponse.hasFailures()) {// 如果有失败
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 多条件查询 or
	 * 
	 * @param index
	 * @param type
	 * @param paramMap
	 *            key:value ==> columnName:columnValue
	 * @return
	 */
	public long multiSearchOr(String index, String type, Map<String, Object> paramMap) {
		MultiSearchRequestBuilder multiSearchRequestBuilder = client.prepareMultiSearch();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		for (String s : paramMap.keySet()) {
			boolQueryBuilder.should(QueryBuilders.termQuery(s, paramMap.get(s)));
		}
		multiSearchRequestBuilder.add(client.prepareSearch().setQuery(boolQueryBuilder));
		MultiSearchResponse sr = multiSearchRequestBuilder.execute().actionGet();

		long nbHits = 0;
		for (MultiSearchResponse.Item item : sr.getResponses()) {
			SearchResponse response = item.getResponse();
			nbHits += response.getHits().getTotalHits();
		}
		return nbHits;
	}

	/**
	 * 多条件查询 and
	 * 
	 * @param index
	 * @param type
	 * @param paramMap
	 *            key:value ==> columnName:columnValue
	 */
	public long multiSearchAnd(String index, String type, Map<String, Object> paramMap) {
		MultiSearchRequestBuilder multiSearchRequestBuilder = client.prepareMultiSearch();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		for (String s : paramMap.keySet()) {
			boolQueryBuilder.must(QueryBuilders.termQuery(s, paramMap.get(s)));
		}
		multiSearchRequestBuilder.add(client.prepareSearch().setQuery(boolQueryBuilder));
		MultiSearchResponse sr = multiSearchRequestBuilder.execute().actionGet();

		long nbHits = 0;
		for (MultiSearchResponse.Item item : sr.getResponses()) {
			SearchResponse response = item.getResponse();
			nbHits += response.getHits().getTotalHits();
		}
		return nbHits;
	}

	/**
	 * 多条件查询 and 分页
	 * 
	 * @param index
	 * @param type
	 * @param start
	 * @param paramMap key:value ==> columnName:columnValue
	 * @throws Exception
	 */
	public long multiSearchAndPage(String index, String type, int start, Map<String, Object> paramMap){
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		for (String s : paramMap.keySet()) {
			boolQueryBuilder.must(QueryBuilders.termQuery(s, paramMap.get(s)));
		}
		SearchRequestBuilder srb = client.prepareSearch(index).setSize(3).setFrom(start).setQuery(boolQueryBuilder);
		SearchResponse sr = srb.execute().actionGet();
		SearchHits hits = sr.getHits();
        System.out.println("查询到记录数=" + hits.getTotalHits());
        SearchHit[] searchHists = hits.getHits();
		return searchHists.length;
	}
	
	/**
	 * 复合查询（中文有问题）
	 * 
	 * @param index
	 * @param type
	 * @param paramMap
	 */
	public void aggregations(String index, String type, Map<String, String> paramMap) {

		SearchRequestBuilder srb = client.prepareSearch().setQuery(QueryBuilders.matchAllQuery());
		srb.setSearchType(SearchType.QUERY_THEN_FETCH);
		TermsBuilder ageTermsBuilder = AggregationBuilders.terms("ageF").field("age");
		TermsBuilder nameTermsBuilder = AggregationBuilders.terms("nameS").field("name");
		ageTermsBuilder.subAggregation(nameTermsBuilder);
		srb.addAggregation(ageTermsBuilder);
		SearchResponse sr = srb.execute().actionGet();
		Map<String, Aggregation> resultMap = sr.getAggregations().asMap();
		LongTerms ageF = (LongTerms) resultMap.get("ageF");
		Iterator<Bucket> gradeBucketIt = ageF.getBuckets().iterator();
		while (gradeBucketIt.hasNext()) {
			Bucket gradeBucket = gradeBucketIt.next();
			System.out.println(gradeBucket.getKey() + "岁有" + gradeBucket.getDocCount() + "个。");

			StringTerms classTerms = (StringTerms) gradeBucket.getAggregations().asMap().get("nameS");
			Iterator<Bucket> classBucketIt = classTerms.getBuckets().iterator();

			while (classBucketIt.hasNext()) {
				Bucket classBucket = classBucketIt.next();
				System.out.println(gradeBucket.getKey() + "的##" + classBucket.getKey() + "##有【" + classBucket.getDocCount() + "】个。");
			}
			System.out.println();
		}
	}

	/**
	 * 查询高亮显示
	 * @param index
	 * @param type
	 * @param paramMap
	 */
	public SearchHit[] search(String index, String type, int start, Map<String, String> paramMap){
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		for (String s : paramMap.keySet()) {
			boolQueryBuilder.must(QueryBuilders.termQuery(s, paramMap.get(s)));
		}
		SortBuilder sortBuilder = SortBuilders.fieldSort("pm_name").order(SortOrder.DESC).unmappedType("String");
		SearchRequestBuilder srb = client.prepareSearch(index).setSize(3).setFrom(start).setQuery(boolQueryBuilder).addSort(sortBuilder)
				.addHighlightedField("pm_name").setHighlighterPreTags("<span style=\"color:red\">").setHighlighterPostTags("</span>");
		SearchResponse sr = srb.execute().actionGet();
		SearchHits hits = sr.getHits();
        System.out.println("查询到记录数=" + hits.getTotalHits());
        return hits.getHits();
	}
	
	/*@Test
	public void es_cud() {
		EsUtil esUtil = EsUtil.getInstance();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			paramMap.put("name", "赵七");
			paramMap.put("age", 20);
			esUtil.createOne("index-demo", "type-demo", "1", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void es_r() {
		EsUtil esUtil = EsUtil.getInstance();
		GetResponse getResponse = esUtil.getOneById("index-demo", "type-demo", "1");
		Assert.assertNotNull(getResponse);
	}

	@Test
	public void es_prepareBulkRequest() {
		EsUtil esUtil = EsUtil.getInstance();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", "张三");
		paramMap.put("age", 21);
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		paramMap2.put("name", "李四");
		paramMap2.put("age", 22);
		list.add(paramMap);
		list.add(paramMap2);
		try {
			boolean isSuccess = esUtil.prepareBulkRequest("index-demo", "type-demo", list);
			Assert.assertTrue(isSuccess);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void es_multiSearchOr() {
		EsUtil esUtil = EsUtil.getInstance();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", "李四");
		paramMap.put("age", 20);
		long nbHits = esUtil.multiSearchOr("index-demo", "type-demo", paramMap);
		Assert.assertEquals(4, nbHits);
	}

	@Test
	public void es_aggregations() {
		EsUtil esUtil = EsUtil.getInstance();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("ageF", "age");
		paramMap.put("nameS", "name");
		try {
			esUtil.aggregations("index-demo", "type-demo", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void es_multiSearchAnd() {
		EsUtil esUtil = EsUtil.getInstance();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", "李四");
		paramMap.put("age", 22);
		long nbHits = esUtil.multiSearchAnd("index-demo", "type-demo", paramMap);
		Assert.assertEquals(4, nbHits);
	}

	@Test
	public void es_multiSearchAndPage() {
		EsUtil esUtil = EsUtil.getInstance();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", "李四");
		paramMap.put("age", 22);
		try {
			long nbHits = esUtil.multiSearchAndPage("index-demo", "type-demo", 4, paramMap);
			Assert.assertEquals(1, nbHits);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void es_search() {
		EsUtil esUtil = EsUtil.getInstance();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("name", "李四");
		paramMap.put("age", "22");
		try {
			esUtil.search("index-demo", "type-demo", 0, paramMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void es_highlight(){
		EsUtil esUtil = EsUtil.getInstance();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("pm_name", "安安");
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
			HighlightField titleField = result.get("pm_name");
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
		System.out.println(JSON.toJSONString(list));
	}*/
}
class DataInfo{
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}