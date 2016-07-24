package thc.controller;

import com.google.common.base.Stopwatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import thc.Utils.TestUtils;
import thc.WebParserRestApplication;
import thc.constant.FinancialConstants;
import thc.domain.StockQuote;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static thc.Utils.TestUtils.containCode;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebParserRestApplication.class)
public class QuoteControllerIntegrationTest {
	private Logger log = LoggerFactory.getLogger(QuoteControllerIntegrationTest.class);

    @Autowired QuoteController quoteController;

	@Test
	public void indexQuotes_shouldReturnMajorIndexes() throws Exception {
		Stopwatch timer = Stopwatch.createStarted();
		
		List<StockQuote> indexes = quoteController.indexQuotes();

		log.info("indexes_shouldReturnMajorIndexes took: {}", timer.stop());

		assertTrue(TestUtils.containCode("HSI", indexes));
		assertTrue(TestUtils.containCode("HSCEI", indexes));
		assertTrue(TestUtils.containCode("SSECI", indexes));
		assertTrue(TestUtils.containCode("CSI300", indexes));
	}
	
    @Test
	public void hkquotes_givenHsiCodes_shouldReturnListOfStockQuote() throws Exception {
		Stopwatch timer = Stopwatch.createStarted();

		List<StockQuote> quotes = quoteController.hkQuotes("2800,700,5,1299,941,939,1398,3988,1,388,2318,883,2,386,16,1113,2628,823,857,3,11,6,688,2388,267,4,1928,66,27,1044,1109,1088,762,19,12,1038,17,23,3328,83,151,2319,992,101,1880,144,836,494,135,322,293");
		
		log.info("hkquotes_givenHsiCodes_shouldReturnListOfStockQuote took: {}", timer.stop());

		assertTrue(quotes.size() > 30);
		assertTrue(containCode("02800", quotes));
		assertTrue(containCode("00005", quotes));
		assertTrue(containCode("00939", quotes));
	}

	@Test
	public void getEachIndexConstituents_shouldReturnSomeStocks() {
        Stopwatch timer = Stopwatch.createStarted();

        List<String> constituents;

        constituents= quoteController.indexConstituents("HSI");
        assertTrue(constituents.size() > 30);
        assertTrue(containList("5", constituents));
        assertTrue(containList("66", constituents));
        assertTrue(containList("388", constituents));

        constituents= quoteController.indexConstituents("HSCEI");
        assertTrue(constituents.size() > 30);
        assertTrue(containList("168", constituents));
        assertTrue(containList("753", constituents));
        assertTrue(containList("3968", constituents));

        constituents= quoteController.indexConstituents("HCCI");
        assertTrue(constituents.size() > 20);
        assertTrue(containList("144", constituents));
        assertTrue(containList("2319", constituents));
        assertTrue(containList("392", constituents));

        constituents= quoteController.indexConstituents("MSCIChina");
        assertTrue(constituents.size() > 50);
        assertTrue(containList("753", constituents));
        assertTrue(containList("941", constituents));
        assertTrue(containList("916", constituents));

        constituents= quoteController.indexConstituents("MSCIHK");
        assertTrue(constituents.size() > 40);
        assertTrue(containList("1299", constituents));
        assertTrue(containList("823", constituents));
        assertTrue(containList("16", constituents));

        log.info("getEachIndexConstituents_shouldReturnSomeStocks took: {}", timer.stop());
    }

    @Test
    public void hsinetReport_shouldReturnTwoHSIndex() throws ParseException {
        Stopwatch timer = Stopwatch.createStarted();

        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        List<StockQuote> reports = quoteController.getHsiNetReportsClosestTo(today);
        assertEquals(2, reports.size());

        StockQuote hsiReport = reports.get(0);
        StockQuote hsceiReport = reports.get(1);

        assertEquals(FinancialConstants.IndexCode.HSI.toString(), hsiReport.getStockCode());
        assertTrue(hsiReport.getPriceDoubleValue() > 1);
        assertEquals(FinancialConstants.IndexCode.HSCEI.toString(), hsceiReport.getStockCode());
        assertTrue(hsceiReport.getPriceDoubleValue() > 1);

        log.info("hsinetReport_shouldReturnTwoHSIndex took: {}", timer.stop());
    }

    private boolean containList(String s, List<String> constituents) {
        return constituents.stream().anyMatch(x->s.equals(x));
    }
}
