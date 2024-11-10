package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public class RangeHistoryQuoteRequest extends HistoryQuoteRequest<List<DailyStockQuote>> {
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
	public List<DailyStockQuote> parseResponse(InputStream inputStream) {
		try
		{
			Map<String, ?> maps = jsonReader.readValue(inputStream);
			var chart = (Map<String, ?>) maps.get("chart");
			var result = (Map<String, ?>) ((List<?>) chart.get("result")).get(0);
			var indicators = (Map<String, ?>) result.get("indicators");
			var adjCloseArray = (Map<String, ?>) ((List<?>) indicators.get("adjclose")).get(0);
			var quote = (Map<String, ?>) ((List<?>) indicators.get("quote")).get(0);

			var timestampList = (List<Integer>) result.get("timestamp");
			var adjCloseList = (List<Double>) adjCloseArray.get("adjclose");
			var lowList = (List<Double>) quote.get("low");
			var openList = (List<Double>) quote.get("open");
			var highList = (List<Double>) quote.get("high");
			var closeList = (List<Double>) quote.get("close");
			var volumeList = (List<Integer>) quote.get("volume");

			var quotes = new ArrayList<DailyStockQuote>();
			for (int i=0; i < timestampList.size(); i++) {
				var localDate = Instant.ofEpochSecond(timestampList.get(i))
						.atZone(ZoneId.systemDefault())
						.toLocalDate();
				quotes.add(new DailyStockQuote(
					localDate, openList.get(i), closeList.get(i), highList.get(i), lowList.get(i), volumeList.get(i), adjCloseList.get(i)
				));
			}
			return quotes;
		} catch (Exception e) {
			log.warn("Fail to get historical price from {}, Reason {}", url(), e.toString());
		}
		return Collections.emptyList();
	}
}

