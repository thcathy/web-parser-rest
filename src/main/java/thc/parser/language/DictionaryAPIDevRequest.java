package thc.parser.language;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import thc.domain.DictionaryResult;
import thc.parser.RestParseRequest;

import java.text.MessageFormat;
import java.util.Collections;

public class DictionaryAPIDevRequest implements RestParseRequest<DictionaryResult> {
	private static final Logger log = LoggerFactory.getLogger(DictionaryAPIDevRequest.class);
	public static final String URL = "https://api.dictionaryapi.dev/api/v2/entries/en/{0}";
	private final String query;

	public DictionaryAPIDevRequest(String query) {
		this.query = query;
	}

	@Override
	public String url() {
		return MessageFormat.format(URL, query);
	}

	@Override
	public Mono<DictionaryResult> parseResponse(JsonNode node) {
		try {
			if (node == null) {
				log.warn("Empty response for query: {}", query);
				return Mono.empty();
			}

			JsonNode matchedNode = null;
			for (int i=0; i<node.size(); i++) {
				if (node.get(i).has("word")) {
					var word = node.get(i).get("word").asText();
					if (thc.util.StringUtils.isAlphabeticallyEqual(query, word)) {
						matchedNode = node.get(i);
						break;
					}
				}
			}
			if (matchedNode == null) {
				log.warn("Cannot find headword equal to query: {}", query);
				return Mono.empty();
			}

			return Mono.just(buildResult(getAudioText(matchedNode), getIPAText(matchedNode)));
		} catch (Exception e) {
			log.error("Fail parse response: {}", e, e);
			return Mono.empty();
		}
	}

	String getIPAText(JsonNode node) {
        if (!node.has("phonetic")) return "";

		var phonetic = node.get("phonetic").asText();
		phonetic = phonetic.charAt(0) == '/' ? phonetic.substring(1) : phonetic;
		phonetic = phonetic.charAt(phonetic.length() - 1) == '/' ? phonetic.substring(0, phonetic.length() - 1) : phonetic;
        return phonetic;
    }

	String getAudioText(JsonNode node) {
		if (!node.has("phonetics")) return "";

		var phonetics = node.get("phonetics");
		for (var i=0; i<phonetics.size(); i++) {
			if (phonetics.get(i).has("audio"))
				return phonetics.get(i).get("audio").textValue();
		}

		return "";
	}

	DictionaryResult buildResult(String audio, String IPA) {
		return new DictionaryResult(query, audio,"English",	IPA,"", Collections.emptyList());
	}


}
