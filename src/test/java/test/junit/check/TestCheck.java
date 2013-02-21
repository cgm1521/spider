package test.junit.check;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Test;

import com.cgm.spider.utils.XPathCheckUtil;

public class TestCheck {

	//@Test
	public void test() {
		 System.out.println(XPathCheckUtil.checkUri("http://www.jincao.com/fa/01/law01.01.htm",
		 "/html/body/div[2]"));
//		System.out.println(XPathCheckUtil.checkUri("http://www.oschina.net",
//				"/html/body"));
	}

	@Test
	public void testXml() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("C:\\06Workspace\\myeclipse10ws\\spider\\src\\test\\resources\\check\\check.xml"));
			StringBuilder expected = new StringBuilder();
			String s = null;
			while ((s = reader.readLine()) != null) {
					expected.append(s).append("\n");
			}
//			System.out.println(expected.toString());
			System.out.println(XPathCheckUtil.checkXml(expected.toString(), "center/table/tbody/tr/td/p/font/b"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
