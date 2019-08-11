package thc.parser.finance;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.MonetaryBase;
import thc.parser.JsoupParseRequest;
import thc.util.NumberUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

public class HKMAMonetaryBaseRequest implements JsoupParseRequest<MonetaryBase> {
	private static Logger log = LoggerFactory.getLogger(HKMAMonetaryBaseRequest.class);

	static String DailyMonetaryBaseURL = "https://www.hkma.gov.hk/eng/market-data-and-statistics/monetary-statistics/monetary-base/{0,date,yyyy}/{0,date,yyyyMMdd}-2.shtml";
	
	private final Date date;

	public HKMAMonetaryBaseRequest(String yyyymmdd) throws ParseException {
		this(DateUtils.parseDate(yyyymmdd,"yyyyMMdd"));
	}

	public HKMAMonetaryBaseRequest(Date date) { this.date = date; }

	@Override
	public String url() {
		return MessageFormat.format(DailyMonetaryBaseURL, date);
	}

	@Override
	public MonetaryBase parseResponse(Document doc) {
		return new MonetaryBase(
					getNumber(doc, "Certificates of Indebtedness"),
					getNumber(doc,"Government Notes/Coins in Circulation"),
					getNumber(doc,"Closing Aggregate Balance"),
					getNumber(doc,"Outstanding Exchange Fund Bills and Notes"));
	}

	@Override
	public MonetaryBase defaultValue() {
		return MonetaryBase.empty();
	}

	private double getNumber(Document doc, String title) {
		for (Iterator<Element> i = doc.select("td.heading").iterator(); i.hasNext();) {
			Element e = i.next();
			if (e.text().contains(title))
				return NumberUtils.extractDouble(e.nextElementSibling().child(0).text());
		}
		throw new RuntimeException("Cannot find number:" + title);
	}

}
    

