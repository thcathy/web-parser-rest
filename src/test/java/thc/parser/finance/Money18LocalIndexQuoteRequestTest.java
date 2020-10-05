package thc.parser.finance;

import com.google.common.base.Stopwatch;
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

public class Money18LocalIndexQuoteRequestTest {
	private Logger log = LoggerFactory.getLogger(Money18LocalIndexQuoteRequestTest.class);

    HttpParseService parserService = new HttpParseService(WebParserRestApplication.httpClient());

	@Test
	public void shouldReturnAllIndexQuotes() {
		Stopwatch timer = Stopwatch.createStarted();
		
        List<StockQuote> quotes = parserService.process(new Money18LocalIndexQuoteRequest()).join();
        
	    log.info("shouldReturnAllIndexQuotes took: " + timer.stop());
        
        assertTrue(TestUtils.containCode("HSI", quotes));
        assertTrue(TestUtils.containCode("HSCEI", quotes));
        assertTrue(TestUtils.containCode("SSECI", quotes));
        assertTrue(TestUtils.containCode("CSI300", quotes));

        quotes.forEach(this::isValidQuote);
    }

    private void isValidQuote(StockQuote quote) {
        log.debug("Checking quote: {}", quote);
        assertTrue(quote.getPriceDoubleValue() > 0);
        assertNotEquals(NA, quote.getStockCode());
        assertTrue(NumberUtils.isNumber(quote.getChangeAmount()));
    }

}
