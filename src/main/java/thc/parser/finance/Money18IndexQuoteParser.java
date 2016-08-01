package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Money18IndexQuoteParser {
	private static final String URL = "http://money18.on.cc/js/real/index/index_all_r.js";

	protected static final Logger log = LoggerFactory.getLogger(Money18IndexQuoteParser.class);
	private static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);

    public static HttpRequest createRequest() {
        return Unirest.get(URL)
                .header("Referer", "http://money18.on.cc/")
                .header("Host", "money18.on.cc");
    }
		
	public static List<StockQuote> parse(HttpResponse<String> response) {
		String[] indexes = response.getBody().split(";");
		return Arrays.stream(indexes)
				.flatMap(Money18IndexQuoteParser::toStockQuote)
				.collect(Collectors.toList());		
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
		return new DecimalFormat("###.##").format((preVal - realVal) / preVal * 100);
	}

}
