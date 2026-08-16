package thc.parser.finance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class ISharesConstituentParser {
	private static final Logger log = LoggerFactory.getLogger(ISharesConstituentParser.class);
	private static final String HKEX = "Hong Kong Exchanges And Clearing Ltd";

	public static List<String> parseMSCIHK(InputStream response) {
		try (var reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8))) {
			var header = readHoldingsHeader(reader);
			var tickerIdx = header.indexOf("Ticker");
			var exchangeIdx = header.indexOf("Exchange");
			if (tickerIdx < 0 || exchangeIdx < 0) {
				log.error("Fail to retrieve constituents of MSCI HK: unexpected CSV columns {}", header);
				return Collections.emptyList();
			}

			var tickers = new LinkedHashSet<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isBlank()) {
					continue;
				}
				var cols = parseCsvLine(line);
				if (cols.size() <= Math.max(tickerIdx, exchangeIdx)) {
					continue;
				}
				if (!HKEX.equals(cols.get(exchangeIdx))) {
					continue;
				}
				var ticker = cols.get(tickerIdx).trim();
				if (!ticker.isEmpty() && !"--".equals(ticker)) {
					tickers.add(ticker);
				}
			}
			return new ArrayList<>(tickers);
		} catch (Exception e) {
			log.error("Fail to retrieve constituents of MSCI HK", e);
			return Collections.emptyList();
		}
	}

	private static List<String> readHoldingsHeader(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			line = stripBom(line);
			if (line.startsWith("Ticker,")) {
				return parseCsvLine(line);
			}
		}
		return Collections.emptyList();
	}

	private static String stripBom(String line) {
		return line.startsWith("\uFEFF") ? line.substring(1) : line;
	}

	private static List<String> parseCsvLine(String line) {
		var fields = new ArrayList<String>();
		var current = new StringBuilder();
		var inQuotes = false;
		for (var i = 0; i < line.length(); i++) {
			var c = line.charAt(i);
			if (c == '"') {
				inQuotes = !inQuotes;
			} else if (c == ',' && !inQuotes) {
				fields.add(current.toString());
				current.setLength(0);
			} else {
				current.append(c);
			}
		}
		fields.add(current.toString());
		return fields;
	}
}
