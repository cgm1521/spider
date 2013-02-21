package com.cgm.spider.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.xml.sax.InputSource;

public class HttpUtil {
	private static final String UTF_8 = "utf-8";
	private static final int DEFAULT_REQUEST_LIMIT = 1;
	private static HttpClient httpClient;
	private static final Log LOGGER = LogFactory.getLog(HttpUtil.class);

	public static synchronized HttpClient getHttpClient() {
		if (null == httpClient) {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "UTF-8");
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams.setUserAgent(params,"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C)");
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, 3000);
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 10000);

			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
			httpClient = new DefaultHttpClient(conMgr, params);
		}
		return httpClient;
	}

	/**
	 * 获得Post请求对象
	 * 
	 * @param uri
	 *            请求地址，也可以带参数
	 * @param params
	 *            如果为null，则不添加由BasicNameValue封装的参数
	 * @return
	 */
	public static HttpPost getPost(String uri, List<BasicNameValuePair> params) {
		HttpPost post = new HttpPost(uri);
		try {
			if (params != null) {
				post.setEntity(new UrlEncodedFormEntity(params));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return post;
	}

	/**
	 * 用户使用的方法 功能：从服务器获得字符串
	 * 
	 * @param post
	 * @return
	 */
	public static String postString(HttpPost post) {

		HttpClient httpClient = getHttpClient();
		HttpResponse response;
		try {
			response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				post.abort();
				LOGGER.warn("响应失败,请求终止.");
				return null;
			}
			LOGGER.warn("响应成功.");
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			LOGGER.error(e.getMessage(),e);
			return null;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
			return null;
		}
	}

	/**
	 * 用户使用的方法 功能：请求服务器，返回字符串
	 * 
	 * @param post
	 *            post 请求对象
	 * @param requestLimit
	 *            请求失败限制次数
	 * @return
	 */
	public static String postString(HttpPost post, int requestLimit) {

		if (requestLimit < 1) {
			return null;
		}
		HttpResponse response;
		int currCount = 0; // 当前请求次数
		String result = null;

		while (currCount < requestLimit) {
			HttpClient httpClient = getHttpClient();
			currCount++;
			try {
				response = httpClient.execute(post);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					LOGGER.warn("响应成功.");
					return EntityUtils.toString(response.getEntity());
				} else {
					post.abort();
					LOGGER.warn("响应失败,请求终止.");
					result = "响应失败,请求终止.";
				}
			} catch (ClientProtocolException e) {
				LOGGER.error(e.getMessage());
				if (currCount > requestLimit) {
					result = "请求失败.";
					break;
				}
				if(LOGGER.isInfoEnabled()) LOGGER.info("ClientProtocolException");
			} catch (IOException e) {
				LOGGER.error(e.getMessage(),e);
				if (e instanceof ConnectTimeoutException) {
					result = "连接超时.";
				} else {
					result = "IO异常.";
				}
				if (currCount > requestLimit) {
					break;
				}
			} finally {
				if(LOGGER.isInfoEnabled()) LOGGER.info("finally");
			}
		}
		return result;
	}

	/**
	 * 用户使用的方法 功能：请求服务器，返回字符串
	 * 
	 * @param uri
	 *            字符串形式的请求地址
	 * @param requestLimit
	 *            最多允许的请求失败次数
	 * @return
	 */
	public static String getString(String uri, int requestLimit) {

		if (requestLimit < 1) {
			return null;
		}
		HttpResponse response;
		int currCount = 0; // 当前请求次数
		String result = null;
		HttpGet get = new HttpGet(uri);
		while (currCount < requestLimit) {

			HttpClient httpClient = getHttpClient();
			currCount++;
			try {
				response = httpClient.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					LOGGER.warn("响应成功.");
					
					String charset = EntityUtils.getContentCharSet(response.getEntity());
		            if (charset == null) {
		                charset = HTTP.DEFAULT_CONTENT_CHARSET;
		            }
		            
		            if(HTTP.DEFAULT_CONTENT_CHARSET.equals(charset)){
		            	String html = EntityUtils.toString(response.getEntity());
		            	if(html.contains("charset=gb2312") || html.contains("charset=GB2312")){
		            		return new String(html.getBytes(HTTP.DEFAULT_CONTENT_CHARSET),"GB2312");
		            	} else if(html.contains("charset=gbk") || html.contains("charset=GBK")){
		            		return new String(html.getBytes(HTTP.DEFAULT_CONTENT_CHARSET),"GBK");
		            	} else if(html.contains("charset=utf-8") || html.contains("charset=UTF-8")){
		            		return new String(html.getBytes(HTTP.DEFAULT_CONTENT_CHARSET),HTTP.UTF_8);
		            	} else{
		            		return new String(html.getBytes(HTTP.DEFAULT_CONTENT_CHARSET),HTTP.UTF_8);
		            	}
		            } else {
		            	return EntityUtils.toString(response.getEntity());
		            }
				} else {
					get.abort();
					LOGGER.warn("响应失败,请求终止.");
					LOGGER.warn(EntityUtils.toString(response.getEntity()));
					result = "响应失败,请求终止.";
				}
			} catch (ClientProtocolException e) {
				LOGGER.error(e.getMessage(),e);
				if (currCount > requestLimit) {
					result = "请求失败.";
					break;
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(),e);
				if (e instanceof ConnectTimeoutException) {
					result = "连接超时.";
				} else {
					result = "IO异常.";
				}
				if (currCount > requestLimit) {
					break;
				}
			} finally {
				shutdown();
			}
		}
		return result;
	}
	
	public static String getString(String uri) {
		return getString(uri, DEFAULT_REQUEST_LIMIT);
	}
	
	public static String getCleanString(String uri) throws IOException {
		String content = getString(uri, DEFAULT_REQUEST_LIMIT);
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setUseCdataForScriptAndStyle(true);
		props.setRecognizeUnicodeChars(true);
		props.setUseEmptyElementTags(true);
		props.setAdvancedXmlEscape(true);
		props.setTranslateSpecialEntities(true);
		props.setBooleanAttributeValues("empty");

		TagNode node = cleaner.clean(content.replaceFirst("<html.+>", "<html>"));
		String result = new PrettyXmlSerializer(props).getAsString(node, UTF_8);
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("URI = "+uri);
			LOGGER.debug(result);
		}
		return result;
	}
	
	public static Document getDocument(String uri) throws IOException, DocumentException{
		if(LOGGER.isInfoEnabled()) LOGGER.info(MessageFormat.format("请求页面,uri={0}", uri));
		String content = getString(uri, DEFAULT_REQUEST_LIMIT);
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setUseCdataForScriptAndStyle(true);
		props.setRecognizeUnicodeChars(true);
		props.setUseEmptyElementTags(true);
		props.setAdvancedXmlEscape(true);
		props.setTranslateSpecialEntities(true);
		props.setBooleanAttributeValues("empty");
		
		TagNode node = cleaner.clean(content.replaceFirst("<html.+>", "<html>"));
		String result = new PrettyXmlSerializer(props).getAsString(node, UTF_8);
		
		if(LOGGER.isDebugEnabled()) LOGGER.debug(result);
		
		SAXReader saxReader = new SAXReader();// 用来读取xml文档
		return saxReader.read(new StringReader(result));
	}
	
	/**
	 * 释放建立http请求占用的资源
	 */
	public static void shutdown() {
		// 释放建立http请求占用的资源
		httpClient.getConnectionManager().shutdown();
		httpClient = null;
	}
}