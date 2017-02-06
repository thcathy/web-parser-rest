package thc.parser.language;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.DictionaryResult;

import java.util.Optional;

public class LongmanDictionaryParser {
	protected static final Logger log = LoggerFactory.getLogger(LongmanDictionaryParser.class);
	
	public static final String URL = "https://api.pearson.com/v2/dictionaries/ldoce5/entries?&audio=pronunciation";
	public static volatile String CONSUMER_KEY;

	private final String query;

	public LongmanDictionaryParser(String query) {
		this.query = query;
	}

	public HttpRequest createRequest() {
		if (StringUtils.isEmpty(CONSUMER_KEY)) throw new IllegalArgumentException("Cannot query Longman dictionary without Pearson api key");

		return Unirest.get(URL)
				.queryString("headword", query)
				.queryString("apikey", CONSUMER_KEY);
	}

	public Optional<DictionaryResult> parse(HttpResponse<JsonNode> response) {
		log.info("start parse response status: {}", response.getStatus());
		if (response.getStatus() > HttpStatus.SC_OK) throw new RuntimeException("Longman dictionary api return fail: {}" + response.getBody());

		JSONArray results = response.getBody().getObject().getJSONArray("results");
		Optional<JSONObject> result = matchQueryToResult(results);
		return result.map(this::toDictionayResult);
	}

	private Optional<JSONObject> matchQueryToResult(JSONArray results) {
		Optional<JSONObject> result = exactMatch(results);
		if (result.isPresent()) return result;

		result = alphaMatchWithPartOfSpeech(results);
		if (result.isPresent()) return result;

		result = alphaMatchWithOutPartOfSpeech(results);
		return result;

	}

	private Optional<JSONObject> alphaMatchWithPartOfSpeech(JSONArray results) {
		for (int i=0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			if (result.has("part_of_speech") && matchAlphabet(result.getString("headword"),query))
				return Optional.of(results.getJSONObject(i));
		}
		return Optional.empty();
	}

	private Optional<JSONObject> alphaMatchWithOutPartOfSpeech(JSONArray results) {
		for (int i=0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			if (matchAlphabet(result.getString("headword"),query))
				return Optional.of(results.getJSONObject(i));
		}
		return Optional.empty();
	}

	private boolean matchAlphabet(String headword, String query) {
		return headword.replaceAll("[^A-Za-z0-9]", "").equalsIgnoreCase(query.replaceAll("[^A-Za-z0-9]", ""));
	}

	private Optional<JSONObject> exactMatch(JSONArray results) {
		for (int i=0; i < results.length(); i++) {
			if (query.equals(results.getJSONObject(i).getString("headword")))
				return Optional.of(results.getJSONObject(i));
		}
		return Optional.empty();
	}

	private DictionaryResult toDictionayResult(JSONObject src) {
		String ipa = "";
		String audioUrl = "";
		String audioLang = "";

		if (src.has("pronunciations")) {
			JSONObject pronunciation = src.getJSONArray("pronunciations").getJSONObject(0);
			ipa = pronunciation.getString("ipa");

			if (pronunciation.has("audio")) {
				JSONObject audio = pronunciation.getJSONArray("audio").getJSONObject(0);
				audioUrl = "http://api.pearson.com" + audio.getString("url");
				audioLang = audio.getString("lang");
			}
		}

		return new DictionaryResult(
				src.getString("headword"),
				audioUrl,
				audioLang,
				ipa,
				src.getJSONArray("senses").getJSONObject(0).has("definition") ? src.getJSONArray("senses").getJSONObject(0).getJSONArray("definition").getString(0) : ""
		);
	}

}
