package thc.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import reactor.core.publisher.Mono;
import thc.parser.JsoupParseRequest;
import thc.parser.finance.AastockStockQuoteRequest;
import thc.parser.finance.YahooStockQuoteRequest;
import thc.service.HttpParseService;
import thc.service.JsoupParseService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FinanceControllerTest {
    @Mock
    HttpParseService httpParseService;

    @Mock
    JsoupParseService jsoupParseService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    FinanceController controller;

    @Before
    public void before() {
        controller = new FinanceController();
        controller.setParseService(httpParseService);
        controller.setJsoupParseService(jsoupParseService);
    }

    @Test
    public void hkQuotes_withSpecificSource_useSpecificParser() {
        when(jsoupParseService.process(any())).thenReturn(Mono.just(Optional.empty()));

        controller.hkQuotes("941,939,2800,2828,5,883", "aastocks").collectList().block();
        verify(jsoupParseService, times(6)).process(any(AastockStockQuoteRequest.class));

        controller.hkQuotes("941,939,2800,2828,5,883", "yahoo").collectList().block();
        verify(jsoupParseService, times(6)).process(any(YahooStockQuoteRequest.class));
    }

    @Test
    public void hkQuotes_givenNullSource_shouldProcessCorrectly() {
        when(jsoupParseService.process(any())).thenReturn(Mono.just(Optional.empty()));

        controller.hkQuotes("941,939,2800,2828,5,883", null).collectList().block();
        verify(jsoupParseService, times(6)).process(any(JsoupParseRequest.class));
    }

    @Test
    public void hkQuotes_givenWrongSource_shouldProcessCorrectly() {
        when(jsoupParseService.process(any())).thenReturn(Mono.just(Optional.empty()));

        controller.hkQuotes("941,939,2800,2828,5,883", "wrong parser").collectList().block();
        verify(jsoupParseService, times(6)).process(any(JsoupParseRequest.class));
    }
}