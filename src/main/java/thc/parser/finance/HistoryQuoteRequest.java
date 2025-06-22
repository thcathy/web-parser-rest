package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.parser.HttpParseRequest;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Map;


public abstract class HistoryQuoteRequest<T> implements HttpParseRequest<T> {
	private static Logger log = LoggerFactory.getLogger(HistoryQuoteRequest.class);

	protected static String QUOTE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/{0}?events=capitalGain|div|split&formatted=true&includeAdjustedClose=true&interval=1d&period1={1,number,##}&period2={2,number,##}&symbol={3}&userYfid=true&lang=zh-Hant-HK&region=HK";
	static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);

	protected final String code;
	protected final Calendar fromDate;
	protected final Calendar toDate;

	public HistoryQuoteRequest(String code, String mic, Calendar fromDate, Calendar toDate) {
		if ("XHKG".equals(mic)) {
			code = code.endsWith(".HK") ? code : code + ".HK";
			code = StringUtils.leftPad(code.replaceFirst("^0+(?!$)", ""), 4, '0');
		}

		if (fromDate.equals(toDate)) {
			toDate = (Calendar) toDate.clone();
			toDate.add(Calendar.DATE, 1);
		}

		this.code = code;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	@Override
	public String url() {
		String url = MessageFormat.format(QUOTE_URL, code, dateSecond(fromDate), dateSecond(toDate), code);
        log.info("History Quote Parser url: {}", url);
		return url;
	}

	public BigDecimal defaultValue() {
		return new BigDecimal("0.0");
	}

	private Object dateSecond(Calendar date) {
		return date.getTimeInMillis()/1000000*1000;
	}
}

