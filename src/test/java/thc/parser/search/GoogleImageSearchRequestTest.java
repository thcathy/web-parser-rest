package thc.parser.search;

import com.google.common.base.Stopwatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import thc.domain.WebItem;
import thc.service.RestParseService;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class GoogleImageSearchRequestTest {
	private Logger log = LoggerFactory.getLogger(GoogleImageSearchRequestTest.class);

	RestParseService parserService = new RestParseService();

	static {
		GoogleImageSearchRequest.setAPIKeys(System.getProperty("googleapi.key"));
	}

	@Test
	public void query_shouldReturnWebItems() {
		Stopwatch timer = Stopwatch.createStarted();

		List<WebItem> items = (List<WebItem>) parserService.process(new GoogleImageSearchRequest("book+clipart")).block();

		assertEquals(10, items.size());
		items.forEach(this::checkItem);

        log.info("query_shouldReturnWebItems took: {}", timer.stop());
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

	private void checkItem(WebItem webItem) {
		log.info("WebItem: {}", webItem);
		assertTrue(webItem.url.startsWith("http"));
		assertTrue(webItem.mime.startsWith("image"));
		assertTrue(webItem.imageHeight > 10);
		assertTrue(webItem.imageWidth > 10);
		assertThat(webItem.thumbnailUrl, containsString("https://"));
	}
}