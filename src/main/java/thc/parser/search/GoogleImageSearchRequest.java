package thc.parser.search;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.WebItem;
import thc.parser.HttpParseRequest;

import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GoogleImageSearchRequest implements HttpParseRequest<List<WebItem>> {
	protected static final Logger log = LoggerFactory.getLogger(GoogleImageSearchRequest.class);

	private static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);
	public static final String KEY_SEPARATOR = ",";
	public static final String URL = "https://www.googleapis.com/customsearch/v1?cx=011552421082581973471%3A0ge1n0sksf4&filter=1&safe=medium&searchType=image";
	public static final int NUM_RESULT = 10;
	public static volatile Iterator<String> keys;

	private final String query;

	public GoogleImageSearchRequest(String query) {
		if (keys == null) throw new IllegalArgumentException("Cannot create google image search request without key");
		this.query = query;
	}

	@Override
	public String url() { return URL; }

	@Override
	public Map<String, String> queryParams() {
		return ImmutableMap.of(
				"q", query,
				"imgSize","medium",
				"imgType","clipart",
				"num", String.valueOf(NUM_RESULT),
				"key",keys.next()
		);
	}

	public static void setAPIKeys(String keys) {
		GoogleImageSearchRequest.keys = Iterables.cycle(keys.split(KEY_SEPARATOR)).iterator();
	}

	@Override
	public List<WebItem> parseResponse(InputStream response) {
		try {
			JsonNode node = jsonReader.readTree(response);

			if (!node.has("items")) {
				log.warn("No item found from body", node.asText());
				return Collections.EMPTY_LIST;
			}

			JsonNode items = node.get("items");
			log.debug("Query result items: {}", items.toString());

			return IntStream.range(0, NUM_RESULT)
					.mapToObj(i -> items.get(i))
					.map(GoogleImageSearchRequest::toWebItem)
					.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("fail to parse response", e);
			return Collections.EMPTY_LIST;
		}
	}

	private static WebItem toWebItem(JsonNode src) {
		return new WebItem(
				src.get("link").asText(),
				src.get("mime").asText(),
				src.get("image").get("height").asInt(),
				src.get("image").get("width").asInt(),
				src.get("image").get("thumbnailLink").asText()
		);
	}

}