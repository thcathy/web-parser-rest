package thc.parser.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class SinaStockQuoteParser {
	static final Logger log = LoggerFactory.getLogger(SinaStockQuoteParser.class);

	static final String URL = "http://sina.com.hk/p/api/aastock/Stock/index/{code}";
	static final ObjectReader jsonReader = new ObjectMapper().readerFor(Map.class);
	
	public static HttpRequest createRequest(String code) {
		return Unirest.get(URL)
				.routeParam("code", StringUtils.leftPad(code, 5, '0'))
				.header("Referer", URL)
				.header("Host", "www.sina.com.hk")
                .header("X-Requested-With", "XMLHttpRequest")
				.queryString("code", code);
	}

    public static Optional<StockQuote> parse(HttpResponse<InputStream> response) {        
        try
		{   
            return Optional.of(parseResponse(response.getRawBody()));
		} catch (Exception e) {
			log.error("Cannot parse stock code: {}", response.getHeaders(), e);
		}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	private static StockQuote parseResponse(InputStream stream) throws IOException {
		Map<String, String> value = jsonReader.readValue(stream);
		
        StockQuote quote = new StockQuote(value.get("ID"));
		quote.setStockName(value.get("Desp"));
		quote.setPrice(value.get("Last"));
		quote.setChangeAmount(value.get("Change"));
		quote.setChange(value.get("PctChange"));
		quote.setHigh(value.get("High"));
		quote.setLow(value.get("Low"));
		quote.setPe(value.get("PERatio"));
		quote.setYield(value.get("Yield") + "%");
		quote.setLastUpdate(value.get("LastUpdate"));
		quote.setYearHigh(value.get("YearHigh"));
		quote.setYearLow(value.get("YearLow"));
        return quote;
	}

}
