package thc.parser.finance;

import org.junit.Test;
import thc.service.HttpService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HistoryQuoteParserTest {
	HttpService httpService = new HttpService();

	@Test
    public void getPreviousYearQuote_GivenLastYear0001_ShouldReturnPriceOver50() throws ExecutionException, InterruptedException {
		HistoryQuoteParser parser = new HistoryQuoteParser("1", 1);

		BigDecimal result = httpService.getAsync(parser.url(), parser::parse).join().get();
		assertTrue(result.doubleValue() > 50);
	}
	
	@Test
	public void getQuoteAtDate_GivenHSIAndHSCEI_ShouldReturnCorrectPrice() {
		Calendar c = Calendar.getInstance();
		c.set(2013, 5, 10);	// 10 Jun 2013

		HistoryQuoteParser hsiParser = new HistoryQuoteParser("%5EHSI", c, c);
		CompletableFuture<Optional<BigDecimal>> hsi = httpService.getAsync(hsiParser.url(), hsiParser::parse);

		HistoryQuoteParser hsceiParser = new HistoryQuoteParser("%5EHSCE", c, c);
		CompletableFuture<Optional<BigDecimal>> hscei = httpService.getAsync(hsceiParser.url(), hsceiParser::parse);

		assertEquals(21615.09, hsi.join().get().doubleValue(), 2);
		assertEquals(10126.97, hscei.join().get().doubleValue(), 2);
	}
}
