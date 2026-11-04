package thc.parser.finance;

import org.junit.Test;
import thc.WebParserRestApplication;
import thc.constant.FinancialConstants;
import thc.domain.StockQuote;
import thc.service.HttpParseService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
		Optional<StockQuote> result = parserService.processFlux(request).block();
		assertFalse(result.isPresent());
	}

	@Test
	public void url_usesEncodedPublicDailyReportAndUnpaddedDay() {
		assertEquals(
				"https://www.hsi.com.hk/api/wsit-hsi-ddoc-ea-public-website-proxy/v1/content/gcs/download?resource=%2Fpublic-website%2Fwebapp%2Fcontents%2Fen%2Findexes%2Freport%2Fhsi%2Fidx_3Nov26.csv",
				new HSINetRequest(HSI, "20261103").url());
		assertTrue(new HSINetRequest(HSCEI, "20261104").url().contains("%2Fhscei%2Fidx_4Nov26.csv"));
		assertTrue(new HSINetRequest(HSI, "20261009").url().contains("idx_9Oct26.csv"));
		assertTrue(new HSINetRequest(HSI, "20261010").url().contains("idx_10Oct26.csv"));
	}

	@Test
	public void parse_realUtf16DailyReport_returnsCloseYieldAndPe() throws IOException {
		var hsi = parseFixture(HSI, "hsinet/idx_3Nov26_hsi.csv");
		assertEquals("HSI", hsi.getStockCode());
		assertEquals("20261103", hsi.getLastUpdate());
		assertEquals("23549.31", hsi.getHigh());
		assertEquals("23347.03", hsi.getLow());
		assertEquals("23525.97", hsi.getPrice());
		assertEquals("132.65", hsi.getChangeAmount());
		assertEquals("0.57", hsi.getChange());
		assertEquals("3.11", hsi.getYield());
		assertEquals("13.17", hsi.getPe());

		var hscei = parseFixture(HSCEI, "hsinet/idx_3Nov26_hscei.csv");
		assertEquals("HSCEI", hscei.getStockCode());
		assertEquals("7943.1", hscei.getPrice());
		assertEquals("36.99", hscei.getChangeAmount());
		assertEquals("0.47", hscei.getChange());
		assertEquals("2.94", hscei.getYield());
		assertEquals("11.75", hscei.getPe());
	}

	@Test
	public void parse_utf16Le_doesNotSplitOnCodeUnitsThatLookLikeTabs() {
		// U+5009 is UTF-16LE 09 50. A byte-wise US-ASCII split treats that 0x09 as a tab and shifts close/yield/PE.
		var report = """
				"Trade Date"\t"Index"\t"Index Currency"\t"Daily High"\t"Daily Low"\t"Index Close"\t"Point Change"\t"% Change"\t"Dividend Yield (%)"\t"PE Ratio (times)"
				"20261103"\t"Index\u5009Name"\t"HKD"\t"11"\t"22"\t"23525.97"\t"132.65"\t"0.57"\t"3.11"\t"13.17"
				"20261103"\t"Sub Index"\t"HKD"\t"1"\t"2"\t"1"\t"1"\t"1"\t"9.99"\t"8.88"
				""";

		var quote = new HSINetRequest(HSI, "20261103").parseResponse(utf16Le(report)).get();

		assertEquals("23525.97", quote.getPrice());
		assertEquals("3.11", quote.getYield());
		assertEquals("13.17", quote.getPe());
	}

	@Test
	public void parse_givenBodyWithoutIndexRow_returnsEmpty() {
		var body = utf16Le("not a daily report\n");
		assertFalse(new HSINetRequest(HSI, "20261103").parseResponse(body).isPresent());
	}

	private StockQuote parseFixture(FinancialConstants.IndexCode code, String resource) throws IOException {
		try (var input = getClass().getClassLoader().getResourceAsStream(resource)) {
			assertNotNull(input);
			return new HSINetRequest(code, "20261103").parseResponse(input).get();
		}
	}

	private static ByteArrayInputStream utf16Le(String text) {
		var body = text.getBytes(StandardCharsets.UTF_16LE);
		var bytes = new byte[body.length + 2];
		bytes[0] = (byte) 0xFF;
		bytes[1] = (byte) 0xFE;
		System.arraycopy(body, 0, bytes, 2, body.length);
		return new ByteArrayInputStream(bytes);
	}

	private Optional<Boolean> getLatestIndexReport(FinancialConstants.IndexCode code) {
		return TenPreviousDayStream()
				.map(t -> new HSINetRequest(code, t))
				.map(r -> parserService.processFlux(r).block())
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