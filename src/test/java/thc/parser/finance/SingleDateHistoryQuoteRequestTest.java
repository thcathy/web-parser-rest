package thc.parser.finance;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import thc.WebParserRestApplication;
import thc.service.HttpParseService;

import java.math.BigDecimal;

public class SingleDateHistoryQuoteRequestTest {
	HttpParseService parseService = new HttpParseService(WebParserRestApplication.httpClient());

	@Test
    public void getPreviousYearQuote_GivenLastYear0001_ShouldReturnPriceOver10() {
		BigDecimal result = parseService.processFlux(new SingleDateHistoryQuoteRequest("00001", 1)).block();
		Assertions.assertThat(result.doubleValue()).isGreaterThan(10l);
	}
}
