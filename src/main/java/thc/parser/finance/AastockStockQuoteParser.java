package thc.parser.finance;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static thc.util.NumberUtils.extractNumber;

public class AastockStockQuoteParser {
	protected static final Logger log = LoggerFactory.getLogger(AastockStockQuoteParser.class);
	
	public static String URL = "http://www.aastocks.com/en/stocks/quote/detail-quote.aspx?symbol=";
	private final BoundRequestBuilder requestBuilder;

	public AastockStockQuoteParser(AsyncHttpClient asyncHttpClient) {
		this.requestBuilder = asyncHttpClient.prepareGet("http://www.aastocks.com/en/stocks/quote/detail-quote.aspx")
				.addHeader("Referer", "http://www.aastocks.com/en/stocks/quote/detail-quote.aspx")
				.addHeader("Host", "www.aastocks.com");
	}

	public CompletableFuture<Optional<StockQuote>> query(String code) {
		return requestBuilder
				.addQueryParam("symbol", StringUtils.leftPad(code, 5, '0'))
				.execute()
				.toCompletableFuture()
				.exceptionally(t -> nullResponseOnError(code, t))
				.thenApply(response -> parse(response));
	}

	private static Response nullResponseOnError(String url, Throwable t) {
		log.error("Error when querying: {}", url, t);
		return null;
	}

	public Optional<StockQuote> parse(Response response) {
		try
		{
			Document doc = Jsoup.parse(response.getResponseBodyAsStream(), "UTF-8", URL);
			StockQuote quote = new StockQuote(extractNumber(doc.select("title").first().text()).replace(".-","").replaceFirst("^0+(?!$)", ""));

			// price
			quote.setPrice(doc.select("div[id=labelLast] span").first().text().substring(1));

			// stock name
			quote.setStockName(doc.select("span[id$=StockName").first().text());

			// change
			quote.setChangeAmount(doc.select("div:containsOwn(Change").first().nextElementSibling().nextElementSibling().text());
			quote.setChange(doc.select("div:containsOwn(Change(%))").first().nextElementSibling().nextElementSibling().text());

			// day high day low
			String[] range = doc.select("div:containsOwn(Range)").first().nextElementSibling().nextElementSibling().text().split(" ");
			if (range.length >= 3) {
				if (NumberUtils.isNumber(range[0])) quote.setLow(range[0]);
				if (NumberUtils.isNumber(range[2])) quote.setHigh(range[2]);
			}

			// PE
			quote.setPe(parseValue(() -> doc.select("div[id=tbPERatio]").first().child(1).text().split(" / ")[0].substring(1)));
			// yield
			quote.setYield(parseValue(() -> doc.select("div:containsOwn(Yield/)").first().parent().parent().child(1).text().split(" / ")[0].substring(1)));
			// NAV
			quote.setNAV(parseValue(() -> doc.select("div[id=tbPBRatio]").first().child(1).text().split(" / ")[1]));

			// last update
			quote.setLastUpdate(doc.select("span:containsOwn(Updated:)").first().child(0).text());

			// 52 high low
			String[] yearHighLow = doc.select("td:containsOwn(52 Week)").first().nextElementSibling().text().split(" - ");
			quote.setYearLow(yearHighLow[0]);
			quote.setYearHigh(yearHighLow[1]);
            return Optional.ofNullable(quote);
		} catch (Exception e) {
			log.error("Cannot parse stock code: {}", response.getHeaders(), e);            
		}
        return Optional.empty();
	}

    private static String parseValue(Supplier<String> f) {
        try {
            return f.get();
        } catch (Exception e) {
            return StockQuote.NA;
        }
    }

}
