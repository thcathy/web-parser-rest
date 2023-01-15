package thc.parser.finance;

import org.junit.Test;
import thc.WebParserRestApplication;
import thc.constant.FinancialConstants;
import thc.service.HttpParseService;

import java.util.List;

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
		List<String> result = queryConstituents(MSCIHK);
		assertTrue("MSCI HK index should contain 11",result.contains("11"));
		assertTrue("MSCI HK index should contain 2",result.contains("2"));
		assertTrue("MSCI HK index should contain 388",result.contains("388"));
		assertTrue("MSCI HK index should contain over 30 stocks", result.size() >= 30);
		assertFalse("Should not contain MPEL", result.contains("MPEL"));
		assertFalse("Should not contain HKD", result.contains("HKD"));
		assertFalse("Should not return any --", result.contains("--"));
	}
}
