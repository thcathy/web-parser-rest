package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class SingleDateHistoryQuoteRequest extends HistoryQuoteRequest<BigDecimal> {
	private static Logger log = LoggerFactory.getLogger(SingleDateHistoryQuoteRequest.class);

	static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);

	public SingleDateHistoryQuoteRequest(String stock, int preYear) {
		super(stock, calculateFromDate(preYear), calculateToDate(preYear));
	}

	private static Calendar calculateFromDate(int preYear) {
		Calendar fromDate = Calendar.getInstance();
		fromDate.add(Calendar.YEAR, -preYear);
		return fromDate;
	}

	private static Calendar calculateToDate(int preYear) {
		Calendar toDate = Calendar.getInstance();
		toDate.add(Calendar.YEAR, -preYear);
		toDate.add(Calendar.DATE, 7);
		return toDate;
	}

	public SingleDateHistoryQuoteRequest(String code, Calendar fromDate, Calendar toDate) {
		super(code, fromDate, calculateToDate(fromDate, toDate));
	}

	private static Calendar calculateToDate(Calendar fromDate, Calendar toDate) {
		if (fromDate.equals(toDate)) {
			toDate = (Calendar) toDate.clone();
			toDate.add(Calendar.DATE, 1);
		}
		return toDate;
	}

	@Override
	public BigDecimal parseResponse(InputStream inputStream) {
		try
		{
			Map<String, ?> maps = jsonReader.readValue(inputStream);
			var chart = (Map<String, ?>) maps.get("chart");
			var result = (Map<String, ?>) ((List<?>) chart.get("result")).get(0);
			var indicators = (Map<String, ?>) result.get("indicators");
			var adjClose = (Map<String, ?>) ((List<?>) indicators.get("adjclose")).get(0);
			var closingPrices = (List<Double>) adjClose.get("adjclose");
			return BigDecimal.valueOf(closingPrices.get(0));
		} catch (Exception e) {
			log.warn("Fail to get historical price from {}, Reason {}", url(), e.toString());
		}
		return defaultValue();
	}
}

