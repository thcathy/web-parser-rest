package thc.parser.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ISharesConstituentParser {
	private static final Logger log = LoggerFactory.getLogger(ISharesConstituentParser.class);
	private static final ObjectReader jsonReader = new ObjectMapper().readerFor(Map.class);
	
	public static List<String> parseMSCIChina(InputStream response) {
		try {
			Map<String, ?> value = jsonReader.readValue(response);
			List<List<String>> data = (List<List<String>>) value.get("aaData");
			List<String> results = data.stream()
									.filter(l -> l.get(2).equals("XHKG"))
									.map(l -> l.get(0))
									.collect(Collectors.toList());
			return results;
		} catch (Exception e) {
			log.error("Fail to retrieve constituents of MSCI HK Constituents",e);
			return Collections.emptyList();
		}	
	}

	public static List<String> parseMSCIHK(InputStream response) {
		try {
			Map<String, ?> value = jsonReader.readValue(response);
			List<List<String>> data = (List<List<String>>) value.get("aaData");
			List<String> results = data.stream()					
									.filter(l -> l.get(11).equals("Hong Kong Exchanges And Clearing Ltd"))
									.map(l -> l.get(0))
									.collect(Collectors.toList());
			return results;
		} catch (Exception e) {
			log.error("Fail to retrieve constituents of MSCI HK",e);
			return Collections.emptyList();
		}	
	}
}