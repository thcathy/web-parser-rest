package thc.parser.finance;

import org.junit.Test;
import thc.WebParserRestApplication;
import thc.service.HttpParseService;

import java.time.LocalDate;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeHistoryQuoteRequestTest {
    HttpParseService parseService = new HttpParseService(WebParserRestApplication.httpClient());

    @Test
    public void GetPast30Date2800_ShouldReturnPriceOver10() {
        var toDate = Calendar.getInstance();
        var fromDate = Calendar.getInstance();
        fromDate.add(Calendar.DATE, -30);
        var today = LocalDate.now();

        var result = parseService.processFlux(new RangeHistoryQuoteRequest("2800", fromDate, toDate)).block();
        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(10);
        assertThat(result.stream().filter(v -> v.adjClose() > 10).count()).isGreaterThan(15);
        assertThat(result.stream().filter(v -> v.close() > 10).count()).isGreaterThan(15);
        result.forEach(v -> assertThat(v.date()).isBeforeOrEqualTo(today));
    }
}
