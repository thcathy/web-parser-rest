package thc.parser.finance;

import org.junit.Test;
import thc.WebParserRestApplication;
import thc.domain.MonetaryBase;
import thc.service.HttpParseService;

import static org.junit.Assert.assertEquals;


public class HKMAMonetaryBaseRequestTest {
	HttpParseService parserService = new HttpParseService(WebParserRestApplication.httpClient());

	@Test
    public void retrieveMonetaryBase_GivenNormalDate_ShouldReturnMonetaryBase() throws Exception {
		MonetaryBase result = parserService.process(new HKMAMonetaryBaseRequest("20160722")).join().get();

		assertEquals(370715, result.indebtedness,0);
		assertEquals(11892, result.notes,0);
		assertEquals(287470, result.closingBalance,0);
		assertEquals(934941, result.exchangeFund,0);
		assertEquals(1605018, result.total,0);
	}

}