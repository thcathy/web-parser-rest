package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.parser.HttpParseRequest;
import thc.parser.JsoupParseRequest;
import thc.util.NumberUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HistoryQuoteRequest implements HttpParseRequest<BigDecimal> {
	private static Logger log = LoggerFactory.getLogger(HistoryQuoteRequest.class);

	private static String QUOTE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/{0}?events=capitalGain|div|split&formatted=true&includeAdjustedClose=true&interval=1d&period1={1,number,##}&period2={2,number,##}&symbol={3}&userYfid=true&lang=zh-Hant-HK&region=HK";
	static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);

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
		String url = MessageFormat.format(QUOTE_URL, code, dateSecond(fromDate), dateSecond(toDate), code);
        log.info("History Quote Parser url: {}", url);
		return url;
	}

	@Override
	public BigDecimal parseResponse(InputStream inputStream) {
		try
		{
			Map<String, ?> maps = jsonReader.readValue(inputStream);
			var chart = (Map<String, ?>) maps.get("chart");
			var result = (Map<String, ?>) ((List<?>) chart.get("result")).get(0);
			var indicators = (Map<String, ?>) result.get("indicators");
			var adjclose = (Map<String, ?>) ((List<?>) indicators.get("adjclose")).get(0);
			var closingPrices = (List<Double>) adjclose.get("adjclose");
			return BigDecimal.valueOf(closingPrices.get(0));
		} catch (Exception e) {
			log.warn("Fail to get historical price from {}, Reason {}", url(), e.toString());
		}
		return defaultValue();
	}

	public BigDecimal defaultValue() {
		return new BigDecimal("0.0");
	}

	private Object dateSecond(Calendar date) {
		return date.getTimeInMillis()/1000000*1000;
	}
}

