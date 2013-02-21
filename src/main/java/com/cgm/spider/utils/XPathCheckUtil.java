package com.cgm.spider.utils;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

public class XPathCheckUtil {

	private final static Log LOGGER = LogFactory.getLog(XPathCheckUtil.class);
	public static String checkUri(String uri,String xpath) {
		StringBuilder result = new StringBuilder();
		try {
			if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("检查XPATH:uri={0},xpath={1}",uri,xpath));
			Document htmlDoc = HttpUtil.getDocument(uri);
			List<Node> nodes = htmlDoc.selectNodes(xpath);
			if(nodes == null || nodes.size() < 1){
				if(LOGGER.isInfoEnabled()) LOGGER.info(MessageFormat.format("检查XPATH:uri={0},xpath={1},无匹配节点！",uri,xpath));
				return result.toString();
			}
			
			if(LOGGER.isInfoEnabled()) LOGGER.info(MessageFormat.format("检查XPATH:uri={0},xpath={1},数量={2}",uri,xpath,nodes.size()));
			for (Node node : nodes) {
				result.append(node.asXML());
			}
		} catch (Exception e) {
			LOGGER.error(result.append("检查异常：").append(e.getMessage()),e);
		}
		return result.toString();
	}
	
	public static String checkXml(String xml,String xpath) {
		StringBuilder result = new StringBuilder();
		
		try {
			if(LOGGER.isDebugEnabled()) LOGGER.debug(MessageFormat.format("检查XPATH:xpath={0}",xpath));
			SAXReader saxReader = new SAXReader();	// 用来读取xml文档
			Document xmlDoc = saxReader.read(new InputSource(new StringReader(xml)));
			List<Node> nodes = xmlDoc.selectNodes(xpath);
			if(nodes == null || nodes.size() < 1){
				if(LOGGER.isInfoEnabled()) LOGGER.info(MessageFormat.format("检查XPATH:xpath={0},无匹配节点！",xpath));
				return result.toString();
			}
			
			if(LOGGER.isInfoEnabled()) LOGGER.info(MessageFormat.format("检查XPATH:xpath={0},数量={1}",xpath,nodes.size()));
			for (Node node : nodes) {
				result.append(node.asXML());
			}
		} catch (Exception e) {
			LOGGER.error(result.append("检查异常：").append(e.getMessage()),e);
		}
		return result.toString();
	}
	
}
