package thc.parser.finance;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.util.NumberUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Optional;


public class HistoryQuoteParser {
	private static Logger log = LoggerFactory.getLogger(HistoryQuoteParser.class);
	
	//private static String YAHOO_HISTORY_QUOTE_URL = "https://hk.finance.yahoo.com/q/hp?s={0}&a={2,number,##}&b={1,date,dd}&c={1,date,yyyy}&d={4,number,##}&e={3,date,dd}&f={3,date,yyyy}&g=d";
	//private static String QUOTE_URL = "http://chart.finance.yahoo.com/table.csv?s={0}&a={2,number}&b={1,date,dd}&c={1,date,yyyy}&d={4,number,##}&e={3,date,dd}&f={3,date,yyyy}&g=d&ignore=.csv";
	private static String QUOTE_URL = "https://hk.finance.yahoo.com/quote/{0}/history?period1={1,number,##}&period2={2,number,##}&interval=1d&filter=history&frequency=1d";

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

	public Optional<BigDecimal> parse(HttpResponse<String> response) {
		try
		{	
			Document doc = Jsoup.parse(response.getRawBody(), "UTF-8", "http://hk.finance.yahoo.com");
			Elements spanInPriceTable = doc.select("table[data-test=historical-prices]").select("span");
			if (spanInPriceTable.get(5).text().contains("收市價") && spanInPriceTable.size() > 13) {
				return Optional.of(new BigDecimal(NumberUtils.extractDouble(spanInPriceTable.get(12).text())));
			}
		} catch (Exception e) {
			log.warn("Fail to get historial price from {}, Reason {}", url(), e.toString());
			return Optional.empty();
		}
		return Optional.empty();
	}
	
	public String url() {
		String url = MessageFormat.format(QUOTE_URL, code, dateSecond(fromDate), dateSecond(toDate));
		log.info("History Quote Parser url: {}", url);
		return url;
	}

	private Object dateSecond(Calendar date) {
		return date.getTimeInMillis()/1000000*1000;
	}

	public HttpRequest createRequest() {
		return Unirest.get(url());
	}
}

