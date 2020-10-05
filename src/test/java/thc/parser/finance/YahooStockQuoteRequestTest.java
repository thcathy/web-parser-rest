package thc.parser.finance;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.Utils.TestUtils;
import thc.domain.StockQuote;
import thc.service.JsoupParseService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static thc.domain.StockQuote.NA;

public class YahooStockQuoteRequestTest {
	private Logger log = LoggerFactory.getLogger(YahooStockQuoteRequestTest.class);

    JsoupParseService parserService = new JsoupParseService();

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() {
        Stopwatch timer = Stopwatch.createStarted();

        StockQuote q = parserService.process(new YahooStockQuoteRequest("941")).block();
		assertEquals("941", q.getStockCode());
		assertEquals("CHINA MOBILE", q.getStockName());
        assertThat(Double.valueOf(q.getPrice()), greaterThan(10.0));
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
    public void getStockQuote_Given2800_ShouldReturn2800StockQuote() throws ParseException {
        Stopwatch timer = Stopwatch.createStarted();

        StockQuote q = parserService.process(new YahooStockQuoteRequest("2800")).block();
        log.debug("StockQuote: ", q);

        assertEquals("2800", q.getStockCode());
        assertEquals("TRACKER FUND", q.getStockName());
        assertThat(Double.valueOf(q.getPrice()), greaterThan(20.0));
        assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
        assertTrue(q.getChange().endsWith("%"));
        assertNotEquals(NA, q.getLastUpdate());
        assertEquals(NA, q.getPe());
        assertEquals(NA, q.getYield());
        assertEquals(NA, q.getNAV());
        assertTrue(NumberUtils.isNumber(q.getYearLow()));
        assertTrue(NumberUtils.isNumber(q.getYearHigh()));
        assertNotNull(new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(q.getLastUpdate()));

        if (TestUtils.withIntraDayData()) {
            assertTrue(NumberUtils.isNumber(q.getLow()));
            assertTrue(NumberUtils.isNumber(q.getHigh()));
        }

        log.debug("getStockQuote_Given2800_ShouldReturn2800StockQuote took: {}", timer.stop());
    }

    @Test
    public void getStockQuote_Given7288_ShouldReturnCode7288() {
        StockQuote q = parserService.process(new YahooStockQuoteRequest("7288")).block();
        log.debug("StockQuote: ", q);

        assertEquals("7288", q.getStockCode());
        assertThat(Double.valueOf(q.getPrice()), greaterThan(2.0));
    }

    @Test
    public void getStockQuote_Given3046_ShouldReturnCode3046() {
        StockQuote q = parserService.process(new YahooStockQuoteRequest("3046")).block();
        log.debug("StockQuote: ", q);

        assertEquals("3046", q.getStockCode());
        assertThat(Double.valueOf(q.getPrice()), greaterThan(20.0));
    }

}