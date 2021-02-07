package thc.parser.finance;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.Utils.TestUtils;
import thc.domain.StockQuote;
import thc.service.JsoupParseService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static thc.domain.StockQuote.NA;

public class AastockStockQuoteRequestTest {
	private Logger log = LoggerFactory.getLogger(AastockStockQuoteRequestTest.class);

    JsoupParseService parserService = new JsoupParseService();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	@Test
	public void getStockQuote_Given941_ShouldReturn941StockQuote() {
        Stopwatch timer = Stopwatch.createStarted();

        StockQuote q = parserService.process(new AastockStockQuoteRequest("941")).block();
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
    public void getStockQuote_Given2800_ShouldReturn2800StockQuote() {
        Stopwatch timer = Stopwatch.createStarted();

        StockQuote q = parserService.process(new AastockStockQuoteRequest("2800")).block();
        log.debug("StockQuote: ", q);

        assertEquals("2800", q.getStockCode());
        assertEquals("TRACKER FUND", q.getStockName());
        assertThat(Double.valueOf(q.getPrice()), greaterThan(20.0));
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

    @Test
    public void getStockQuote_Given7288_ShouldReturnCode7288() {
        StockQuote q = parserService.process(new AastockStockQuoteRequest("7288")).block();
        log.debug("StockQuote: ", q);

        assertEquals("7288", q.getStockCode());
    }

    @Test
    @Ignore // CBBC number expire too offen
    public void getStockQuote_GivenCBBCCode_ShouldReturnQuote() throws Exception {
	    String cbbcCode = getOneCBBCCodeFromHKEX();
        StockQuote q = parserService.process(new AastockStockQuoteRequest(cbbcCode)).block();
        log.debug("StockQuote: ", q);

        assertEquals(cbbcCode, q.getStockCode());
        assertNotEquals("NA", q.getPrice());
    }

    private String getOneCBBCCodeFromHKEX() throws Exception {
        InputStream input = new URL("https://www.hkex.com.hk/eng/cbbc/search/cbbcFullList.csv").openStream();

        var reader = new InputStreamReader(input, "UTF-16");
        var bufferedReader = new BufferedReader(reader);
        Optional<String> cbbc = Optional.empty();
        while (!cbbc.isPresent()) {
            cbbc = isListedCBBC(bufferedReader.readLine());
        }
        log.debug("CBBC found: {}", cbbc);
        return cbbc.get().split("\\s+")[0];
    }

    private Optional<String> isListedCBBC(String data) {
	    log.debug("Check is listed CBBC: {}", data);
        long now = new Date().getTime();
	    try {
	        var dataArray = data.split("\\s+");
            var date = dateFormat.parse(dataArray[6]);
            if (date.getTime() < now) {
                return Optional.of(data);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
	        return Optional.empty();
        }
    }

}
