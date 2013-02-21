package com.cgm.spider.parse;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.cgm.spider.parse.vo.ContentTemplate;
import com.cgm.spider.parse.vo.ListRecordTemplate;
import com.cgm.spider.parse.vo.ListTemplate;
import com.cgm.spider.parse.vo.Value;

public class TemplateParser {
	
	private final static Log LOGGER = LogFactory.getLog(TemplateParser.class);
	/**
	 * 解析列表模版<br/>
	 * @param templatePath 文件相对classloader的路径
	 * @return 返回结果：key:模版ID,value:模版实体
	 */
	public static Map<String,ListTemplate> parseTemplate(String templatePath){
		Map<String,ListTemplate> templates = new HashMap<String,ListTemplate>();
		
		try {
			if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("开始解析列表模版:{0}", templatePath));
			InputStream is = TemplateParser.class.getClassLoader().getResourceAsStream(templatePath);
			if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("开始解析列表模版:{0},InputStream={1}", templatePath,is));
			if(is == null){
				throw new Exception("找不到该文件");
			}
			SAXReader saxReader = new SAXReader();	// 用来读取xml文档
			Document document = saxReader.read(is);
			List<Node> listTempNodes = document.selectNodes("//listTemplate");
			if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("解析到列表模版数量:[{0}],模版路径:{1}", listTempNodes.size(),templatePath));
			
			if(listTempNodes == null || listTempNodes.size() < 1){
				if(LOGGER.isInfoEnabled()) LOGGER.info("没有解析到列表模版");
				return templates;
			}
			
			ITER_PARSE_TEMPS:
			for (Node listTempNode : listTempNodes) {
				String id = listTempNode.valueOf("@id");
				String name = listTempNode.valueOf("@name");
				String url = listTempNode.valueOf("@url");
				String xpath = listTempNode.valueOf("@xpath");
				if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("id={0},name={1},url={2},xpath={3}",id,name,url,xpath));
				
				// 记录模版
				Node recordTempNode = listTempNode.selectSingleNode("listRecordTemplate");
				if(recordTempNode == null){
					if(LOGGER.isInfoEnabled()) LOGGER.info(MessageFormat.format("模版[{0}]缺少listRecordTemplate节点,解析失败！",id));
					continue ITER_PARSE_TEMPS;
				}
				if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("recordTempNode={0}",recordTempNode));
				ListRecordTemplate recordTemplate = new ListRecordTemplate();
				recordTemplate.setXpath(recordTempNode.valueOf("@xpath"));
				recordTemplate.setValues(parseValueNode(recordTempNode));
				
				// 内容模版
				Node contentTempNode = listTempNode.selectSingleNode("contentTemplate");
				if(contentTempNode == null){
					if(LOGGER.isInfoEnabled()) LOGGER.info(MessageFormat.format("模版[{0}]缺少contentTemplate节点,解析失败！",id));
					continue ITER_PARSE_TEMPS;
				}
				if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("contentTempNode={0}",contentTempNode));
				ContentTemplate contentTemplate = new ContentTemplate();
				contentTemplate.setUrlRoot(contentTempNode.valueOf("@urlRoot"));
				contentTemplate.setXpath(contentTempNode.valueOf("@xpath"));
				contentTemplate.setValues(parseValueNode(contentTempNode));
				
				ListTemplate listTemplate = new ListTemplate();
				// 列表模版Value节点
				listTemplate.setId(id);
				listTemplate.setName(name);
				listTemplate.setUrl(url);
				listTemplate.setXpath(xpath);
				listTemplate.setRecordTemplate(recordTemplate);
				listTemplate.setContentTemplate(contentTemplate);
				listTemplate.setValues(parseValueNode(listTempNode));
				
				templates.put(id, listTemplate);
			}
		} catch (Exception e) {
			LOGGER.error(MessageFormat.format("列表模版解释异常！{0}", templatePath), e);
		}finally{
			if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("列表模版解析结束:{0}", templatePath));
		}
		return templates;
	}
		
	private static List<Value> parseValueNode(Node soucreNode) throws Exception{
		List<Value> values = new ArrayList<Value>();
		List<Node> valueNodes = soucreNode.selectNodes("value");
		if(valueNodes == null || valueNodes.size() < 1){
			if(LOGGER.isInfoEnabled()) LOGGER.info(MessageFormat.format("节点[{0}]中不包含value节点", soucreNode.getName()));
			return values;
		}
		
		for (Node valueNode : valueNodes) {
			Value val = new Value();
			val.setKey(valueNode.getText());
			val.setType(valueNode.valueOf("@type"));
			val.setXpath(valueNode.valueOf("@xpath"));
			values.add(val);
			if(LOGGER.isDebugEnabled()) LOGGER.debug(val);
		}
		if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("节点[{0}]中不包含[{1}]个value节点", soucreNode.getName(),values.size()));
		
		return values;
	}
}
