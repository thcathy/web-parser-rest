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

import java.text.MessageFormat;
import java.util.Date;
import java.util.Optional;

public class OxfordDictionaryParser {
	protected static final Logger log = LoggerFactory.getLogger(OxfordDictionaryParser.class);

	public static final String URL = "https://od-api.oxforddictionaries.com/api/v1/entries/en/{0}/regions={1}";
	public static final String KEY_SEPARATOR = ",";
	public static volatile String[] APP_KEY_LIST;
	public static volatile String[] APP_ID_LIST;
	public static final String REGION_GB = "gb";
	public static final String REGION_US = "us";


	private final String query;
	private final String region;

	public OxfordDictionaryParser(String query) {
		this(query, REGION_GB);
	}

	public OxfordDictionaryParser(String query, String region) {
		this.query = query;
		this.region = region;
	}

	public HttpRequest createRequest() {
		int i = (int) (System.currentTimeMillis() % APP_ID_LIST.length);
		if (StringUtils.isEmpty(APP_KEY_LIST[i]) || StringUtils.isEmpty(APP_ID_LIST[i]))
			throw new IllegalArgumentException("Cannot query Oxford dictionary without App Key and App Id");

		return Unirest.get(MessageFormat.format(URL, query, region))
				.header("app_id", APP_ID_LIST[i])
				.header("app_key", APP_KEY_LIST[i]);
	}

	public Optional<DictionaryResult> parse(HttpResponse<JsonNode> response) {
		try {
			log.info("start parse response status: {}", response.getStatus());
			if (response.getStatus() > HttpStatus.SC_OK)
				throw new RuntimeException("Oxford dictionary api return fail: {}" + response.getBody());

			JSONArray results = response.getBody().getObject().getJSONArray("results").getJSONObject(0).getJSONArray("lexicalEntries");
			Optional<JSONObject> result = matchQueryToResult(results);
			return result.map(this::toDictionayResult);
		} catch (Exception e) {
			log.error("Fail parse response", e);
			return Optional.empty();
		}
	}

	private Optional<JSONObject> matchQueryToResult(JSONArray results) {
		Optional<JSONObject> result = exactMatchWithPrononciation(results);
		if (result.isPresent()) return result;

		result = exactMatch(results);
		if (result.isPresent()) return result;

		return alphaMatch(results);
	}

	private Optional<JSONObject> exactMatchWithPrononciation(JSONArray results) {
		for (int i=0; i < results.length(); i++) {
			if (query.equals(results.getJSONObject(i).getString("text"))
					&& results.getJSONObject(i).has("pronunciations"))
				return Optional.of(results.getJSONObject(i));
		}
		return Optional.empty();
	}

	private Optional<JSONObject> alphaMatch(JSONArray results) {
		for (int i=0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			if (matchAlphabet(result.getString("text"),query))
				return Optional.of(results.getJSONObject(i));
		}
		return Optional.empty();
	}

	private boolean matchAlphabet(String text, String query) {
		return text.replaceAll("[^A-Za-z0-9]", "").equalsIgnoreCase(query.replaceAll("[^A-Za-z0-9]", ""));
	}

	private Optional<JSONObject> exactMatch(JSONArray results) {
		for (int i=0; i < results.length(); i++) {
			if (query.equals(results.getJSONObject(i).getString("text")))
				return Optional.of(results.getJSONObject(i));
		}
		return Optional.empty();
	}

	private DictionaryResult toDictionayResult(JSONObject src) {
		String ipa = "";
		String audioUrl = "";
		String audioLang = "";

		if (src.has("pronunciations")) {
			JSONArray pronunciations = src.getJSONArray("pronunciations");
			for (int i=0; i < pronunciations.length(); i++) {
				JSONObject pronunciation = pronunciations.getJSONObject(i);

				if (pronunciation.has("phoneticNotation") && pronunciation.has("phoneticSpelling")) {
					if (pronunciation.getString("phoneticNotation").equalsIgnoreCase("IPA"))
						ipa = pronunciation.getString("phoneticSpelling").replaceAll("\\(","").replaceAll("\\)","");
				}

				if (pronunciation.has("audioFile")) {
					audioUrl = pronunciation.getString("audioFile");
					if (pronunciation.has("dialects"))
						audioLang = pronunciation.getJSONArray("dialects").getString(0);
				}

				if (StringUtils.isNotEmpty(ipa) && StringUtils.isNotEmpty(audioUrl))
					break;
			}
		}

		String definition =
				Optional.ofNullable(
						src.isNull("entries") ? null : src.getJSONArray("entries").getJSONObject(0)
				)
				.flatMap(o -> Optional.ofNullable(o.isNull("senses") ? null : o.getJSONArray("senses")))
				.flatMap(x -> Optional.ofNullable(
						x.getJSONObject(0).has("definitions")
							? x.getJSONObject(0).getJSONArray("definitions") : null)
				)
				.flatMap(x -> Optional.ofNullable(x.getString(0)))
				.orElse("");

		return new DictionaryResult(
				src.getString("text"),
				audioUrl,
				audioLang,
				ipa,
				definition
		);
	}

}
