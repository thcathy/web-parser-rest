package thc.parser.finance;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.Utils.TestUtils;
import thc.domain.StockQuote;
import thc.service.HttpService;

import static org.junit.Assert.*;

public class EtnetStockQuoteParserTest {
	private Logger log = LoggerFactory.getLogger(EtnetStockQuoteParserTest.class);

	HttpService httpService = new HttpService();

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() {
		Stopwatch timer = Stopwatch.createStarted();

		StockQuote q = httpService
				.queryAsync(
						EtnetStockQuoteParser.createRequest("941")::asBinaryAsync, 
						EtnetStockQuoteParser::parse)
				.join().get();

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