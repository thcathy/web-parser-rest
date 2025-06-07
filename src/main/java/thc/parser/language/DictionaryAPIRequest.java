package thc.parser.language;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import thc.domain.DictionaryResult;
import thc.parser.RestParseRequest;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;

public class DictionaryAPIRequest implements RestParseRequest<DictionaryResult> {
	private static final Logger log = LoggerFactory.getLogger(DictionaryAPIRequest.class);

	public static final String URL = "https://www.dictionaryapi.com/api/v3/references/collegiate/json/{0}?key={1}";
	public static final String AUDIO_URL = "https://media.merriam-webster.com/audio/prons/en/us/mp3/{0}/{1}.mp3";
	public static volatile String API_KEY;

	private final String query;

	public DictionaryAPIRequest(String query) {
		this.query = query;
	}

	@Override
	public String url() {
		return MessageFormat.format(URL, query, API_KEY);
	}

	protected String getSubDirectory(String audio) {
		if (audio.startsWith("bix")) {
			return "bix";
		} else if (audio.startsWith("gg")) {
			return  "gg";
		}

		String firstCharacter = audio.substring(0, 1);
		if (firstCharacter.matches("[0-9]") || firstCharacter.matches("\\p{Punct}")) {
			return "number";
		}
		return firstCharacter;
	}

	@Override
	public Mono<DictionaryResult> parseResponse(JsonNode node) {
		if (node == null) {
			log.warn("Empty response for query: {}", query);
			return Mono.empty();
		}

		JsonNode matchedNode = null;
		for (int i=0; i<node.size(); i++) {
			if (node.get(i).has("meta") && node.get(i).get("meta").has("id")) {
				var metaId = node.get(i).get("meta").get("id").asText().split(":")[0];
				if (query.equals(metaId)) {
					matchedNode = node.get(i);
					break;
				}
			}
		}
		if (matchedNode == null) {
			log.warn("Cannot find headword equal to query: {}", query);
			return Mono.empty();
		}

		try {
			String audio, IPA;
			for (Iterator<JsonNode> it = matchedNode.get("hwi").get("prs").elements(); it.hasNext(); ) {
				var element = it.next();
				audio = getAudioText(element);
				IPA = getIPAText(element);
				if (StringUtils.isNotEmpty(audio) && StringUtils.isNotEmpty(IPA)) {
					return Mono.just(buildResult(audio, IPA));
				}
			}

			var prsNode = matchedNode.get("hwi").get("prs").get(0);
			audio = getAudioText(prsNode);
			IPA = getIPAText(prsNode);
			return Mono.just(buildResult(audio, IPA));
		} catch (Exception e) {
			log.error("Fail parse response: {}", e, e);
			return Mono.empty();
		}
	}

	String getIPAText(JsonNode node) {
		if (node.has("mw"))
			return node.get("mw").textValue();
		return "";
	}

	String getAudioText(JsonNode node) {
		if (node.has("sound")) {
			if (node.get("sound").has("audio"))
				return node.get("sound").get("audio").textValue();
		}
		return "";
	}

	DictionaryResult buildResult(String audio, String IPA) {
		return new DictionaryResult(
				query,
				MessageFormat.format(AUDIO_URL, getSubDirectory(audio), audio),
				"English",
				IPA,
				"",
				Collections.emptyList()
		);
	}


}
