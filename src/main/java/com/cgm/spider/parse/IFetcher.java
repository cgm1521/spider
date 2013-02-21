package com.cgm.spider.parse;

import java.util.List;

import com.cgm.spider.parse.pojo.Record;
import com.cgm.spider.parse.vo.ListTemplate;

public interface IFetcher {

	public List<Record> fetch(ListTemplate listTemplate) throws Exception;
	
}
