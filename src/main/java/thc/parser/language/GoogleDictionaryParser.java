package thc.parser.language;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import thc.domain.DictionaryResult;

import java.util.stream.Collectors;

public class GoogleDictionaryParser {
	protected static final Logger log = LoggerFactory.getLogger(GoogleDictionaryParser.class);
	private final String query;

	public GoogleDictionaryParser(String query) { this.query = query; }

	public Mono<DictionaryResult> parse() {
		var url = String.format("https://www.google.com/search?hl=en&q=define+%s&gl=US", replaceNonAlpha(query, '+'));

		try {
			Document doc = Jsoup.connect(url).timeout(60000).get();

			var meanings = doc.select("div[data-dobid='dfn']")
					.stream()
					.map(this::parseMeaning)
					.toList();

			log.info("Parse Google meaning for {}: {}", query, meanings);
			return Mono.just(new DictionaryResult(query, meanings));
		} catch (Exception e) {
			log.warn("cannot process [{}], reason [{}]", query, e.toString());
		}
		return Mono.empty();
	}

	private String parseMeaning(Element element) {
		return element.select("span").stream()
				.filter(s -> s.selectFirst("sup") == null)
				.map(Element::text).collect(Collectors.joining(" "));
	}

	public static String replaceNonAlpha(String input, char replacement) {
		return input.chars()
				.mapToObj(c -> Character.isAlphabetic(c) ? (char) c : replacement)
				.map(String::valueOf)
				.collect(Collectors.joining());
	}
}
