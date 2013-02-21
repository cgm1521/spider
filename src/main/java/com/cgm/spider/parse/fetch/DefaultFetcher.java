package com.cgm.spider.parse.fetch;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;

import com.cgm.spider.parse.IFetcher;
import com.cgm.spider.parse.pojo.Content;
import com.cgm.spider.parse.pojo.Record;
import com.cgm.spider.parse.vo.ContentTemplate;
import com.cgm.spider.parse.vo.ListRecordTemplate;
import com.cgm.spider.parse.vo.ListTemplate;
import com.cgm.spider.parse.vo.Value;
import com.cgm.spider.utils.HttpUtil;

public class DefaultFetcher implements IFetcher{

	private Log logger = LogFactory.getLog(DefaultFetcher.class);
	
	@Override
	public List<Record> fetch(ListTemplate listTemplate) throws Exception {
		Document htmlDoc = HttpUtil.getDocument(listTemplate.getUrl());
		
		List<Record> records = new ArrayList<Record>(0);
		List<Node> listNodes = htmlDoc.selectNodes(listTemplate.getXpath());
		
		if(listNodes == null || listNodes.size() < 1){
			if(logger.isInfoEnabled()) logger.info(MessageFormat.format("没有检索到record列表！id={0},name={1},url={2},xpath={3}", listTemplate.getId(),listTemplate.getName(),listTemplate.getUrl(),listTemplate.getXpath()));
			return records;
		}
		
		if(logger.isDebugEnabled()) logger.debug(MessageFormat.format("检索到record列表数量:{4}！id={0},name={1},url={2},xpath={3}", listTemplate.getId(),listTemplate.getName(),listTemplate.getUrl(),listTemplate.getXpath(),listNodes.size()));
		ITER_LIST_NODES: // 解析页面中的记录列表
		for (Node listNode : listNodes) {
			try {
				if(logger.isInfoEnabled()) logger.info(MessageFormat.format("开始解析list记录！id={0},nodeName={1},xpath={2}", listTemplate.getId(),listNode.getName(),listTemplate.getXpath()));
				if(logger.isDebugEnabled()) logger.debug(MessageFormat.format("list:{0}", listNode.asXML()));
				
				// 解析record
				ListRecordTemplate recordTemplate = listTemplate.getRecordTemplate();
				List<Node> recordNodes = listNode.selectNodes(recordTemplate.getXpath());
				if(recordNodes == null || recordNodes.size() < 1){
					if(logger.isInfoEnabled()) logger.info(MessageFormat.format("没有检索到record记录！id={0},nodeName={1},xpath={2}", listTemplate.getId(),listNode.getName(),recordTemplate.getXpath()));
					continue ITER_LIST_NODES;
				}
				if(logger.isInfoEnabled()) logger.info(MessageFormat.format("检索到[{1}]条record记录！xpath={0}", recordTemplate.getXpath(),recordNodes.size()));
				
				Map<String,String> listValueMap = fetchValueNodes(listTemplate.getValues(), listNode);
				ContentTemplate contentTemplate = listTemplate.getContentTemplate();
				ITER_RECORD_NODES:
				for (int i = 0; i < recordNodes.size(); i++) {
					Node recordNode = recordNodes.get(i);
					if(logger.isDebugEnabled()) logger.debug(MessageFormat.format("解析第[{0}]条记录,record:{1}", i+1,recordNode.asXML()));
					Record record = new Record();
					List<Content> cnts = new ArrayList<Content>(); 
					record.setValueMap(fetchValueNodes(recordTemplate.getValues(), recordNode));
					record.getValueMap().putAll(listValueMap);
					
					try {
						Content cnt = new Content();
						// 解析content,内容解析可扩展为一条记录对应多条内容页面
						try {
							String url = new StringBuilder(contentTemplate.getUrlRoot()).append(record.getValueMap().get("url")).toString();
							if(logger.isInfoEnabled()) logger.info(MessageFormat.format("content url={0}", url));
							Document cntHtmlDoc = HttpUtil.getDocument(url);
							Node contentNode = cntHtmlDoc.selectSingleNode(contentTemplate.getXpath());
							if(logger.isDebugEnabled()){
								logger.debug(MessageFormat.format("content xpath={0}", contentTemplate.getXpath()));
								logger.debug(MessageFormat.format("content html={0}", contentNode.asXML()));
							}
							cnt.setValueMap(fetchValueNodes(contentTemplate.getValues(),contentNode));
						} catch (Exception e) {
							if(logger.isWarnEnabled()) logger.warn(MessageFormat.format("记录内容[content]解析异常,continue record;xpath={0}", recordTemplate.getXpath()),e);
							continue ITER_RECORD_NODES;
						}
						cnts.add(cnt);
					} catch (Exception e) {
						if(logger.isWarnEnabled()) logger.warn(MessageFormat.format("列表记录[record]解析异常,continue record;xpath={0}", recordTemplate.getXpath()),e);
						continue ITER_RECORD_NODES;
					}
					record.setCnts(cnts);
					records.add(record);
				}
			} catch (Exception e) {
				if(logger.isWarnEnabled()) logger.warn(MessageFormat.format("列表[list]解析异常,continue list;id={0},nodeName={1},xpath={2}", listTemplate.getId(),listNode.getName(),listTemplate.getXpath()));
				continue ITER_LIST_NODES;
			}
		}
		
		return records;
	}

	/**
	 * 解析Value节点
	 * @param values
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private Map<String,String> fetchValueNodes(List<Value> values, Node node) throws Exception{
		Map<String,String> valueMap = new HashMap<String,String>();
		for (Value val : values) {
			if(logger.isDebugEnabled()) logger.debug(MessageFormat.format("解析value节点:xpath={0},type={1}",val.getXpath(),val.getType()));
			String value = null;
			if("attr".equalsIgnoreCase(val.getType())){
				value = node.valueOf((val.getXpath()));
			} else if ("text".equalsIgnoreCase(val.getType())){
				Node valNode = node.selectSingleNode(val.getXpath());
				if(valNode != null) value = valNode.getText();
			} else if ("html".equalsIgnoreCase(val.getType())){
				Node valNode = node.selectSingleNode(val.getXpath());
				if(valNode != null) value = valNode.asXML();
			} else {
				if(logger.isInfoEnabled()) logger.info(MessageFormat.format("value节点type不正确([attr,text]),xpath={0},type={1}", val.getXpath(),val.getType()));
			}
			valueMap.put(val.getKey(), value);
		}
		if(logger.isDebugEnabled()) logger.debug(MessageFormat.format("valueMap = {0}",valueMap));
		return valueMap;
	}

	
}
