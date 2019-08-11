package thc.parser.finance;

import org.junit.Test;
import thc.domain.MonetaryBase;
import thc.service.JsoupParseService;

import static org.junit.Assert.assertEquals;


public class HKMAMonetaryBaseRequestTest {
	JsoupParseService parseService = new JsoupParseService();

	@Test
    public void retrieveMonetaryBase_GivenNormalDate_ShouldReturnMonetaryBase() throws Exception {
		MonetaryBase result = parseService.process(new HKMAMonetaryBaseRequest("20160722")).block();

		assertEquals(370715, result.indebtedness,0);
		assertEquals(11892, result.notes,0);
		assertEquals(287470, result.closingBalance,0);
		assertEquals(934941, result.exchangeFund,0);
		assertEquals(1605018, result.total,0);
	}

}