package thc.parser.finance;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.constant.FinancialConstants.IndexCode;
import thc.domain.StockQuote;
import thc.parser.HttpParseRequest;
import thc.util.NumberUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class HSINetRequest implements HttpParseRequest<Optional<StockQuote>> {
	private static final Logger log = LoggerFactory.getLogger(HSINetRequest.class);

	// The previous /static/uploads/.../idx_*.csv path now 404s. Daily files are served from this public download.
	static final String DailyReportURL =
			"https://www.hsi.com.hk/api/wsit-hsi-ddoc-ea-public-website-proxy/v1/content/gcs/download";
	private static final String RESOURCE_TEMPLATE =
			"/public-website/webapp/contents/en/indexes/report/{0}/idx_{1}.csv";

	private final IndexCode index;
	private final Date date;

	public HSINetRequest(IndexCode index, String yyyymmdd) {
		var dateFormat = new SimpleDateFormat("yyyyMMdd");
		this.index = index;

		try {
			this.date = dateFormat.parse(yyyymmdd);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String url() {
		var dayFormat = new SimpleDateFormat("dMMMyy", Locale.US);
		var resource = MessageFormat.format(
				RESOURCE_TEMPLATE,
				index.toString().toLowerCase(Locale.ROOT),
				dayFormat.format(date));
		return DailyReportURL + "?resource=" + URLEncoder.encode(resource, StandardCharsets.UTF_8);
	}

	@Override
	public Optional<StockQuote> parseResponse(InputStream response) {
		if (response == null) {
			log.warn("Fail to retrieveDailyReportFromHSINet: {} empty response", index);
			return Optional.empty();
		}
		try {
			var columns = headlineColumns(decodeReport(response));
			if (columns == null) {
				log.warn("Fail to retrieveDailyReportFromHSINet: {} has no index row", index);
				return Optional.empty();
			}

			var quote = new StockQuote(index.toString());
			quote.setLastUpdate(NumberUtils.extractNumber(columns[0]));
			quote.setHigh(NumberUtils.extractNumber(columns[3]));
			quote.setLow(NumberUtils.extractNumber(columns[4]));
			quote.setPrice(NumberUtils.extractNumber(columns[5]));
			quote.setChangeAmount(NumberUtils.extractNumber(columns[6]));
			quote.setChange(NumberUtils.extractNumber(columns[7]));
			quote.setYield(NumberUtils.extractNumber(columns[8]));
			quote.setPe(NumberUtils.extractNumber(columns[9]));

			log.info("parsed HSI quote: {}", quote);
			return Optional.of(quote);
		} catch (Exception e) {
			log.warn("Fail to retrieveDailyReportFromHSINet: {}, due to {}", index, e.toString());
			return Optional.empty();
		}
	}

	private static String decodeReport(InputStream response) throws IOException {
		var text = IOUtils.toString(response, StandardCharsets.UTF_16LE);
		if (!text.isEmpty() && text.charAt(0) == '\uFEFF') {
			return text.substring(1);
		}
		return text;
	}

	/**
	 * The first trade-date row is the headline index. Later rows are sub-indexes with different PE and yield.
	 */
	private static String[] headlineColumns(String report) {
		for (var line : report.split("\\R")) {
			if (line.isBlank()) {
				continue;
			}
			var fields = unquote(line.split("\t", -1));
			if (fields.length > 9 && fields[0].matches("\\d{8}")) {
				return fields;
			}
		}
		return null;
	}

	private static String[] unquote(String[] fields) {
		var values = new String[fields.length];
		for (var i = 0; i < fields.length; i++) {
			var value = fields[i].trim();
			if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
				value = value.substring(1, value.length() - 1);
			}
			values[i] = value;
		}
		return values;
	}
}
