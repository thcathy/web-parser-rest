package thc.parser.finance;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EtnetIndexConstituentParser {
	private static final Logger log = LoggerFactory.getLogger(EtnetIndexConstituentParser.class);
		
	public static List<String> parse(InputStream response) {
		List<String> results = new ArrayList<String>();
		try
		{			
			Document doc = Jsoup.parse(response, "UTF-8", "http://www.etnet.com.hk");
			for (Iterator<Element> i = doc.select("a[href^=realtime/quote.php?code=]").iterator(); i.hasNext();) {				
				Element e = i.next();
				if (StringUtils.isNumeric(e.html())) results.add(Integer.valueOf(e.html()).toString());
			}
		} catch (Exception e) {
			log.error("Fail to retrieve index constituent from response", e);
		}
		return results;
	}
}

