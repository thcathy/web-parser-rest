package thc.parser.finance;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.parser.HttpParseRequest;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Money18StockQuoteRequest implements HttpParseRequest<StockQuote> {
	protected static final Logger log = LoggerFactory.getLogger(Money18StockQuoteRequest.class);

	public static String URL = "http://realtime-money18-cdn.on.cc/securityQuote/genStockDetailHKJSON.php?stockcode=";
	static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);

	private final String symbol;

	public Money18StockQuoteRequest(String code) {
		this.symbol = StringUtils.leftPad(code, 5, '0');
	}

	@Override
	public String url() { return URL + symbol; }

	@Override
	public Map<String, String> headers() {
		return ImmutableMap.of(
				"Referer","http://money18.on.cc/",
				"Host","money18.on.cc");
	}

	@Override
	public StockQuote parseResponse(InputStream inputStream) {
		log.info("parse response from url: {}", url());

		try
		{
			Map<String, ?> maps = jsonReader.readValue(inputStream);

			StockQuote quote = new StockQuote(symbol.replaceFirst("^0+(?!$)", ""));
			quote.setPrice(parseString(maps, List.of("real", "np")));
			quote.setStockName(parseString(maps, List.of("daily", "name")));
			quote.setChangeAmount(parseDouble(maps, List.of("calculation", "change")));
			quote.setChange(parseDouble(maps, List.of("calculation", "pctChange")) + "%");
			quote.setLow(parseString(maps, List.of("real", "dyl")));
			quote.setHigh(parseString(maps, List.of("real", "dyh")));
			quote.setPe(parseDouble(maps, List.of("calculation", "pe")));

			//quote.setYield();
			//quote.setNAV();

			quote.setLastUpdate(parseString(maps, List.of("real", "ltt")));
			quote.setYearLow(parseDouble(maps, List.of("daily", "wk52Low")));
			quote.setYearHigh(parseDouble(maps, List.of("daily", "wk52High")));

			log.info("parsed quote: {}", quote);
			return quote;
		} catch (Exception e) {
			log.error("Cannot parse stock code", e);
		}
        return defaultValue();
	}

	StockQuote defaultValue() {
		return new StockQuote(StockQuote.NA);
	}

	private static String parseValue(Supplier<String> f) {
        try {
            return f.get();
        } catch (Exception e) {
            return StockQuote.NA;
        }
    }

    private static String parseString(Map<String, ?> maps, List<String> keys) {
		try {
			for (int i=0; i < keys.size()-1; i++) {
				maps = (Map<String, ?>) maps.get(keys.get(i));
			}
			return (String) maps.get(keys.get(keys.size() - 1));
		} catch (Exception e) {
			return "";
		}
	}

	private static String parseDouble(Map<String, ?> maps, List<String> keys) {
		try {
			for (int i=0; i < keys.size()-1; i++) {
				maps = (Map<String, ?>) maps.get(keys.get(i));
			}
			return maps.get(keys.get(keys.size() - 1)).toString();
		} catch (Exception e) {
			return "";
		}
	}

}
