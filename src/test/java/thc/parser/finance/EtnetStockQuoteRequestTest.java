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

import static org.junit.Assert.*;

public class EtnetStockQuoteRequestTest {
	private Logger log = LoggerFactory.getLogger(EtnetStockQuoteRequestTest.class);

	HttpParseService parserService = new HttpParseService(Dsl.asyncHttpClient());

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() {
		Stopwatch timer = Stopwatch.createStarted();

		StockQuote q = parserService.process(new EtnetStockQuoteRequest("941")).join().get();

		log.info("StockQuote: ", q);
		assertEquals("941", q.getStockCode());
		assertTrue(NumberUtils.isNumber(q.getPrice()));
		assertNotEquals("NA", q.getLastUpdate());
		assertNotEquals("NA", q.getNAV());
		assertTrue(NumberUtils.isNumber(q.getYearLow()));
		assertTrue(NumberUtils.isNumber(q.getYearHigh()));

		if (TestUtils.withIntraDayData()) {
			assertTrue(NumberUtils.isNumber(q.getChangeAmount().replace("+", "").replace("-", "")));
			assertTrue(q.getChange().endsWith("%"));
			assertTrue(NumberUtils.isNumber(q.getLow()));
			assertTrue(NumberUtils.isNumber(q.getHigh()));
			assertTrue("PE of 941 > 1", NumberUtils.toDouble(q.getPe()) > 1);
			assertTrue(q.getYield().endsWith("%"));
			assertTrue("Yield of 941 < 10%", NumberUtils.toDouble(q.getYield().replace("%","")) < 10);
		}

		log.debug("getStockQuote_Given941_ShouldReturn941StockQuote took: " + timer.stop());
	}

}