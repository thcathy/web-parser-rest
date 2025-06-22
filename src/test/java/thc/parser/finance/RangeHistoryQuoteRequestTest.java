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

        var result = parseService.processFlux(new RangeHistoryQuoteRequest(
                "2800", "XHKG", fromDate, toDate)).block();
        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(10);
        assertThat(result.stream().filter(v -> v.adjClose() > 10).count()).isGreaterThan(15);
        assertThat(result.stream().filter(v -> v.close() > 10).count()).isGreaterThan(15);
        result.forEach(v -> assertThat(v.date()).isBeforeOrEqualTo(today));
    }

    @Test
    public void GetPast20DateQQQ_ShouldReturnPriceOver500() {
        var toDate = Calendar.getInstance();
        var fromDate = Calendar.getInstance();
        fromDate.add(Calendar.DATE, -20);
        var today = LocalDate.now();

        var result = parseService.processFlux(new RangeHistoryQuoteRequest(
                "QQQ", "XNAS", fromDate, toDate)).block();
        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(12);
        assertThat(result.stream().filter(v -> v.adjClose() > 400).count()).isGreaterThan(12);
        assertThat(result.stream().filter(v -> v.close() > 400).count()).isGreaterThan(12);
        result.forEach(v -> assertThat(v.date()).isBeforeOrEqualTo(today));
    }

    @Test
    public void GetPast20DateIBIT_ShouldReturnPriceOver500() {
        var toDate = Calendar.getInstance();
        var fromDate = Calendar.getInstance();
        fromDate.add(Calendar.DATE, -20);
        var today = LocalDate.now();

        var result = parseService.processFlux(new RangeHistoryQuoteRequest(
                "IBIT", "XNAS", fromDate, toDate)).block();
        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThan(12);
        assertThat(result.stream().filter(v -> v.adjClose() > 40).count()).isGreaterThan(12);
        assertThat(result.stream().filter(v -> v.close() > 40).count()).isGreaterThan(12);
        result.forEach(v -> assertThat(v.date()).isBeforeOrEqualTo(today));
    }
}
