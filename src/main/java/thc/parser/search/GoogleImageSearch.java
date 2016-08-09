package thc.parser.search;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.WebItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GoogleImageSearch {
	protected static final Logger log = LoggerFactory.getLogger(GoogleImageSearch.class);
	
	public static final String URL = "https://www.googleapis.com/customsearch/v1?cx=011552421082581973471%3A0ge1n0sksf4&filter=1&safe=medium&searchType=image";
	public static final int NUM_RESULT = 10;
	public static volatile String KEY;

	public static HttpRequest createRequest(String query) {
		if (StringUtils.isEmpty(KEY)) throw new IllegalArgumentException("Cannot create GoogleImageSearch request without KEY");

		return Unirest.get(URL)
				.queryString("q", query)
				.queryString("imgSize", "medium")
				.queryString("imgType", "clipart")
				.queryString("num", NUM_RESULT)
				.queryString("key", KEY);
	}

	public static List<WebItem> parse(HttpResponse<JsonNode> response) {
		log.info("start parse response status: {}", response.getStatus());

		JSONArray items = response.getBody().getObject().getJSONArray("items");
		log.debug("Query result items: {}", items.toString());

		return IntStream.range(0, NUM_RESULT)
				.mapToObj(items::getJSONObject)
				.map(GoogleImageSearch::toWebItem)
				.collect(Collectors.toList());
	}

	private static WebItem toWebItem(JSONObject src) {
		return new WebItem(
				src.getString("link"),
				src.getString("mime"),
				src.getJSONObject("image").getInt("height"),
				src.getJSONObject("image").getInt("width")
		);
	}

}
