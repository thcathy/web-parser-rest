package thc.parser.finance;

import com.mashape.unirest.http.HttpResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.MonetaryBase;
import thc.util.NumberUtils;

import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

public class HKMAMonetaryBaseParser {
	private static Logger log = LoggerFactory.getLogger(HKMAMonetaryBaseParser.class);

	static String DailyMonetaryBaseURL = "http://www.hkma.gov.hk/eng/market-data-and-statistics/monetary-statistics/monetary-base/{0,date,yyyy}/{0,date,yyyyMMdd}-2.shtml";
	
	private final Date date;

	public HKMAMonetaryBaseParser(String yyyymmdd) throws ParseException {
		this(DateUtils.parseDate(yyyymmdd,"yyyyMMdd"));
	}

	public HKMAMonetaryBaseParser(Date date) { this.date = date; }

	public String url() {
		return MessageFormat.format(DailyMonetaryBaseURL, date);
	}

	public Optional<MonetaryBase> parse(HttpResponse<InputStream> response) {
		try {
			Document doc = Jsoup.parse(response.getRawBody(), "UTF-8", "http://www.hkma.gov.hk");
			return Optional.of(
					new MonetaryBase(
						getNumber(doc, "Certificates of Indebtedness"),
						getNumber(doc,"Government Notes/Coins in Circulation"),
						getNumber(doc,"Closing Aggregate Balance"),
						getNumber(doc,"Outstanding Exchange Fund Bills and Notes"))
				);
		} catch (Exception e) {
			log.info("Fail to retrieveDailyMonetaryBase:" + response.getHeaders(),e);
			return Optional.empty();
		}
	}

	private static double getNumber(Document doc, String title) {
		for (Iterator<Element> i = doc.select("td.heading").iterator(); i.hasNext();) {
			Element e = i.next();
			if (e.text().contains(title))
				return NumberUtils.extractDouble(e.nextElementSibling().child(0).text());
		}
		throw new RuntimeException("Cannot find number:" + title);
	}

}
    

