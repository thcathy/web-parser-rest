package thc.parser.finance;

import com.google.common.base.Stopwatch;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;
import thc.service.HttpService;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class EtnetStockQuoteParserTest {
	private Logger log = LoggerFactory.getLogger(EtnetStockQuoteParserTest.class);

	HttpService httpService = new HttpService();

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() throws UnirestException {
		if (tooEarlyTooTest()) return;
		
		Stopwatch timer = Stopwatch.createStarted();

		StockQuote q = httpService
				.queryAsync(
						EtnetStockQuoteParser.createRequest("941")::asBinaryAsync, 
						EtnetStockQuoteParser::parse)
				.join().get();
       
		assertEquals("00941", q.getStockCode());
		assertTrue(NumberUtils.isNumber(q.getPrice()));
		assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));		
		assertTrue(q.getChange().endsWith("%"));
		assertTrue(NumberUtils.isNumber(q.getLow()));
		assertTrue(NumberUtils.isNumber(q.getHigh()));
		assertNotEquals("NA", q.getLastUpdate());
		assertTrue("PE of 941 > 1", NumberUtils.toDouble(q.getPe()) > 1);
		assertTrue(q.getYield().endsWith("%"));
		assertTrue("Yield of 941 < 10%", NumberUtils.toDouble(q.getYield().replace("%","")) < 10);
		assertNotEquals("NA", q.getNAV());
		assertTrue(NumberUtils.isNumber(q.getYearLow()));
		assertTrue(NumberUtils.isNumber(q.getYearHigh()));

		log.debug("getStockQuote_Given941_ShouldReturn941StockQuote took: " + timer.stop());
	}

	private boolean tooEarlyTooTest() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));
		if (c.get(Calendar.HOUR_OF_DAY) < 9 || c.get(Calendar.MINUTE) < 20)
			return true;
		else
			return false;
	}

}