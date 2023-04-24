package thc.parser.finance;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.parser.JsoupParseRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Supplier;

public class YahooStockQuoteRequest implements JsoupParseRequest<StockQuote> {
	protected static final Logger log = LoggerFactory.getLogger(YahooStockQuoteRequest.class);

	private static String URL = "https://hk.finance.yahoo.com/quote/";
	private final SimpleDateFormat sourceTimeFormat = new SimpleDateFormat("hh:mma", Locale.US);
	private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	private final String code;

	public YahooStockQuoteRequest(String code) {
		this.code = code;
	}

	@Override
	public String url() {
		return URL + StringUtils.leftPad(code, 4, '0') + ".hk";
	}

	@Override
	public StockQuote parseResponse(Document doc) {
		try
		{
			StockQuote quote = new StockQuote(code);

			// price
			quote.setPrice(doc.select("div[id~=quote-header-info] [data-field$=regularMarketPrice]").first().text());

			// stock name
			String title = doc.select("title").text();
			quote.setStockName(title.substring(0, title.indexOf("(")-1).trim());

			// change
			quote.setChangeAmount(doc.select("div[id~=quote-header-info] [data-field$=regularMarketChange]").first().text());
			quote.setChange(
					doc.select("div[id~=quote-header-info] [data-field$=regularMarketChangePercent]").first().text()
							.replace("(","")
							.replace(")","")
			);

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
			String time = doc.select("div[id~=quote-market-notice] span").text().split(" ")[0].substring(3);
			Date parsedTime = sourceTimeFormat.parse(time);
			quote.setLastUpdate(outputDateFormat.format(parsedTime));

			// 52 high low
			String[] yearHighLow = doc.select("span:contains(52 週波幅)").first().parent().nextElementSibling().text().split(" - ");
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
        	String value = f.get();
        	if ("無".equals(value)) throw new IllegalArgumentException();
            return value;
        } catch (Exception e) {
            return StockQuote.NA;
        }
    }
}
