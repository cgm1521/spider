package com.cgm.spider.parse.vo;

import java.util.List;


public class ListTemplate {

	private String id;
	private String name;
	private String url;
	private String xpath;
	private List<Value> values;
	private ListRecordTemplate recordTemplate;
	private ContentTemplate contentTemplate;
	
	public List<Value> getValues() {
		return values;
	}
	public void setValues(List<Value> values) {
		this.values = values;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getXpath() {
		return xpath;
	}
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	public ListRecordTemplate getRecordTemplate() {
		return recordTemplate;
	}
	public void setRecordTemplate(ListRecordTemplate recordTemplate) {
		this.recordTemplate = recordTemplate;
	}
	public ContentTemplate getContentTemplate() {
		return contentTemplate;
	}
	public void setContentTemplate(ContentTemplate contentTemplate) {
		this.contentTemplate = contentTemplate;
	}
	
}
