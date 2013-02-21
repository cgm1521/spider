package com.cgm.spider.parse.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Record implements Serializable{
	
	private static final long serialVersionUID = -8538091639839027983L;

	private Map<String,String> valueMap;
	
	private List<Content> cnts;

	public Map<String, String> getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map<String, String> valueMap) {
		this.valueMap = valueMap;
	}

	public List<Content> getCnts() {
		return cnts;
	}

	public void setCnts(List<Content> cnts) {
		this.cnts = cnts;
	}
	
}
