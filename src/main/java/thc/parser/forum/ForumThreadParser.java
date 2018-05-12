package thc.parser.forum;

import com.mashape.unirest.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.ForumThread;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class ForumThreadParser {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected final SimpleDateFormat simpleDFormat = new SimpleDateFormat("yyyy-M-d");

	public final String url;
	public final String source;
	public final String encoding;
	public final Optional<String> loginUrl;	
			
	// Constructor
	protected ForumThreadParser(String url, String source, String encoding, Optional<String> loginUrl) {
		this.url = url;
		this.source = source;
		this.loginUrl = loginUrl;
		this.encoding = encoding;
	}
	
	/**
	 * Factory method to build a thc.parser instance
	 */
	public static ForumThreadParser buildParser(String url, int page) {
		if (url.contains("www.uwants.com")) 
			return new UwantsThreadParser(String.format(url,page), "Uwants", "UTF-8");
		else if (url.contains("www.discuss.com"))
			return new UwantsThreadParser(String.format(url,page), "Discuss", "UTF-8");
		else if (url.contains("www.tvboxnow.com"))
			return new TvboxnowThreadParser(String.format(url,page), "Tvboxnow");			
		else
			throw new IllegalArgumentException("No thc.parser for url: " + url);
	}
    
	public List<ForumThread> parse(HttpResponse response) {
		List<ForumThread> results = new ArrayList<ForumThread>();
		
		try {
			for (Iterator<Element> iter = parseThreads(Jsoup.parse(response.getRawBody(), encoding, url)).iterator(); iter.hasNext(); ) {				
				Element e = iter.next();
				results.add(new ForumThread(new URL(url).getHost() + "/" + parseURL(e), parseTitle(e), source, convertDate(parseDateStr(e))));
			}
		} catch (Exception e1) {
			log.warn("Fail to parse url:" + url, e1);
		}		
		
		return results;
	}
		
	protected Date convertDate(String str) {		
		try {
			return simpleDFormat.parse(str);
		} catch (ParseException e) {
			log.warn("Cannot parse date: ", str);
			return new Date();
		}
	}
	
	abstract protected String parseURL(Element e);
	abstract protected String parseTitle(Element e);
	abstract protected String parseDateStr(Element e);
	abstract protected Elements parseThreads(Document doc);	
}
