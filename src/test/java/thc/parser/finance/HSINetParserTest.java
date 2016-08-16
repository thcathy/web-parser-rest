package thc.parser.finance;

import org.junit.Test;
import thc.constant.FinancialConstants;
import thc.domain.StockQuote;
import thc.service.HttpService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static thc.constant.FinancialConstants.IndexCode.HSCEI;
import static thc.constant.FinancialConstants.IndexCode.HSI;

public class HSINetParserTest {

	HttpService httpService = new HttpService();

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
		HSINetParser parser = new HSINetParser(HSI, "20160102");
		Optional<StockQuote> result = submitHttpRequest(parser).join();
		assertFalse(result.isPresent());
	}

	private Optional<Boolean> getLatestIndexReport(FinancialConstants.IndexCode code) {
		return TenPreviousDayStream()
				.map(t -> new HSINetParser(code, t))
				.map(r ->
					submitHttpRequest(r).join()
				)
				.map(quote -> quote.isPresent() && quote.get().getPriceDoubleValue() > 1)
				.filter(x -> x)
				.findFirst();
	}

	private CompletableFuture<Optional<StockQuote>> submitHttpRequest(HSINetParser r) {
		return httpService
				.queryAsync(
						r.createRequest()::asStringAsync,
						r::parse);
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