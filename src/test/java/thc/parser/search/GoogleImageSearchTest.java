package thc.parser.search;

import com.google.common.base.Stopwatch;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import thc.domain.WebItem;
import thc.service.HttpService;
import thc.unirest.UnirestSetup;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class GoogleImageSearchTest {
	private Logger log = LoggerFactory.getLogger(GoogleImageSearchTest.class);

    HttpService httpService = new HttpService();

	static {
		UnirestSetup.setupAll();
		GoogleImageSearch.setAPIKeys(System.getProperty("googleapi.key"));
	}

	@Test
	public void query_shouldReturnWebItems() {
		Stopwatch timer = Stopwatch.createStarted();

        HttpRequest request = GoogleImageSearch.createRequest("book+clipart");
		List<WebItem> items = httpService.queryAsync(request::asJsonAsync, GoogleImageSearch::parse).join();

		assertEquals(10, items.size());
		items.forEach(this::checkItem);

        log.info("query_shouldReturnWebItems took: {}", timer.stop());
	}

	@Test(expected = IllegalArgumentException.class)
	public void query_givenNoAPIKey_shouldThrowException() {
		Iterator<String> keys = GoogleImageSearch.keys;

		try {
			GoogleImageSearch.keys = null;
			HttpRequest request = GoogleImageSearch.createRequest("book");
		} finally {
			GoogleImageSearch.keys = keys;
		}
	}

	@Test
	public void parse_givenFailedResponse_shouldReturnEmptyList() {
		HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
		when(mockResponse.getStatus()).thenReturn(HttpStatus.SC_BAD_REQUEST);

		List result = GoogleImageSearch.parse(mockResponse);
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