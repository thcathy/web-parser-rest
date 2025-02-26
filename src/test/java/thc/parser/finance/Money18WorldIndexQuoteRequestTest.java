package thc.parser.finance;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.Utils.TestUtils;
import thc.WebParserRestApplication;
import thc.domain.StockQuote;
import thc.service.HttpParseService;

import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static thc.domain.StockQuote.NA;

public class Money18WorldIndexQuoteRequestTest {
	private Logger log = LoggerFactory.getLogger(Money18WorldIndexQuoteRequestTest.class);

    HttpParseService parserService = new HttpParseService(WebParserRestApplication.httpClient());

	@Test
	public void shouldReturnAllIndexQuotes() {
        List<StockQuote> quotes = parserService.process(new Money18WorldIndexQuoteRequest()).join();
        assertTrue(TestUtils.containCode("紐約道瓊斯指數", quotes));
        assertTrue(TestUtils.containCode("標準普爾指數", quotes));
        assertTrue(TestUtils.containCode("上海綜合指數", quotes));
        quotes.forEach(this::isValidQuote);
    }

    private void isValidQuote(StockQuote quote) {
        log.debug("Checking quote: {}", quote);
        assertTrue(quote.getPriceDoubleValue() > 0);
        assertNotEquals(NA, quote.getStockCode());
        assertTrue(NumberUtils.isNumber(quote.getChangeAmount()));
    }

}
