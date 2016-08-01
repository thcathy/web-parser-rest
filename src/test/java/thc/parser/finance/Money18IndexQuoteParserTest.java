package thc.parser.finance;

import com.google.common.base.Stopwatch;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.Utils.TestUtils;
import thc.domain.StockQuote;
import thc.service.HttpService;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class Money18IndexQuoteParserTest {
	private Logger log = LoggerFactory.getLogger(Money18IndexQuoteParserTest.class);

    HttpService httpService = new HttpService();

	@Test
	public void shouldReturnAllIndexQuotes() throws UnirestException, IOException {
		Stopwatch timer = Stopwatch.createStarted();
		
        List<StockQuote> quotes = httpService.queryAsync(Money18IndexQuoteParser.createRequest()::asStringAsync, Money18IndexQuoteParser::parse).join();
        
	log.info("shouldReturnAllIndexQuotes took: " + timer.stop());
        
        assertTrue(TestUtils.containCode("HSI", quotes));
        assertTrue(TestUtils.containCode("HSCEI", quotes));
        assertTrue(TestUtils.containCode("SSECI", quotes));
        assertTrue(TestUtils.containCode("CSI300", quotes));
    }

}
