package thc.parser.finance;

import org.junit.Test;
import thc.domain.MonetaryBase;
import thc.service.HttpService;

import static org.junit.Assert.assertEquals;


public class HKMAMonetaryBaseParserTest {
	HttpService httpService = new HttpService();

	@Test
    public void retrieveMonetaryBase_GivenNormalDate_ShouldReturnMonetaryBase() throws Exception {
    	HKMAMonetaryBaseParser parser = new HKMAMonetaryBaseParser("20160722");

		MonetaryBase result = httpService.getAsync(parser.url(), parser::parse).join().get();

		assertEquals(370715, result.indebtedness,0);
		assertEquals(11892, result.notes,0);
		assertEquals(287470, result.closingBalance,0);
		assertEquals(934941, result.exchangeFund,0);
		assertEquals(1605018, result.total,0);
	}
		
}