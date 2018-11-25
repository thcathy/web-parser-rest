package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.parser.HttpParseRequest;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static thc.domain.StockQuote.NA;

public class Money18IndexQuoteRequest implements HttpParseRequest<List<StockQuote>> {
	private static final String URL = "http://money18.on.cc/js/real/index/index_all_r.js";

	protected static final Logger log = LoggerFactory.getLogger(Money18IndexQuoteRequest.class);
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
			String[] indexes = IOUtils.toString(response, StandardCharsets.ISO_8859_1.name()).split(";");
			return Arrays.stream(indexes)
					.flatMap(Money18IndexQuoteRequest::toStockQuote)
					.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("cannot parse response", e);
			return Collections.emptyList();
		}

	}

	private static Stream<StockQuote> toStockQuote(String input) {
		try {
			String[] temp = input.replaceAll("\r","").replaceAll("\n", "").replaceAll("\t", "").split(" = ");
			String code = temp[0].replace("M18.r_", "");
			Map<String, String> value = jsonReader.readValue(temp[1]);
			
			StockQuote quote = new StockQuote(code);
			quote.setLastUpdate(value.get("ltt"));
			quote.setHigh(value.get("high"));
			quote.setLow(value.get("low"));
			quote.setChangeAmount(value.get("difference"));
			quote.setPrice(value.get("value"));
			quote.setChange(calculateChangePercentage(value.get("pc"), value.get("value")) + '%');
			
			return Stream.of(quote);
		} catch (Exception e) {
			log.warn("Fail to parse index to stock quote: {}", input);
			return Stream.empty();
		}
	}
	
	private static String calculateChangePercentage(String pre, String real) {
		double preVal = Double.valueOf(pre);
		double realVal = Double.valueOf(real);

		if (preVal <= 0.0)
			return NA;
		else
			return new DecimalFormat("###.##").format((realVal - preVal) / preVal * 100);
	}

}
