package thc.parser.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mashape.unirest.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ISharesConstituentParser {
	private static final Logger log = LoggerFactory.getLogger(ISharesConstituentParser.class);
	private static final ObjectReader jsonReader = new ObjectMapper().readerFor(Map.class);
	
	public static List<String> parseMSCIChina(HttpResponse input) {
		try {
			Map<String, ?> value = jsonReader.readValue(input.getRawBody());
			List<Map<String,Object>> data = (List<Map<String, Object>>) value.get("aaData");
			List<String> results = data.stream()
									.filter(m -> m.get("colExchangeCode").equals("XHKG"))
									.map(m -> (String)m.get("colTicker"))
									.collect(Collectors.toList());
			return results;
		} catch (Exception e) {
			log.error("Fail to retrieve constituents of MSCI HK Constituents",e);
			return Collections.emptyList();
		}	
	}

	public static List<String> parseMSCIHK(HttpResponse input) {
		try {
			Map<String, ?> value = jsonReader.readValue(input.getRawBody());
			List<Map<String,Object>> data = (List<Map<String, Object>>) value.get("aaData");
			List<String> results = data.stream()					
									.filter(m -> m.get("colExchange").equals("Hong Kong Exchanges And Clearing Ltd"))
									.map(m -> (String)m.get("colTicker"))
									.collect(Collectors.toList());
			return results;
		} catch (Exception e) {
			log.error("Fail to retrieve constituents of MSCI HK",e);
			return Collections.emptyList();
		}	
	}
}