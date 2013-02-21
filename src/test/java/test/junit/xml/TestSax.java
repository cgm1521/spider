package test.junit.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TestSax {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	// @Test
	public void test() {
		SAXParserFactory saxfac = SAXParserFactory.newInstance();
		try {
			SAXParser saxparser = saxfac.newSAXParser();
			InputStream is = new FileInputStream(
					"C:\\06Workspace\\myeclipse10ws\\spider\\src\\test\\resources\\baidu.html");
			
			
			saxparser.parse(is, new DefaultHandler());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDom() {

		SAXReader reader = new SAXReader();
		try {
			Document document = reader
					.read(new File(
							"C:\\06Workspace\\myeclipse10ws\\spider\\src\\test\\resources\\baidu.html"));
			List eles = document.selectNodes("//*[@id=\"IndustryNews\"]/ul/text()");
			System.out.println(eles.size());
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	}

	// @Test
	public void testJsoup() {
		/*
		 * Document doc; try { doc =
		 * Jsoup.connect("http://www.baidu.com/").get();
		 * System.out.println(doc.html()); Elements newsHeadlines =
		 * doc.select("#mp-itn b a"); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
	}

	// @Test
	public void testTagSoup() {

	}

	// @Test
	public void testHtmlCleaner() {
		try {
			TagNode tagNode = new HtmlCleaner()
					.clean(new File(
							"C:\\06Workspace\\myeclipse10ws\\spider\\src\\test\\resources\\baidu.html"));
			org.w3c.dom.Document doc = new DomSerializer(
					new CleanerProperties()).createDOM(tagNode);
			System.out.println(doc.toString());
			System.out.println(doc.getNodeValue());
			XPath xpath = XPathFactory.newInstance().newXPath();
			String str = (String) xpath.evaluate(
					"//*[@id=\"IndustryNews\"]/ul[1]/li[1]/a/text()", doc,
					XPathConstants.STRING);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void testHtmlCleanerBaidu() {
		/*
		 * try { TagNode tagNode = new HtmlCleaner().clean(new File(
		 * "C:\\06Workspace\\myeclipse10ws\\spider\\src\\test\\resources\\baidu.html"
		 * )); System.out.println(tagNode.getText()); org.w3c.dom.Document doc =
		 * new DomSerializer(new CleanerProperties()).createDOM(tagNode);
		 * System.out.println(doc.toString()); XPath xpath =
		 * XPathFactory.newInstance().newXPath(); Object result = (Object)
		 * xpath.evaluate(
		 * "/html/body/table[3]/tbody/tr/td[3]/table[5]/tbody/tr[1]/td[2]/a/text()"
		 * , doc,XPathConstants.NODESET); NodeList nodes = (NodeList) result;
		 * System.out.println("---"); System.out.println(nodes.getLength()); for
		 * (int i = 0; i < nodes.getLength(); i++) {
		 * System.out.println(nodes.item(i).getNodeValue()); }
		 * System.out.println("---"); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */
	}

	// @Test
	public void test2() {
		/*
		 * try { TagNode tagNode = new HtmlCleaner().clean(new File(
		 * "C:\\06Workspace\\myeclipse10ws\\spider\\src\\test\\resources\\baidu.html"
		 * )); // System.out.println(tagNode.getText()); org.w3c.dom.Document
		 * doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
		 * 
		 * XPathFactory factory = XPathFactory.newInstance();
		 * 
		 * XPath xpath = factory.newXPath();
		 * 
		 * XPathExpression expr =
		 * xpath.compile("//*[@id=\"IndustryNews\"]/ul/text()"); NodeList nodes
		 * = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		 * 
		 * System.out.println(nodes); System.out.println(nodes.getLength()); for
		 * (int i = 0; i < nodes.getLength(); i++) { Node node = nodes.item(i);
		 * System.out.println("name:"+node.getNodeName());
		 * System.out.println("val:"+node.getNodeValue());
		 * System.out.println("val:"
		 * +node.getOwnerDocument().getChildNodes().item(0).getTextContent());
		 * System.out.println("---"+nodes.item(i).getTextContent()); } } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
	}
}
