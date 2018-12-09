package thc.parser.finance;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.parser.HttpParseRequest;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

public class YahooStockQuoteRequest implements HttpParseRequest<Optional<StockQuote>> {
	protected static final Logger log = LoggerFactory.getLogger(YahooStockQuoteRequest.class);

	public static String URL = "https://hk.finance.yahoo.com/quote/";

	private final String code;

	public YahooStockQuoteRequest(String code) {
		this.code = code;
	}

	@Override
	public String url() {
		return URL + StringUtils.leftPad(code, 4, '0') + ".hk";
	}

	@Override
	public Optional<StockQuote> parseResponse(InputStream responseInputStream) {
		try
		{
			Document doc = Jsoup.parse(responseInputStream, "UTF-8", URL);
			StockQuote quote = new StockQuote(code);

			String[] titleArray = doc.select("title").text().split(" ");

			// price
			quote.setPrice(doc.select("div[id~=QuoteHeader] span[data-reactid$=14]").first().text());

			// stock name
			String title = doc.select("title").text();
			title.substring(title.indexOf("："), title.indexOf(" 的摘要"));
			quote.setStockName(title.substring(title.indexOf("：") + 1, title.indexOf(" 的摘要")));

			// change
			String[] changes = doc.select("div[id~=QuoteHeader] span[data-reactid$=16]").text().split(" ");
			if (changes.length == 2) {
				quote.setChangeAmount(changes[0]);
				quote.setChange(changes[1].substring(1, changes[1].length() - 1));
			}


			// day high day low
			String[] range = doc.select("span:contains(今日波幅)").first().parent().nextElementSibling().text().split(" ");
			if (range.length >= 3) {
				if (NumberUtils.isNumber(range[0])) quote.setLow(range[0]);
				if (NumberUtils.isNumber(range[2])) quote.setHigh(range[2]);
			}

			// PE
			quote.setPe(parseValue(() -> doc.select("span:contains(市盈率)").first().parent().nextElementSibling().text()));
			// yield
			String yield = doc.select("span:contains(收益率)").first().parent().nextElementSibling().text();
			quote.setYield(parseValue(() -> yield.substring(yield.indexOf("(") + 1, yield.indexOf(")"))));
			// NAV
			quote.setNAV(parseValue(() -> doc.select("span:contains(每股盈利)").first().parent().nextElementSibling().text()));

			// last update
			String[] time = doc.select("div[id~=QuoteHeader] span[data-reactid$=18]").text().split(" ");
			quote.setLastUpdate(time[time.length - 2]);

			// 52 high low
			String[] yearHighLow = doc.select("span:contains(52 週波幅)").first().parent().nextElementSibling().text().split(" - ");
			quote.setYearLow(yearHighLow[0]);
			quote.setYearHigh(yearHighLow[1]);

			log.info("parsed quote: {}", quote);
            return Optional.ofNullable(quote);
		} catch (Exception e) {
			log.error("Cannot parse stock code", e);
		}
        return Optional.empty();
	}

    private static String parseValue(Supplier<String> f) {
        try {
        	String value = f.get();
        	if ("無".equals(value)) throw new IllegalArgumentException();
            return value;
        } catch (Exception e) {
            return StockQuote.NA;
        }
    }

}
