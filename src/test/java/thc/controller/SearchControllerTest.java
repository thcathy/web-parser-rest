package thc.controller;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import thc.domain.WebItem;
import thc.parser.search.GoogleImageSearchRequest;
import thc.service.RestParseService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class SearchControllerTest {
    @Mock
    RestParseService parseService;

    SearchController controller;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new SearchController();
        controller.parseService = parseService;
        GoogleImageSearchRequest.keys = Iterables.cycle("key").iterator();
    }

    @Test
    public void httpReturnIsCached() {
        var webItemList = List.of(new WebItem("url", "", 100, 100, ""));
        when(parseService.process(any())).thenReturn(Mono.just(webItemList), Mono.just(Collections.EMPTY_LIST));

        var result = (List<WebItem>) controller.searchImage("any", null, 1).block();
        assertThat(result.size()).isEqualTo(1);
        assertThat((result.get(0)).url).isEqualTo("url");

        var result2 = (List<WebItem>) controller.searchImage("any", null, 1).block();
        assertThat(result2.size()).isEqualTo(1);
        assertThat((result2.get(0)).url).isEqualTo("url");
    }

    @Test
    public void differentQueryIsNotCached() {
        var webItemList = List.of(new WebItem("url", "", 100, 100, ""));
        when(parseService.process(any())).thenReturn(Mono.just(webItemList), Mono.just(Collections.EMPTY_LIST));

        var result = (List<WebItem>) controller.searchImage("any", null, 1).block();
        assertThat(result.size()).isEqualTo(1);
        assertThat((result.get(0)).url).isEqualTo("url");

        var result2 = (List<WebItem>) controller.searchImage("any", null, 11).block();
        assertThat(result2.size()).isEqualTo(0);
    }


}
