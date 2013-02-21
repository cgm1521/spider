package test.junit.crawler;

import java.io.IOException;

import org.dom4j.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cgm.spider.utils.HttpUtil;

public class TestHttpUtil {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		try {
			System.out.println(HttpUtil.getCleanString("http://www.oschina.net"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Document doc = HttpUtil.getDocument("http://www.baidu.com");
			System.out.println(doc.asXML());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
