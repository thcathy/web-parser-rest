package thc.parser.finance;

import org.junit.Test;
import thc.WebParserRestApplication;
import thc.service.HttpParseService;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeHistoryQuoteRequestTest {
    HttpParseService parseService = new HttpParseService(WebParserRestApplication.httpClient());

    @Test
    public void GetPast30Date2800_ShouldReturnPriceOver10() {
        var toDate = Calendar.getInstance();
        var fromDate = Calendar.getInstance();
        fromDate.add(Calendar.DATE, -30);

        var result = parseService.processFlux(new RangeHistoryQuoteRequest("2800", fromDate, toDate)).block();
        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(10);
        result.forEach(v -> assertThat(v).isGreaterThan(new BigDecimal("12.5")));
    }
}
