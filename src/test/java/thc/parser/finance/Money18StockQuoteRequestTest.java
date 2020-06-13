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

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static thc.domain.StockQuote.NA;

public class Money18StockQuoteRequestTest {
	private Logger log = LoggerFactory.getLogger(Money18StockQuoteRequestTest.class);

    HttpParseService parserService = new HttpParseService(WebParserRestApplication.httpClient());

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() throws Exception {
        Stopwatch timer = Stopwatch.createStarted();

        StockQuote q = parserService.process(new Money18StockQuoteRequest("941")).get();
		assertEquals("941", q.getStockCode());
		assertEquals("CHINA MOBILE", q.getStockName());
		assertThat(Double.valueOf(q.getPrice()), greaterThan(50.0));
		assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
		assertTrue(q.getChange().endsWith("%"));
		assertNotEquals(NA, q.getLastUpdate());
		assertTrue(NumberUtils.isNumber(q.getPe()));
		assertTrue(NumberUtils.isNumber(q.getYearLow()));
		assertTrue(NumberUtils.isNumber(q.getYearHigh()));

		if (TestUtils.withIntraDayData()) {
            assertTrue(NumberUtils.isNumber(q.getLow()));
            assertTrue(NumberUtils.isNumber(q.getHigh()));
        }

        log.debug("getStockQuote_Given941_ShouldReturn941StockQuote took: {}", timer.stop());
	}

    @Test
    public void getStockQuote_Given2800_ShouldReturn2800StockQuote() throws Exception {
        Stopwatch timer = Stopwatch.createStarted();

        StockQuote q = parserService.process(new Money18StockQuoteRequest("2800")).get();
        log.debug("StockQuote: ", q);

        assertEquals("2800", q.getStockCode());
        assertEquals("TRACKER FUND", q.getStockName());
        assertThat(Double.valueOf(q.getPrice()), greaterThan(20.0));
        assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
        assertTrue(q.getChange().endsWith("%"));
        assertNotEquals(NA, q.getLastUpdate());
        assertEquals("0", q.getPe());
        assertTrue(NumberUtils.isNumber(q.getYearLow()));
        assertTrue(NumberUtils.isNumber(q.getYearHigh()));

        if (TestUtils.withIntraDayData()) {
            assertTrue(NumberUtils.isNumber(q.getLow()));
            assertTrue(NumberUtils.isNumber(q.getHigh()));
        }

        log.debug("getStockQuote_Given2800_ShouldReturn2800StockQuote took: {}", timer.stop());
    }

    @Test
    public void getStockQuote_Given7288_ShouldReturnCode7288() throws Exception {
        StockQuote q = parserService.process(new Money18StockQuoteRequest("7288")).get();
        assertEquals("7288", q.getStockCode());
    }

}
