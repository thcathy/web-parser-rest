package thc.parser.finance;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.parser.JsoupParseRequest;

import java.util.Map;
import java.util.function.Supplier;

public class AastockStockQuoteRequest implements JsoupParseRequest<StockQuote> {
	protected static final Logger log = LoggerFactory.getLogger(AastockStockQuoteRequest.class);
	
	public static String URL = "http://www.aastocks.com/en/stocks/quote/detail-quote.aspx?symbol=";

	private final String symbol;

	public AastockStockQuoteRequest(String code) {
		this.symbol = StringUtils.leftPad(code, 5, '0');
	}

	@Override
	public String url() { return URL + symbol; }

	@Override
	public Map<String, String> headers() {
		return ImmutableMap.of(
		        "Referer",URL,
                "Host", "www.aastocks.com");
	}

	@Override
	public StockQuote parseResponse(Document doc) {
		log.info("parse response from url: {}", url());
		try
		{
			StockQuote quote = new StockQuote(symbol.replaceFirst("^0+(?!$)", ""));

			// price
			var price = doc.select("div[id=labelLast] span").first().text();
			price = NumberUtils.isNumber(price) ? price : price.substring(1);
			quote.setPrice(price);

			// stock name
			quote.setStockName(doc.select("span[id$=StockName]").first().text());

			// change
			quote.setChangeAmount(doc.select("div:containsOwn(Change)").first().nextElementSibling().nextElementSibling().text());
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

			log.info("parsed quote: {}", quote);
			return quote;
		} catch (Exception e) {
			log.error("Cannot parse stock code", e);
		}
        return defaultValue();
	}

	@Override
	public StockQuote defaultValue() {
		return new StockQuote(StockQuote.NA);
	}

	private static String parseValue(Supplier<String> f) {
        try {
            return f.get();
        } catch (Exception e) {
            return StockQuote.NA;
        }
    }

}
