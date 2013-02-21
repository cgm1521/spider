package com.cgm.spider.parse.vo;

import java.text.MessageFormat;

public class Value {
	
	private String xpath;
	private String type;
	private String key;
	
	@Override
	public String toString() {
		return MessageFormat.format("key={0},type={1},xpath={2}", key,type,xpath);
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
