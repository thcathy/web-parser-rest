package thc.parser.finance;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.constant.FinancialConstants.IndexCode;
import thc.domain.StockQuote;
import thc.util.NumberUtils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class HSINetParser {
	private static Logger log = LoggerFactory.getLogger(HSINetParser.class);
	
	static String DailyReportURL = "http://www.hsi.com.hk/HSI-Net/static/revamp/contents/en/indexes/report/{0}/idx_{1}.csv";

	private final IndexCode index;
	private final Date date;

	public HSINetParser(IndexCode index, String yyyymmdd) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		this.index = index;

		try {
			this.date = dateFormat.parse(yyyymmdd);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public HttpRequest createRequest() {
		SimpleDateFormat format = new SimpleDateFormat("dMMMyy", Locale.US);
		String url = MessageFormat.format(DailyReportURL, StringUtils.lowerCase(index.toString().toLowerCase()), format.format(date));

		return Unirest.get(url);
	}
	
	public Optional<StockQuote> parse(HttpResponse<String> response) {
		try {
			String[] csv = response.getBody().split("\n");
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
			return Optional.of(quote);
		} catch (Exception e) {
			log.warn("Fail to retrieveDailyReportFromHSINet: {}, due to {}", index, e.getMessage());
			return Optional.empty();
		}
	}
}
