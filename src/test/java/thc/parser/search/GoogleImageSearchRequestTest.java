package thc.parser.search;

import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import thc.domain.WebItem;
import thc.service.RestParseService;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GoogleImageSearchRequestTest {
	private Logger log = LoggerFactory.getLogger(GoogleImageSearchRequestTest.class);

	@Mock
	RestParseService parseService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		GoogleImageSearchRequest.keys = Iterables.cycle("key").iterator();
		GoogleImageSearchRequest.setAPIKeys(System.getProperty("googleapi.key"));
	}

	@Test
	public void query_shouldReturnWebItems() {
		var webItemList = List.of(new WebItem("url", "", 100, 100, ""));
		when(parseService.process(any())).thenReturn(Mono.just(webItemList), Mono.just(Collections.EMPTY_LIST));

		List<WebItem> items = (List<WebItem>) parseService.process(
				new GoogleImageSearchRequest("book+clipart").setStart(1)
		).block();
		assertEquals(1, items.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void query_givenNoAPIKey_shouldThrowException() {
		Iterator<String> keys = GoogleImageSearchRequest.keys;

		try {
			GoogleImageSearchRequest.keys = null;
			GoogleImageSearchRequest request = new GoogleImageSearchRequest("book");
		} finally {
			GoogleImageSearchRequest.keys = keys;
		}
	}

	@Test
	public void parse_givenFailedResponse_shouldReturnEmptyList() {
		List result = new GoogleImageSearchRequest("").parseResponse(null).block();
		assertTrue(CollectionUtils.isEmpty(result));
	}

	@Test
	public void givenAllImageSize_shouldNotSetImageSizeInParam() {
		var request = new GoogleImageSearchRequest("").setImgSize("all");
		assertFalse(request.queryParams().containsKey("imgSize"));
	}


}
