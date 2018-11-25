package thc.parser.language;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.DictionaryResult;

import java.util.Optional;

public class CambridgeDictionaryParser {
	protected static final Logger log = LoggerFactory.getLogger(CambridgeDictionaryParser.class);

	public static final String URL = "https://dictionary.cambridge.org/dictionary/english/";

	private String query;
	private String audioLink;
	private String ipa;

	public CambridgeDictionaryParser(String query) { this.query = query; }

	public Optional<DictionaryResult> parse() {
		if (!tryParseFromUrl(concatURL()))
			if (!tryParseFromUrl(concatURL2()))
				if (!tryParseFromUrl(concatURL3())) {}

		if (!isContentFind())
			return Optional.empty();

		return Optional.of(new DictionaryResult(
				query,
				audioLink,
				"British English",
				ipa,
				"N.A.")
		);
	}

	public boolean isContentFind() {
		return org.springframework.util.StringUtils.hasText(ipa) && org.springframework.util.StringUtils.hasText(audioLink);
	}

	private boolean tryParseFromUrl(String url) {
		Document doc;

		try {
			doc = Jsoup.connect(url).get();

			Elements audioLinkSource = doc.select("span.audio_play_button");
			audioLink = "https://dictionary.cambridge.org" + audioLinkSource.get(0).attr("data-src-mp3");

			ipa = doc.select("span.ipa").get(0).ownText();
		} catch (Exception e1) {
			log.warn("cannot get reader when process [{}] from url [{}], reason [{}]", new Object[]{query, url, e1.toString()});
		}
		return isContentFind();
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
