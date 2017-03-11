package thc.parser.search;

import com.google.common.base.Stopwatch;
import com.mashape.unirest.request.HttpRequest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.WebItem;
import thc.service.HttpService;
import thc.unirest.UnirestSetup;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	private void checkItem(WebItem webItem) {
		log.info("WebItem: {}", webItem);
		assertTrue(webItem.url.startsWith("http"));
		assertTrue(webItem.mime.startsWith("image"));
		assertTrue(webItem.imageHeight > 10);
		assertTrue(webItem.imageWidth > 10);
	}
}