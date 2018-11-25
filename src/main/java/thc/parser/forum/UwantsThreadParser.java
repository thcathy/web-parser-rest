package thc.parser.forum;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.MessageFormat;
import java.util.Optional;

public class UwantsThreadParser extends ForumThreadParser {
	static public final String LOGIN_URL = "https://www.discuss.com.hk/logging.php?action=login&loginsubmit=yes&username={0}&password={1}";
	static public volatile String USERNAME;
	static public volatile String PASSWORD;

	public UwantsThreadParser(String url, String source, String encoding) {
		super(url, source, encoding, Optional.of(MessageFormat.format(LOGIN_URL, USERNAME, PASSWORD)));
		validateAccount();
	}

	private void validateAccount() {
		if (StringUtils.isBlank(USERNAME) || StringUtils.isBlank(PASSWORD))
			throw new IllegalArgumentException("Cannot create UwantsThreadRetriever without username or password");
	}

	@Override
	protected String parseURL(Element e) {
		return e.select("span[id^=thread] a[href^=viewthread.php]").attr("href");
	}

	@Override
	protected String parseTitle(Element e) {
		return e.select("span[id^=thread] a[href^=viewthread.php]").text();
	}

	@Override
	protected String parseDateStr(Element e) {
		return e.select("td.author em").text();
	}

	@Override
	protected Elements parseThreads(Document doc) {
		return doc.select("tbody[id^=normal]");
	}	
}
