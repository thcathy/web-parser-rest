package thc.controller;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import thc.domain.WebItem;
import thc.parser.search.GoogleImageSearchRequest;
import thc.service.HttpParseService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SearchControllerTest {
    @Mock
    HttpParseService parseService;

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
        when(parseService.process(any())).thenReturn(CompletableFuture.supplyAsync(() -> webItemList));
        when(parseService.processFlux(any())).thenCallRealMethod();

        var result = controller.searchImage("any").block();
        assertThat(result.size()).isEqualTo(1);
        assertThat(((WebItem)result.get(0)).url).isEqualTo("url");

        var result2 = controller.searchImage("any").block();
        assertThat(result2.size()).isEqualTo(1);
        assertThat(((WebItem)result2.get(0)).url).isEqualTo("url");
        Mockito.verify(parseService, times(1)).process(any());
    }

}