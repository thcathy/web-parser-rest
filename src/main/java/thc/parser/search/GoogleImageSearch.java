package thc.parser.search;

import com.google.common.collect.Iterables;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.WebItem;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GoogleImageSearch {
	protected static final Logger log = LoggerFactory.getLogger(GoogleImageSearch.class);

	public static final String KEY_SEPARATOR = ",";
	public static final String URL = "https://www.googleapis.com/customsearch/v1?cx=011552421082581973471%3A0ge1n0sksf4&filter=1&safe=medium&searchType=image";
	public static final int NUM_RESULT = 10;
	public static volatile Iterator<String> keys;

	public static HttpRequest createRequest(String query) {
		if (keys == null) throw new IllegalArgumentException("Cannot create google image search request without key");

		return Unirest.get(URL)
				.queryString("q", query)
				.queryString("imgSize", "medium")
				.queryString("imgType", "clipart")
				.queryString("num", NUM_RESULT)
				.queryString("key", keys.next());
	}

	public static void setAPIKeys(String keys) {
		GoogleImageSearch.keys = Iterables.cycle(keys.split(KEY_SEPARATOR)).iterator();
	}

	public static List<WebItem> parse(HttpResponse<JsonNode> response) {
		try {
			log.info("start parse response status: {}", response.getStatus());
			if (response.getStatus() > HttpStatus.SC_OK)
				throw new RuntimeException("Google image api return fail: {}" + response.getBody());

			if (!response.getBody().getObject().has("items")) {
				log.warn("No item found from body", response.getBody().getObject());
				return Collections.EMPTY_LIST;
			}

			JSONArray items = response.getBody().getObject().getJSONArray("items");
			log.debug("Query result items: {}", items.toString());

			return IntStream.range(0, NUM_RESULT)
					.mapToObj(items::getJSONObject)
					.map(GoogleImageSearch::toWebItem)
					.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("fail to parse response", e);
			return Collections.EMPTY_LIST;
		}
	}

	private static WebItem toWebItem(JSONObject src) {
		return new WebItem(
				src.getString("link"),
				src.getString("mime"),
				src.getJSONObject("image").getInt("height"),
				src.getJSONObject("image").getInt("width"),
				src.getJSONObject("image").getString("thumbnailLink")
		);
	}

}
