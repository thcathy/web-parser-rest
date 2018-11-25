package thc.parser.finance;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.constant.FinancialConstants.IndexCode;
import thc.domain.StockQuote;
import thc.parser.HttpParseRequest;
import thc.util.NumberUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class HSINetRequest implements HttpParseRequest<Optional<StockQuote>> {
	private static Logger log = LoggerFactory.getLogger(HSINetRequest.class);
	
	static String DailyReportURL = "https://www.hsi.com.hk/static/uploads/contents/en/indexes/report/{0}/idx_{1}.csv";

	private final IndexCode index;
	private final Date date;

	public HSINetRequest(IndexCode index, String yyyymmdd) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		this.index = index;

		try {
			this.date = dateFormat.parse(yyyymmdd);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String url() {
		SimpleDateFormat format = new SimpleDateFormat("dMMMyy", Locale.US);
		return MessageFormat.format(DailyReportURL, StringUtils.lowerCase(index.toString().toLowerCase()), format.format(date));
	}

	@Override
	public Optional<StockQuote> parseResponse(InputStream response) {
		try {
			String[] csv = IOUtils.toString(response, StandardCharsets.US_ASCII.name()).split("\n");
			String[] result = csv[2].split("\t");
			StockQuote quote = new StockQuote(index.toString());
			quote.setLastUpdate(NumberUtils.extractNumber(result[0]));
			quote.setHigh(NumberUtils.extractNumber(result[3]));
			quote.setLow(NumberUtils.extractNumber(result[4]));
			quote.setPrice(NumberUtils.extractNumber(result[5]));
			quote.setChangeAmount(NumberUtils.extractNumber(result[6]));
			quote.setChange(NumberUtils.extractNumber(result[7]));
			quote.setYield(NumberUtils.extractNumber(result[8]));
			quote.setPe(NumberUtils.extractNumber(result[9]));

			log.info("pared HSI quote: {}", quote);
			return Optional.of(quote);
		} catch (Exception e) {
			log.warn("Fail to retrieveDailyReportFromHSINet: {}, due to {}", index, e.toString());
			return Optional.empty();
		}
	}
}
