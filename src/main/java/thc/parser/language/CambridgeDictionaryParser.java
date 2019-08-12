package thc.parser.language;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import thc.domain.DictionaryResult;

import static org.springframework.util.StringUtils.hasText;

public class CambridgeDictionaryParser {
	protected static final Logger log = LoggerFactory.getLogger(CambridgeDictionaryParser.class);

	public static final String URL = "https://dictionary.cambridge.org/dictionary/english/";

	private String query;
	private String audioLink;
	private String ipa;

	public CambridgeDictionaryParser(String query) { this.query = query; }

	public Mono<DictionaryResult> parse() {
		return Mono.first(
				parseMono(concatURL()),
				parseMono(concatURL2()),
				parseMono(concatURL3())
		);
	}

	private Mono<DictionaryResult> parseMono(String url) {
		return Mono.defer(() -> parserFromUrl(url));
	}

	private Mono<DictionaryResult> parserFromUrl(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			Elements audioLinkSource = doc.select("span.audio_play_button");
			audioLink = "https://dictionary.cambridge.org" + audioLinkSource.get(0).attr("data-src-mp3");
			ipa = doc.select("span.ipa").get(0).ownText();
			if (hasText(ipa) && hasText(audioLink)) {
				return Mono.just(new DictionaryResult(
						query, audioLink,"British English", ipa,"N.A."));
			}
		} catch (Exception e) {
			log.warn("cannot get reader when process [{}], reason [{}]", new Object[]{query, e.toString()});
		}
		return Mono.empty();
	}

	private String concatURL() {
		return URL + query;
	}

	private String concatURL2() {
		return URL + query + "_1?q=" + query;
	}

	private String concatURL3() {
		return URL + query + "_2?q=" + query;
	}
}
