package thc.parser.finance;

import com.google.common.base.Stopwatch;
import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.service.HttpService;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static thc.domain.StockQuote.NA;

public class AastockStockQuoteParserTest {
	private Logger log = LoggerFactory.getLogger(AastockStockQuoteParserTest.class);

    HttpService httpService = new HttpService();

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() {        
        if (tooEarlyTooTest()) return;
        
        Stopwatch timer = Stopwatch.createStarted();

        HttpRequest request = AastockStockQuoteParser.createRequest("941");
		StockQuote q = httpService.queryAsync(request::asBinaryAsync, AastockStockQuoteParser::parse).join().get();
		assertEquals("00941", q.getStockCode());
		assertEquals("CHINA MOBILE", q.getStockName());
		assertTrue(NumberUtils.isNumber(q.getPrice()));
		assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
		assertTrue(q.getChange().endsWith("%"));
		assertTrue(NumberUtils.isNumber(q.getLow()));
		assertTrue(NumberUtils.isNumber(q.getHigh()));
		assertNotEquals(NA, q.getLastUpdate());
		assertTrue(NumberUtils.isNumber(q.getPe()));
		assertTrue(q.getYield().endsWith("%"));
		assertNotEquals(NA, q.getNAV());
		assertTrue(NumberUtils.isNumber(q.getYearLow()));
		assertTrue(NumberUtils.isNumber(q.getYearHigh()));

        log.debug("getStockQuote_Given941_ShouldReturn941StockQuote took: {}", timer.stop());
	}

    @Test
    public void getStockQuote_Given2800_ShouldReturn2800StockQuote() {
        if (tooEarlyTooTest()) return;
        
        Stopwatch timer = Stopwatch.createStarted();

        HttpRequest request = AastockStockQuoteParser.createRequest("2800");
        StockQuote q = httpService.queryAsync(request::asBinaryAsync, AastockStockQuoteParser::parse).join().get();
        assertEquals("02800", q.getStockCode());
        assertEquals("TRACKER FUND", q.getStockName());
        assertTrue(NumberUtils.isNumber(q.getPrice()));
        assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
        assertTrue(q.getChange().endsWith("%"));
        assertTrue(NumberUtils.isNumber(q.getLow()));
        assertTrue(NumberUtils.isNumber(q.getHigh()));
        assertNotEquals(NA, q.getLastUpdate());
        assertEquals(NA, q.getPe());
        assertTrue(q.getYield().endsWith("%"));
        assertEquals(NA, q.getNAV());
        assertTrue(NumberUtils.isNumber(q.getYearLow()));
        assertTrue(NumberUtils.isNumber(q.getYearHigh()));

        log.debug("getStockQuote_Given2800_ShouldReturn2800StockQuote took: {}", timer.stop());
    }
    
    private boolean tooEarlyTooTest() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));
        if (c.get(Calendar.HOUR_OF_DAY) < 9 || c.get(Calendar.MINUTE) < 20) 
            return true;
        else 
            return false;        
    }
}