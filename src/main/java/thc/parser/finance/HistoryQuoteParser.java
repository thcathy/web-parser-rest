package thc.parser.finance;

import com.mashape.unirest.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.util.NumberUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Optional;


public class HistoryQuoteParser {
	private static Logger log = LoggerFactory.getLogger(HistoryQuoteParser.class);
	
	private static String YAHOO_HISTORY_QUOTE_URL = "http://hk.finance.yahoo.com/q/hp?s={0}&a={2,number,##}&b={1,date,dd}&c={1,date,yyyy}&d={4,number,##}&e={3,date,dd}&f={3,date,yyyy}&g=d";

	private final String code;
	private final Calendar fromDate;
	private final Calendar toDate;

	public HistoryQuoteParser(String stock, int preYear) {
		this.fromDate = Calendar.getInstance();
		this.toDate = Calendar.getInstance();

		fromDate.add(Calendar.YEAR, 0-preYear);
		toDate.add(Calendar.YEAR, 0-preYear);
		toDate.add(Calendar.DATE, 7);

		this.code = StringUtils.leftPad(stock.replaceFirst("^0+(?!$)", ""), 4, '0').concat(".HK");
	}

	public HistoryQuoteParser(String code, Calendar fromDate, Calendar toDate) {
		this.code = code;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public Optional<BigDecimal> parse(HttpResponse<InputStream> response) {
		try
		{	
			Document doc = Jsoup.parse(response.getRawBody(), "UTF-8", "http://hk.finance.yahoo.com");
			String price = doc.select("td[class^=yfnc_tabledata1]:eq(6)").first().text();
			return Optional.of( new BigDecimal(NumberUtils.extractDouble(price)));
		} catch (Exception e) {			
			log.warn("Fail to get historial price from {}, Reason {}", url(), e);
			return Optional.empty();
		}
	}
	
	public String url() {
		return MessageFormat.format(YAHOO_HISTORY_QUOTE_URL, code, fromDate.getTime(), fromDate.get(Calendar.MONTH), toDate.getTime(), toDate.get(Calendar.MONTH));
	}	
}

