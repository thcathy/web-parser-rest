package thc.parser.language;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import thc.domain.DictionaryResult;
import thc.util.StringUtils;

import java.util.Collections;

import static org.springframework.util.StringUtils.hasText;

public class CambridgeDictionaryParser {
	protected static final Logger log = LoggerFactory.getLogger(CambridgeDictionaryParser.class);

	public static final String URL = "https://dictionary.cambridge.org/dictionary/english/";

	private String query;
	private String audioLink;
	private String ipa;

	public CambridgeDictionaryParser(String query) { this.query = query; }

	public Mono<DictionaryResult> parse() {
		return parseMono(concatURL())
				.switchIfEmpty(parseMono(concatURL2()))
				.switchIfEmpty(parseMono(concatURL3()));
	}

	private Mono<DictionaryResult> parseMono(String url) {
		return Mono.defer(() -> parserFromUrl(url));
	}

	private Mono<DictionaryResult> parserFromUrl(String url) {
		log.info("query url: {}", url);
		try {
            var doc = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
					.timeout(60000)
					.get();
			var headwordElement = doc.selectFirst("span.hw");
			if (headwordElement == null || !StringUtils.isAlphabeticallyEqual(query, headwordElement.text())) {
				log.info("headword [{}], different from query [{}]", headwordElement != null ? headwordElement.text() : null, query);
				return Mono.empty();
			}

			var posHeaderElement = headwordElement.parent().parent().parent();
            var audioLinkSource = posHeaderElement.select("audio source[type*=mpeg]");
			audioLink = "https://dictionary.cambridge.org" + audioLinkSource.getFirst().attr("src");
			ipa = posHeaderElement.select("span.pron.dpron span").getFirst().text();
			if (hasText(ipa) && hasText(audioLink)) {
				return Mono.just(new DictionaryResult(
						query, audioLink,"British English", ipa,"N.A.", Collections.emptyList()));
			}
		} catch (Exception e) {
			log.warn("cannot process [{}], reason [{}]", query, e.toString());
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
