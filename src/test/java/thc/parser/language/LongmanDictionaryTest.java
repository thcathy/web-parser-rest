package thc.parser.language;

import com.google.common.base.Stopwatch;
import com.mashape.unirest.request.HttpRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.DictionaryResult;
import thc.service.HttpService;
import thc.unirest.UnirestSetup;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

// longman dictionary api is going deleted
@Ignore
public class LongmanDictionaryTest {
	private Logger log = LoggerFactory.getLogger(LongmanDictionaryTest.class);

    HttpService httpService = new HttpService();

	static {
		UnirestSetup.setupAll();
		LongmanDictionaryParser.CONSUMER_KEY = System.getProperty("pearsonapi.key");
	}

	@Test
	public void query_apple_shouldReturnCorrectResult() {
		Stopwatch timer = Stopwatch.createStarted();

		LongmanDictionaryParser parser = new LongmanDictionaryParser("apple");
		DictionaryResult result = httpService.queryAsync(parser.createRequest()::asJsonAsync, parser::parse).join().get();

		assertEquals("apple", result.word);
		assertEquals("ˈæpəl", result.IPA);
		assertEquals("http://api.pearson.com/v2/dictionaries/assets/ldoce/gb_pron/brelasdeapple.mp3", result.pronunciationUrl);
		assertEquals("a hard round fruit that has red, light green, or yellow skin and is white inside", result.definition);

        log.info("query_apple_shouldReturnCorrectResult took: {}", timer.stop());
	}

	@Test
	public void query_busstop_shouldReturnCorrectResult() {
		Stopwatch timer = Stopwatch.createStarted();

		LongmanDictionaryParser parser = new LongmanDictionaryParser("bus-stop");
		DictionaryResult result = httpService.queryAsync(parser.createRequest()::asJsonAsync, parser::parse).join().get();

		assertEquals("bus stop", result.word);
		assertEquals("", result.IPA);
		assertEquals("", result.pronunciationUrl);
		assertEquals("a place at the side of a road, marked with a sign, where buses stop for passengers", result.definition);

		log.info("query_busstop_shouldReturnCorrectResult took: {}", timer.stop());
	}

	@Test
	public void query_account_shouldReturnCorrectResult() {
		LongmanDictionaryParser parser = new LongmanDictionaryParser("account");
		DictionaryResult result = httpService.queryAsync(parser.createRequest()::asJsonAsync, parser::parse).join().get();

		assertEquals("account", result.word);
		assertEquals("əˈkaʊnt", result.IPA);
		assertEquals("http://api.pearson.com/v2/dictionaries/assets/ldoce/gb_pron/account_v0205.mp3", result.pronunciationUrl);
		assertEquals("a written or spoken description that says what happens in an event or process", result.definition);
	}

	@Test
	public void query_newyork_shouldReturnCorrectResult() {
		Stopwatch timer = Stopwatch.createStarted();

		LongmanDictionaryParser parser = new LongmanDictionaryParser("new york");
		DictionaryResult result = httpService.queryAsync(parser.createRequest()::asJsonAsync, parser::parse).join().get();

		assertEquals("New York", result.word);
		assertEquals("", result.IPA);
		assertEquals("", result.pronunciationUrl);
		assertEquals("", result.definition);

		log.info("query_newyork_shouldReturnCorrectResult took: {}", timer.stop());
	}

	@Test
	public void query_refer_shouldReturnCorrectResult() {
		Stopwatch timer = Stopwatch.createStarted();

		LongmanDictionaryParser parser = new LongmanDictionaryParser("refer");
		DictionaryResult result = httpService.queryAsync(parser.createRequest()::asJsonAsync, parser::parse).join().get();

		assertEquals("refer", result.word);
		assertEquals("rɪˈfɜː", result.IPA);
		assertEquals("http://api.pearson.com/v2/dictionaries/assets/ldoce/gb_pron/refer0205.mp3", result.pronunciationUrl);
		assertEquals("", result.definition);

		log.info("query_newyork_shouldReturnCorrectResult took: {}", timer.stop());
	}

	@Test
	public void query_nonword_shouldReturnEmptyResult() {
		Stopwatch timer = Stopwatch.createStarted();

		LongmanDictionaryParser parser = new LongmanDictionaryParser("----------");
		Optional<DictionaryResult> result = httpService.queryAsync(parser.createRequest()::asJsonAsync, parser::parse).join();

		assertFalse(result.isPresent());

		log.info("query_nonword_shouldReturnEmptyResult took: {}", timer.stop());
	}

	@Test(expected = IllegalArgumentException.class)
	public void query_givenNoAPIKey_shouldThrowException() {
		String key = LongmanDictionaryParser.CONSUMER_KEY;

		try {
			LongmanDictionaryParser.CONSUMER_KEY = null;
			HttpRequest request = new LongmanDictionaryParser("apple").createRequest();
		} finally {
			LongmanDictionaryParser.CONSUMER_KEY = key;
		}
	}
}