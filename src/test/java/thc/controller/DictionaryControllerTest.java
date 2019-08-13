package thc.controller;

import reactor.core.publisher.Mono;
import thc.domain.DictionaryResult;
import thc.service.RestParseService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class DictionaryControllerTest {
    @Mock
    RestParseService parseService;

    DictionaryController controller;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new DictionaryController();
        controller.parseService = parseService;
    }

    @Test
    public void httpReturnIsCached() {
        var dictionaryResult = new DictionaryResult("test", "http://", "", "", "");
        var mono = Mockito.mock(Mono.class);
        when(parseService.process(any())).thenReturn(Mono.just(dictionaryResult), Mono.empty());

        var result = controller.query("test").block();
        assertThat(result.word).isEqualTo("test");

        var result2 = controller.query("test").block();
        assertThat(result2.word).isEqualTo("test"); // second call is return from cache, otherwise the mock return empty
    }
}