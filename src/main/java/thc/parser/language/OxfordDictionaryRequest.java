package thc.parser.language;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.DictionaryResult;
import thc.parser.HttpParseRequest;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

public class OxfordDictionaryRequest implements HttpParseRequest<Optional<DictionaryResult>> {
	private static final Logger log = LoggerFactory.getLogger(OxfordDictionaryRequest.class);
	private static final ObjectReader jsonReader = new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true).readerFor(Map.class);

	public static final String URL = "https://od-api.oxforddictionaries.com/api/v1/entries/en/{0}/regions={1}";
	public static final String KEY_SEPARATOR = ",";
	public static volatile String[] APP_KEY_LIST;
	public static volatile String[] APP_ID_LIST;
	public static final String REGION_GB = "gb";
	public static final String REGION_US = "us";

	private final String query;
	private final String region;

	public OxfordDictionaryRequest(String query) {
		this(query, REGION_GB);
	}

	public OxfordDictionaryRequest(String query, String region) {
		this.query = query;
		this.region = region;
	}

	@Override
	public String url() {
		return MessageFormat.format(URL, query, region);
	}

	@Override
	public Map<String, String> headers() {
		int i = (int) (System.currentTimeMillis() % APP_ID_LIST.length);
		if (StringUtils.isEmpty(APP_KEY_LIST[i]) || StringUtils.isEmpty(APP_ID_LIST[i]))
			throw new IllegalArgumentException("Cannot process Oxford dictionary without App Key and App Id");

		return ImmutableMap.of(
				"app_id",APP_ID_LIST[i],
				"app_key",APP_KEY_LIST[i]);
	}

	@Override
	public Optional<DictionaryResult> parseResponse(InputStream response) {
		if (response == null) return Optional.empty();
		try {
            JsonNode node = jsonReader.readTree(response);
			ArrayNode results = (ArrayNode) node.get("results").get(0).get("lexicalEntries");
			Optional<JsonNode> result = matchQueryToResult(results);
			return result.map(this::toDictionayResult);
		} catch (Exception e) {
			log.error("Fail parse response", e);
			return Optional.empty();
		}
	}

	private Optional<JsonNode> matchQueryToResult(ArrayNode results) {
		Optional<JsonNode> result = exactMatchWithPrononciation(results);
		if (result.isPresent()) return result;

		result = exactMatch(results);
		if (result.isPresent()) return result;

		return alphaMatch(results);
	}

	private Optional<JsonNode> exactMatchWithPrononciation(ArrayNode results) {
		for (int i=0; i < results.size(); i++) {
			if (query.equals(results.get(i).get("text").asText())
					&& results.get(i).has("pronunciations"))
				return Optional.of(results.get(i));
		}
		return Optional.empty();
	}

	private Optional<JsonNode> alphaMatch(ArrayNode results) {
		for (int i=0; i < results.size(); i++) {
			JsonNode result = results.get(i);
			if (matchAlphabet(result.get("text").asText(),query))
				return Optional.of(results.get(i));
		}
		return Optional.empty();
	}

	private boolean matchAlphabet(String text, String query) {
		return text.replaceAll("[^A-Za-z0-9]", "").equalsIgnoreCase(query.replaceAll("[^A-Za-z0-9]", ""));
	}

	private Optional<JsonNode> exactMatch(ArrayNode results) {
		for (int i=0; i < results.size(); i++) {
			if (query.equals(results.get(i).get("text").asText()))
				return Optional.of(results.get(i));
		}
		return Optional.empty();
	}

	private DictionaryResult toDictionayResult(JsonNode src) {
		String ipa = "";
		String audioUrl = "";
		String audioLang = "";

		if (src.has("pronunciations")) {
			ArrayNode pronunciations = (ArrayNode) src.get("pronunciations");
			for (int i=0; i < pronunciations.size(); i++) {
				JsonNode pronunciation = pronunciations.get(i);

				if (pronunciation.has("phoneticNotation") && pronunciation.has("phoneticSpelling")) {
					if (pronunciation.get("phoneticNotation").asText().equalsIgnoreCase("IPA"))
						ipa = pronunciation.get("phoneticSpelling").asText().replaceAll("\\(","").replaceAll("\\)","");
				}

				if (pronunciation.has("audioFile")) {
					audioUrl = pronunciation.get("audioFile").asText();
					if (pronunciation.has("dialects"))
						audioLang = pronunciation.get("dialects").get(0).asText();
				}

				if (StringUtils.isNotEmpty(ipa) && StringUtils.isNotEmpty(audioUrl))
					break;
			}
		}

		String definition =
				Optional.ofNullable(
						src.has("entries") ? src.get("entries").get(0) : null
				)
				.flatMap(o -> Optional.ofNullable(o.has("senses") ? o.get("senses") :  null))
				.flatMap(x -> Optional.ofNullable(
						x.get(0).has("definitions")
							? x.get(0).get("definitions") : null)
				)
				.flatMap(x -> Optional.ofNullable(x.get(0).asText()))
				.orElse("");

		return new DictionaryResult(
				src.get("text").asText(),
				audioUrl,
				audioLang,
				ipa,
				definition
		);
	}

}
