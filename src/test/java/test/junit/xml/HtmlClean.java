package test.junit.xml;

import java.io.File;
import java.io.StringReader;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.xml.sax.InputSource;

public class HtmlClean {

	public void cleanHtml(String htmlurl, String xmlurl) {
		try {
			long start = System.currentTimeMillis();

			HtmlCleaner cleaner = new HtmlCleaner();
			CleanerProperties props = cleaner.getProperties();
			props.setUseCdataForScriptAndStyle(true);
			props.setRecognizeUnicodeChars(true);
			props.setUseEmptyElementTags(true);
			props.setAdvancedXmlEscape(true);
			props.setTranslateSpecialEntities(true);
			props.setBooleanAttributeValues("empty");

			TagNode node = cleaner.clean(new File(htmlurl), "utf-8");

			System.out.println("vreme:" + (System.currentTimeMillis() - start));

			String result = new PrettyXmlSerializer(props).getAsString(node,"utf-8");

			SAXReader saxReader = new SAXReader();	// 用来读取xml文档
			Document document = saxReader.read(new InputSource(new StringReader(result)));
			System.out.println(document.getRootElement().getName());
			List<Node> list = document.selectNodes("//*[@id=\"IndustryNews\"]/ul");
			System.out.println(list.size());
			for (Node nod : list) {
				System.out.println(nod.asXML());
			}
			System.out.println("vreme:" + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		HtmlClean clean = new HtmlClean();
		clean.cleanHtml(
				"C:\\06Workspace\\myeclipse10ws\\spider\\src\\test\\resources\\baidu.html",
				"C:\\06Workspace\\myeclipse10ws\\spider\\src\\test\\resources\\1.xml");
	}
}