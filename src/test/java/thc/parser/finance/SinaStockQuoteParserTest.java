package thc.parser.finance;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.service.HttpService;

import static org.junit.Assert.*;
import static thc.domain.StockQuote.NA;


public class SinaStockQuoteParserTest {
	private Logger log = LoggerFactory.getLogger(SinaStockQuoteParserTest.class);

    HttpService httpService = new HttpService();

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() {
        Stopwatch timer = Stopwatch.createStarted();

		StockQuote q = httpService
                .queryAsync(
                        SinaStockQuoteParser.createRequest("941")::asBinaryAsync,
                        SinaStockQuoteParser::parse)
                .join().get();
		assertEquals("941", q.getStockCode());
		assertEquals("中國移動", q.getStockName());
		assertTrue(NumberUtils.isNumber(q.getPrice()));
		assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
		assertTrue(q.getChange().endsWith("%"));
		assertTrue(NumberUtils.isNumber(q.getLow()));
		assertTrue(NumberUtils.isNumber(q.getHigh()));
		assertNotEquals(NA, q.getLastUpdate());
		assertTrue(NumberUtils.isNumber(q.getPe()));
		assertTrue(q.getYield().endsWith("%"));
		assertEquals(NA, q.getNAV());
		assertTrue(NumberUtils.isNumber(q.getYearLow()));
		assertTrue(NumberUtils.isNumber(q.getYearHigh()));

        log.debug("getStockQuote_Given941_ShouldReturn941StockQuote took: {}", timer.stop());
	}

    @Test
    public void getStockQuote_Given2800_ShouldReturn2800StockQuote() {
        Stopwatch timer = Stopwatch.createStarted();

        StockQuote q = httpService
                .queryAsync(
                        SinaStockQuoteParser.createRequest("2800")::asBinaryAsync,
                        SinaStockQuoteParser::parse)
                .join().get();
        assertEquals("2800", q.getStockCode());
        assertEquals("盈富基金", q.getStockName());
        assertTrue(NumberUtils.isNumber(q.getPrice()));
        assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
        assertTrue(q.getChange().endsWith("%"));
        assertTrue(NumberUtils.isNumber(q.getLow()));
        assertTrue(NumberUtils.isNumber(q.getHigh()));
        assertNotEquals(NA, q.getLastUpdate());
        assertTrue(NumberUtils.isNumber(q.getPe()));
        assertTrue(q.getYield().endsWith("%"));
        assertEquals(NA, q.getNAV());
        assertTrue(NumberUtils.isNumber(q.getYearLow()));
        assertTrue(NumberUtils.isNumber(q.getYearHigh()));

        log.debug("getStockQuote_Given2800_ShouldReturn2800StockQuote took: {}", timer.stop());
    }
}