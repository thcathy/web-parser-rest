package thc.parser.finance;

import org.asynchttpclient.Dsl;
import org.junit.Test;
import thc.service.HttpParseService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HistoryQuoteRequestTest {
	HttpParseService parserService = new HttpParseService(Dsl.asyncHttpClient());

	@Test
    public void getPreviousYearQuote_GivenLastYear0001_ShouldReturnPriceOver50() {
		BigDecimal result = parserService.process(new HistoryQuoteRequest("00001", 1)).join().get();
		assertTrue(result.doubleValue() > 50);
	}
	
	@Test
	public void getQuoteAtDate_GivenHSIAndHSCEI_ShouldReturnCorrectPrice() {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2016, 5, 10);	// 10 Jun 2016

		CompletableFuture<Optional<BigDecimal>> hsi = parserService.process(new HistoryQuoteRequest("%5EHSI", c, c));
		CompletableFuture<Optional<BigDecimal>> hscei = parserService.process(new HistoryQuoteRequest("%5EHSCE", c, c));;

		assertEquals(21042.64	, hsi.join().get().doubleValue(), 2);
		assertEquals(8831.97, hscei.join().get().doubleValue(), 2);
	}
}
