package thc.parser.finance;

import org.junit.Test;
import thc.WebParserRestApplication;
import thc.constant.FinancialConstants;
import thc.service.HttpParseService;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static thc.constant.FinancialConstants.IndexCode.MSCIChina;
import static thc.constant.FinancialConstants.IndexCode.MSCIHK;

public class ISharesConstituentParserTest {

	HttpParseService parserService = new HttpParseService(WebParserRestApplication.httpClient());

	@Test
	public void parseMSCIChina_ShouldReturnCorrectStockCodes() {
		List<String> result = queryConstituents(MSCIChina);

		assertTrue("MSCI China index should contain over 50 stocks", result.size() > 50);
		assertTrue("MSCI China index should contain 941",result.contains("941"));
		assertTrue("MSCI China index should contain 2628",result.contains("2628"));
		assertTrue("MSCI China index should contain 857",result.contains("857"));
		assertTrue("MSCI China index should contain 992",result.contains("992"));
		assertFalse("Should not return any --", result.contains("--"));	
	}

	private List<String> queryConstituents(FinancialConstants.IndexCode index) {
		return parserService.process(index).join();
	}

	@Test
	public void parseMSCIHK_ShouldReturnCorrectStockCodes() {
		List<String> result = queryConstituents(MSCIHK);
		assertTrue("MSCI HK index should contain 11",result.contains("11"));
		assertTrue("MSCI HK index should contain 2",result.contains("2"));
		assertTrue("MSCI HK index should contain 388",result.contains("388"));
		assertTrue("MSCI HK index should contain over 30 stocks", result.size() > 30);
		assertFalse("Should not contain MPEL", result.contains("MPEL"));
		assertFalse("Should not contain HKD", result.contains("HKD"));
		assertFalse("Should not return any --", result.contains("--"));
	}
}