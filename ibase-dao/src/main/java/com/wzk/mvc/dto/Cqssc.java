package com.wzk.mvc.dto;

import java.util.List;

public class Cqssc {
	private int rows;
	private String code;
	private String info;
	private List<OpenballInfo> data;
	
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<OpenballInfo> getData() {
		return data;
	}

	public void setData(List<OpenballInfo> data) {
		this.data = data;
	}

	public static class OpenballInfo{
		private long expect;
		private String opencode;
//		private String opentime;
//		private String opentimestamp;
		
		public String getOpencode() {
			return opencode;
		}
		public long getExpect() {
			return expect;
		}
		public void setExpect(long expect) {
			this.expect = expect;
		}
		public void setOpencode(String opencode) {
			this.opencode = opencode;
		}
		
	}
}
