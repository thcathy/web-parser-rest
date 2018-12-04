package thc.parser.finance;

import com.google.common.collect.ImmutableMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.parser.HttpParseRequest;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EtnetStockQuoteRequest implements HttpParseRequest<Optional<StockQuote>> {
	protected static final Logger log = LoggerFactory.getLogger(EtnetStockQuoteRequest.class);

	private static final String URL = "http://www.etnet.com.hk/www/tc/stocks/realtime/quote.php";
	private final String code;

	public EtnetStockQuoteRequest(String code) {
		this.code = code;
	}

	@Override
	public String url() {
		return URL;
	}

	@Override
	public Map<String, String> headers() {
		return ImmutableMap.of(
				"Referer",URL,
				"Host", "www.aastocks.com");
	}

	@Override
	public Map<String, String> queryParams() {
		return ImmutableMap.of("code", code);
	}

	@Override
	public Optional<StockQuote> parseResponse(InputStream responseInputStream) {
		try {
			Document doc = Jsoup.parse(responseInputStream, "UTF-8", "http://www.etnet.com.hk");
						
			StockQuote q = new StockQuote(doc.select("input[id=quotesearch]").attr("value").replaceFirst("^0+(?!$)", ""));
			q.setPrice(doc.select("div[id^=StkDetailMainBox] span[class^=Price ]").text().replaceAll("[\\D]+$",""));
			
			String[] changes = doc.select("div[id^=StkDetailMainBox] span[class^=Change]").text().split(" ");
			if (changes.length >= 2) {
				q.setChangeAmount(changes[0]);
				q.setChange(changes[1].replace("(", "").replace(")", ""));
			}

			q.setHigh(doc.select("div[id^=StkDetailMainBox] tr:eq(0) td:eq(1) span.Number").text());
			q.setLow(doc.select("div[id^=StkDetailMainBox] tr:eq(1) td:eq(0) span.Number").text());

			Optional<String> updateTime = extractText(doc.select("div[id^=StkDetailTime]").text(), "[0-9]*/[0-9]*/[0-9]* [0-9]*:[0-9]*");
			updateTime.ifPresent(q::setLastUpdate);
			
			q.setPe(doc.select("div[id^=StkList] li:eq(37)").text().split("/")[0].trim());
			q.setYield(doc.select("div[id^=StkList] li:eq(41)").text().split("/")[0].trim() + "%");
			q.setNAV(doc.select("div[id^=StkList] li:eq(49)").text());
			q.setYearHigh(doc.select("div[id^=StkList] li:eq(23)").text());
			q.setYearLow(doc.select("div[id^=StkList] li:eq(27)").text());
			
			log.info("parsed quote: {}", q);
			
			return Optional.of(q);
		} catch (Exception e) {
			log.warn("Cannot get quote from Etnet" , e);
			return Optional.empty();
		}	
	}

	public static Optional<String> extractText(String text, String regex) {
		Pattern p2 = Pattern.compile(regex);
		Matcher m2 = p2.matcher(text);
		if (m2.find()) {
			return Optional.of(m2.group());
		} else {
			return Optional.empty();
		}
	}

}
