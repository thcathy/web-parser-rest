package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class RangeHistoryQuoteRequest extends HistoryQuoteRequest<List<BigDecimal>> {
	private static Logger log = LoggerFactory.getLogger(RangeHistoryQuoteRequest.class);

	static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);

	public RangeHistoryQuoteRequest(String code, Calendar fromDate, Calendar toDate) {
		super(code, fromDate, calculateToDate(fromDate, toDate));
	}

	private static Calendar calculateToDate(Calendar fromDate, Calendar toDate) {
		if (fromDate.equals(toDate)) {
			toDate = (Calendar) toDate.clone();
			toDate.add(Calendar.DATE, 30);
		}
		return toDate;
	}

	@Override
	public List<BigDecimal> parseResponse(InputStream inputStream) {
		try
		{
			Map<String, ?> maps = jsonReader.readValue(inputStream);
			var chart = (Map<String, ?>) maps.get("chart");
			var result = (Map<String, ?>) ((List<?>) chart.get("result")).get(0);
			var indicators = (Map<String, ?>) result.get("indicators");
			var adjClose = (Map<String, ?>) ((List<?>) indicators.get("adjclose")).get(0);
			var closingPrices = (List<Double>) adjClose.get("adjclose");
			return closingPrices.stream().map(BigDecimal::valueOf).toList();
		} catch (Exception e) {
			log.warn("Fail to get historical price from {}, Reason {}", url(), e.toString());
		}
		return Collections.emptyList();
	}
}

