package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import thc.domain.StockQuote;
import thc.parser.HttpParseRequest;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Money18WorldIndexQuoteRequest implements HttpParseRequest<List<StockQuote>> {
	private static final String URL = "http://realtime-money18-cdn.on.cc/js/daily/adr_radar/json/b_index_w.js";

	protected static final Logger log = LoggerFactory.getLogger(Money18WorldIndexQuoteRequest.class);
	private static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);

	@Override
	public String url() { return URL; }

	@Override
	public Map<String, String> headers() {
		return ImmutableMap.of(
				"Referer","http://money18.on.cc/",
				"Host","money18.on.cc");
	}

	@Override
	public List<StockQuote> parseResponse(InputStream response) {
		try {
			Map<String, Object> responseMap = jsonReader.readValue(IOUtils.toString(response, StandardCharsets.UTF_8.name()));
			String updateTime = formatTime((String) responseMap.get("modtime"));
			List<Map<String, String>> items = (List<Map<String, String>>) responseMap.get("item");
			return items.stream()
					.flatMap((i) -> toStockQuote(i, updateTime))
					.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("cannot parse response", e);
			return Collections.emptyList();
		}

	}

	String formatTime(String input) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHss");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			return outputFormat.format(inputFormat.parse(input));
		} catch (ParseException e) {
			log.error("{} is not in format 'yyyyMMddHHss'", input);
			return "";
		}
	}

	private static Stream<StockQuote> toStockQuote(Map<String, String> values, String lastUpdateTime) {
		if (CollectionUtils.isEmpty(values)) return Stream.empty();

		try {
			StockQuote quote = new StockQuote(values.get("name"));
			quote.setLastUpdate(lastUpdateTime);
			quote.setHigh(values.get("high"));
			quote.setLow(values.get("low"));
			quote.setChangeAmount(values.get("change"));
			quote.setPrice(values.get("value"));
			quote.setChange(values.get("percentage") + '%');
			return Stream.of(quote);
		} catch (Exception e) {
			log.warn("Fail to parse index to stock quote: {} : {}", e.toString(), values);
			return Stream.empty();
		}
	}

}
