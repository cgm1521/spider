package com.cgm.spider.parse.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 抓取的正文
 * @author CuiGuiming
 *
 */
public class Content implements Serializable{
	
	private static final long serialVersionUID = 6809462227848589565L;
	
	private Map<String,String> valueMap;

	public Map<String, String> getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map<String, String> valueMap) {
		this.valueMap = valueMap;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
