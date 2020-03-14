package thc.parser.finance;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.parser.JsoupParseRequest;
import thc.util.NumberUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;


public class HistoryQuoteRequest implements JsoupParseRequest<BigDecimal> {
	private static Logger log = LoggerFactory.getLogger(HistoryQuoteRequest.class);

	private static String QUOTE_URL = "https://hk.finance.yahoo.com/quote/{0}/history?period1={1,number,##}&period2={2,number,##}&interval=1d&filter=history&frequency=1d";

	private final String code;
	private final Calendar fromDate;
	private final Calendar toDate;

	public HistoryQuoteRequest(String stock, int preYear) {
		this.fromDate = Calendar.getInstance();
		this.toDate = Calendar.getInstance();

		fromDate.add(Calendar.YEAR, 0-preYear);
		toDate.add(Calendar.YEAR, 0-preYear);
		toDate.add(Calendar.DATE, 7);

		this.code = StringUtils.leftPad(stock.replaceFirst("^0+(?!$)", ""), 4, '0').concat(".HK");
	}

	public HistoryQuoteRequest(String code, Calendar fromDate, Calendar toDate) {
		this.code = code;
		this.fromDate = fromDate;

		if (fromDate.equals(toDate)) {
			toDate = (Calendar) toDate.clone();
			toDate.add(Calendar.DATE, 1);
		}
		this.toDate = toDate;
	}

	@Override
	public String url() {
		String url = MessageFormat.format(QUOTE_URL, code, dateSecond(fromDate), dateSecond(toDate));
		log.info("History Quote Parser url: {}", url);
		return url;
	}

	@Override
	public BigDecimal parseResponse(Document doc) {
		try
		{
			Elements spanInPriceTable = doc.select("table[data-test=historical-prices]").select("span");
			if (spanInPriceTable.get(5).text().contains("收市價") && spanInPriceTable.size() > 13) {
				return new BigDecimal(NumberUtils.extractDouble(spanInPriceTable.get(12).text()));
			}
		} catch (Exception e) {
			log.warn("Fail to get historial price from {}, Reason {}", url(), e.toString());
		}
		return defaultValue();
	}

	@Override
	public BigDecimal defaultValue() {
		return new BigDecimal(0.0);
	}

	private Object dateSecond(Calendar date) {
		return date.getTimeInMillis()/1000000*1000;
	}
}

