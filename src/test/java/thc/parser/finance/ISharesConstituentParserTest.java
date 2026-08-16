package thc.parser.finance;

import org.junit.Test;
import thc.WebParserRestApplication;
import thc.constant.FinancialConstants;
import thc.service.HttpParseService;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static thc.constant.FinancialConstants.IndexCode.MSCIHK;

public class ISharesConstituentParserTest {

	HttpParseService parserService = new HttpParseService(WebParserRestApplication.httpClient());

	private List<String> queryConstituents(FinancialConstants.IndexCode index) {
		return parserService.processFlux(index).block();
	}

	@Test
	public void parseMSCIHK_ShouldReturnCorrectStockCodes() {
		var result = queryConstituents(MSCIHK);
		assertTrue("MSCI HK index should contain 2",result.contains("2"));
		assertTrue("MSCI HK index should contain 388",result.contains("388"));
		assertTrue("MSCI HK index should contain over 20 stocks", result.size() >= 20);
		assertFalse("Should not contain MPEL", result.contains("MPEL"));
		assertFalse("Should not contain HKD", result.contains("HKD"));
		assertFalse("Should not return any --", result.contains("--"));
		assertFalse("Should not contain US ADR FUTU", result.contains("FUTU"));
	}

	@Test
	public void parseMSCIHK_shouldKeepHkexTickersOnly() {
		var csv = """
				iShares MSCI Hong Kong ETF
				Fund Holdings as of,"Aug 13, 2026"

				Ticker,Name,Exchange
				"2","CLP HOLDINGS","Hong Kong Exchanges And Clearing Ltd"
				"388","HONG KONG EXCHANGES AND CLEARING","Hong Kong Exchanges And Clearing Ltd"
				"388","HONG KONG EXCHANGES AND CLEARING","Hong Kong Exchanges And Clearing Ltd"
				"FUTU","FUTU HOLDINGS ADR","NASDAQ"
				"J36","JARDINE MATHESON HOLDINGS LTD","Singapore Exchange"
				"HKD","HKD CASH","-"
				""";

		assertEquals(List.of("2", "388"), parse(csv));
	}

	@Test
	public void parseMSCIHK_shouldReturnEmptyWhenHeaderMissing() {
		assertEquals(List.of(), parse("not a holdings file\n"));
	}

	private List<String> parse(String csv) {
		return ISharesConstituentParser.parseMSCIHK(
				new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)));
	}
}
