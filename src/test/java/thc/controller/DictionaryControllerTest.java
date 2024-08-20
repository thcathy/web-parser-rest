package thc.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import thc.constant.ContentConstants;
import thc.domain.DictionaryResult;
import thc.service.RestParseService;

import java.util.Collections;

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
    public void http_return_is_cached() {
        var dictionaryResult = new DictionaryResult("test", "http://", "", "", "", Collections.emptyList());
        var mono = Mockito.mock(Mono.class);
        when(parseService.process(any())).thenReturn(Mono.just(dictionaryResult), Mono.empty());

        var result = controller.query("test").block();
        assertThat(result.word).isEqualTo("test");

        var result2 = controller.query("test").block();
        assertThat(result2.word).isEqualTo("test"); // second call is return from cache, otherwise the mock return empty
    }

    @Test
    public void cache_empty_result_if_word_cannot_get_from_online() {
        String invalidWord = "@#$%(*@#$%$#";
        when(parseService.process(any())).thenReturn(Mono.empty());

        var result = controller.query(invalidWord).block();
        assertThat(result.word).isEqualTo(invalidWord);
        assertThat(result.pronunciationUrl).isNull();
        assertThat(result.IPA).isEqualTo(ContentConstants.NOT_AVAILABLE);
        assertThat(result.hasPronunciation()).isFalse();

        var result2 = controller.query(invalidWord).block();
        assertThat(result.hashCode()).isEqualTo(result2.hashCode()); // second call is return from cache, otherwise the mock return empty
    }
}
