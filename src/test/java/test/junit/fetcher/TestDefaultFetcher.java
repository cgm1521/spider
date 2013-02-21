package test.junit.fetcher;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cgm.spider.parse.IFetcher;
import com.cgm.spider.parse.TemplateParser;
import com.cgm.spider.parse.fetch.DefaultFetcher;
import com.cgm.spider.parse.pojo.Record;
import com.cgm.spider.parse.vo.ListTemplate;

public class TestDefaultFetcher {

	private ListTemplate temp; 
	@Before
	public void setUp() throws Exception {
		Map<String,ListTemplate> temps = TemplateParser.parseTemplate("tempts/template.xml");
		temp = temps.get("oschina001");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		IFetcher fether = new DefaultFetcher();
		try {
			List<Record> rs = fether.fetch(temp);
			System.out.println(rs.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
