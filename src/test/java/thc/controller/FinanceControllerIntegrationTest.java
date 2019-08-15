package thc.controller;

import com.google.common.base.Stopwatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import thc.Utils.TestUtils;
import thc.WebParserRestApplication;
import thc.domain.MonetaryBase;
import thc.domain.StockQuote;

import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;
import static thc.Utils.TestUtils.containCode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebParserRestApplication.class)
public class FinanceControllerIntegrationTest {
	private Logger log = LoggerFactory.getLogger(FinanceControllerIntegrationTest.class);

    @Autowired
    FinanceController financeController;

	@Test
	public void indexQuotes_shouldReturnMajorIndexes() {
		Stopwatch timer = Stopwatch.createStarted();
		
		List<StockQuote> indexes = financeController.indexQuotes().block();

		log.info("indexes_shouldReturnMajorIndexes took: {}", timer.stop());

		assertTrue(TestUtils.containCode("HSI", indexes));
		assertTrue(TestUtils.containCode("HSCEI", indexes));
		assertTrue(TestUtils.containCode("SSECI", indexes));
		assertTrue(TestUtils.containCode("CSI300", indexes));
	}
	
    @Test
	public void hkquotes_givenHsiCodes_shouldReturnListOfStockQuote() throws Exception {
		Stopwatch timer = Stopwatch.createStarted();

		List<StockQuote> quotes = financeController.hkQuotes("2800,700,5,1299,941,939,1398,3988,1,388,2318,883,2,386,16,1113,2628,823,857,3,11,6,688,2388,267,4,1928,66,27,1044,1109,1088,762,19,12,1038,17,23,3328,83,151,2319,992,101,1880,144,836,494,135,322,293", null)
                .collectList().block();
		
		log.info("hkquotes_givenHsiCodes_shouldReturnListOfStockQuote took: {}", timer.stop());

		assertTrue(quotes.size() > 30);
		assertTrue(containCode("2800", quotes));
		assertTrue(containCode("5", quotes));
		assertTrue(containCode("939", quotes));
	}

	@Test
    public void hkQuoteSingle_givenHSBC_shouldReturnQuote() {
        Stopwatch timer = Stopwatch.createStarted();

        StockQuote quote = financeController.hkQuoteSingle("941", null).block();

        log.info("hkQuoteSingle_givenHSBC_shouldReturnQuote took: {}", timer.stop());
        log.info("Return quote: {}", quote);

        assertTrue(quote.getStockCode().endsWith("941"));
        assertTrue(quote.getPriceDoubleValue() > 30);
        assertNotEquals(0.0, quote.getLastYearPercentage());
        assertNotEquals(0.0, quote.getLast2YearPercentage());
        assertNotEquals(0.0, quote.getLast3YearPercentage());
        if (TestUtils.withIntraDayData()) {
            assertTrue("High >= Low", Double.valueOf(quote.getHigh()) >= Double.valueOf(quote.getLow()));
            assertTrue("Year High >= Year Low", Double.valueOf(quote.getYearHigh()) >= Double.valueOf(quote.getYearLow()));
        }
    }

	@Test
	public void getEachIndexConstituents_shouldReturnSomeStocks() {
        Stopwatch timer = Stopwatch.createStarted();

        List<String> constituents;

        constituents= financeController.indexConstituents("HSI").block();
        assertTrue(constituents.size() > 30);
        assertTrue(containList("5", constituents));
        assertTrue(containList("66", constituents));
        assertTrue(containList("388", constituents));

        constituents= financeController.indexConstituents("HSCEI").block();
        assertTrue(constituents.size() > 30);
        assertTrue(containList("939", constituents));
        assertTrue(containList("2318", constituents));
        assertTrue(containList("3968", constituents));

        constituents= financeController.indexConstituents("HCCI").block();
        assertTrue(constituents.size() > 20);
        assertTrue(containList("144", constituents));
        assertTrue(containList("2319", constituents));
        assertTrue(containList("392", constituents));

        constituents= financeController.indexConstituents("MSCIChina").block();
        assertTrue(constituents.size() > 50);
        assertTrue(containList("753", constituents));
        assertTrue(containList("941", constituents));
        assertTrue(containList("916", constituents));

        constituents= financeController.indexConstituents("MSCIHK").block();
        assertTrue(constituents.size() > 35);
        assertTrue(containList("1299", constituents));
        assertTrue(containList("823", constituents));
        assertTrue(containList("16", constituents));

        log.info("getEachIndexConstituents_shouldReturnSomeStocks took: {}", timer.stop());
    }

    @Test
    public void hsinetReport_queryOnHoliday_shouldReturnNAStockQuote() {
        List<StockQuote> reports = financeController.getHsiNetReports("20160701").collectList().block();

        assertEquals(2, reports.size());
        assertEquals(StockQuote.NA, reports.get(0).getStockCode());
        assertEquals(StockQuote.NA, reports.get(1).getStockCode());
    }

    @Test
    public void getHKMAReport_giveFutureDate_shouldReturnEmptyReport() throws ParseException {
        MonetaryBase report = financeController.getHKMAReport("21000701").block();
        assertEquals(0, report.exchangeFund, 0.01);
        assertEquals(0, report.closingBalance, 0.01);
        assertEquals(0, report.notes, 0.01);
        assertEquals(0, report.indebtedness, 0.01);

    }

    private boolean containList(String s, List<String> constituents) {
        return constituents.stream().anyMatch(x->s.equals(x));
    }
}
