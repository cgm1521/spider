package com.cgm.spider.parse.vo;

import java.util.List;

public class ContentTemplate {
	
	private String xpath;
	private String urlRoot;
	private List<Value> values;
	
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public String getUrlRoot() {
		return urlRoot;
	}
	public void setUrlRoot(String urlRoot) {
		this.urlRoot = urlRoot;
	}
	public List<Value> getValues() {
		return values;
	}
	public void setValues(List<Value> values) {
		this.values = values;
	}
}
