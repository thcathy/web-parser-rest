package thc.parser.finance;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.math.NumberUtils;
import org.asynchttpclient.Dsl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.Utils.TestUtils;
import thc.domain.StockQuote;
import thc.service.HttpParseService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static thc.domain.StockQuote.NA;

public class AastockStockQuoteRequestTest {
	private Logger log = LoggerFactory.getLogger(AastockStockQuoteRequestTest.class);

    HttpParseService parserService = new HttpParseService(Dsl.asyncHttpClient());

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() {
        Stopwatch timer = Stopwatch.createStarted();

        CompletableFuture<Optional<StockQuote>> quote = parserService.process(new AastockStockQuoteRequest("941"));
        StockQuote q = quote.join().get();
		assertEquals("941", q.getStockCode());
		assertEquals("CHINA MOBILE", q.getStockName());
		assertTrue(NumberUtils.isNumber(q.getPrice()));
		assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
		assertTrue(q.getChange().endsWith("%"));
		assertNotEquals(NA, q.getLastUpdate());
		assertTrue(NumberUtils.isNumber(q.getPe()));
		assertTrue(q.getYield().endsWith("%"));
		assertNotEquals(NA, q.getNAV());
		assertTrue(NumberUtils.isNumber(q.getYearLow()));
		assertTrue(NumberUtils.isNumber(q.getYearHigh()));

		if (TestUtils.withIntraDayData()) {
            assertTrue(NumberUtils.isNumber(q.getLow()));
            assertTrue(NumberUtils.isNumber(q.getHigh()));
        }

        log.debug("getStockQuote_Given941_ShouldReturn941StockQuote took: {}", timer.stop());
	}

    @Test
    public void getStockQuote_Given2800_ShouldReturn2800StockQuote() {
        Stopwatch timer = Stopwatch.createStarted();

        CompletableFuture<Optional<StockQuote>> quote = parserService.process(new AastockStockQuoteRequest("2800"));
        StockQuote q = quote.join().get();
        log.debug("StockQuote: ", q);

        assertEquals("2800", q.getStockCode());
        assertEquals("TRACKER FUND", q.getStockName());
        assertTrue(NumberUtils.isNumber(q.getPrice()));
        assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
        assertTrue(q.getChange().endsWith("%"));
        assertNotEquals(NA, q.getLastUpdate());
        assertEquals(NA, q.getPe());
        assertTrue(q.getYield().endsWith("%"));
        assertEquals(NA, q.getNAV());
        assertTrue(NumberUtils.isNumber(q.getYearLow()));
        assertTrue(NumberUtils.isNumber(q.getYearHigh()));

        if (TestUtils.withIntraDayData()) {
            assertTrue(NumberUtils.isNumber(q.getLow()));
            assertTrue(NumberUtils.isNumber(q.getHigh()));
        }

        log.debug("getStockQuote_Given2800_ShouldReturn2800StockQuote took: {}", timer.stop());
    }

}