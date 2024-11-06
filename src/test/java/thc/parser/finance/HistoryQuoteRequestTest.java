package thc.parser.finance;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import thc.WebParserRestApplication;
import thc.service.HttpParseService;
import thc.service.JsoupParseService;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.*;

public class HistoryQuoteRequestTest {
	HttpParseService parseService = new HttpParseService(WebParserRestApplication.httpClient());

	@Test
    public void getPreviousYearQuote_GivenLastYear0001_ShouldReturnPriceOver10() {
		BigDecimal result = parseService.processFlux(new HistoryQuoteRequest("00001", 1)).block();
		Assertions.assertThat(result.doubleValue()).isGreaterThan(10l);
	}
	
	@Test
	public void getQuoteAtDate_GivenHSIAndHSCEI_ShouldReturnCorrectPrice() {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(2016, 5, 10);	// 10 Jun 2016

		BigDecimal hsi = parseService.processFlux(new HistoryQuoteRequest("%5EHSI", c, c)).block();
		BigDecimal hscei = parseService.processFlux(new HistoryQuoteRequest("%5EHSCE", c, c)).block();

		assertEquals(21042.64	, hsi.doubleValue(), 2);
		assertEquals(8831.97, hscei.doubleValue(), 2);
	}
}
