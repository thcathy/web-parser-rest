package thc.parser.finance;

import org.junit.Test;
import thc.WebParserRestApplication;
import thc.constant.FinancialConstants;
import thc.domain.StockQuote;
import thc.service.HttpParseService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static thc.constant.FinancialConstants.IndexCode.HSCEI;
import static thc.constant.FinancialConstants.IndexCode.HSI;

public class HSINetRequestTest {
	HttpParseService parserService = new HttpParseService(WebParserRestApplication.httpClient());

	@Test
	public void parse_givenYesterday_ShouldReturnHSCEI() {
		Optional<Boolean> hsceiFound = getLatestIndexReport(HSCEI);

		assertTrue(hsceiFound.get());
	}

	@Test
	public void parse_givenYesterday_ShouldReturnHSI() {
		Optional<Boolean> hsiFound = getLatestIndexReport(HSI);

		assertTrue(hsiFound.get());
	}

	@Test
	public void parse_givenVeryOldDate_ShouldNotFound() {
		HSINetRequest request = new HSINetRequest(HSI, "20160102");
		Optional<StockQuote> result = parserService.process(request).join();
		assertFalse(result.isPresent());
	}

	private Optional<Boolean> getLatestIndexReport(FinancialConstants.IndexCode code) {
		return TenPreviousDayStream()
				.map(t -> new HSINetRequest(code, t))
				.map(r -> parserService.process(r).join())
				.map(quote -> quote.isPresent() && quote.get().getPriceDoubleValue() > 1)
				.filter(x -> x)
				.findFirst();
	}

	private Stream<String> TenPreviousDayStream() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

		return IntStream.range(0, 10).mapToObj(i-> {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -i-1);
			return dateFormat.format(c.getTime());
		});
	}
}