package thc.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import thc.domain.DictionaryResult;
import thc.parser.language.CambridgeDictionaryParser;
import thc.parser.language.DictionaryAPIRequest;
import thc.parser.language.GoogleDictionaryParser;
import thc.service.RestParseService;

import java.util.concurrent.TimeUnit;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@CrossOrigin(origins = "*")
@RestController
public class DictionaryController {
	private static Logger log = LoggerFactory.getLogger(DictionaryController.class);

	@Autowired RestParseService parseService;

	final protected Cache<String, DictionaryResult> dictionaryResultCache;
	final protected Cache<String, DictionaryResult> googleResultCache;

	public DictionaryController() {
		dictionaryResultCache = Caffeine.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(30, TimeUnit.DAYS)
				.build();

		googleResultCache = Caffeine.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(30, TimeUnit.DAYS)
				.build();
	}

	@RequestMapping(value = "/rest/dictionary/{query}", method = GET)
	public Mono<DictionaryResult> query(@PathVariable String query) {
		log.debug("process: {}", query);

		return Mono.defer(() -> {
			var cachedValue = dictionaryResultCache.getIfPresent(query);
			if (cachedValue != null) {
				return Mono.just(cachedValue);
			} else {
				return queryOnlineSources(query)
						.doOnNext(value -> dictionaryResultCache.put(query, value));
			}
		});
	}

	@RequestMapping(value = "/rest/dictionary/google/{query}", method = GET)
	public Mono<DictionaryResult> queryGoogle(@PathVariable String query) {
		log.debug("process: {}", query);

		return Mono.defer(() -> {
			var cachedValue = googleResultCache.getIfPresent(query);
			if (cachedValue != null) {
				return Mono.just(cachedValue);
			} else {
				return new GoogleDictionaryParser(query).parse()
						.doOnNext(value -> googleResultCache.put(query, value));
			}
		});
	}

	private Mono<DictionaryResult> queryOnlineSources(String query) {
		return new CambridgeDictionaryParser(query).parse()
				.filter(this::hasPronunciationUrl)
				.switchIfEmpty(queryDictionaryAPI(query))
				.filter(this::hasPronunciationUrl)
				.defaultIfEmpty(new DictionaryResult(query));
	}

	private boolean hasPronunciationUrl(DictionaryResult result) {
		return StringUtils.isNotEmpty(result.pronunciationUrl);
	}

	private Mono<DictionaryResult> queryDictionaryAPI(String query) {
		try {
			return parseService.process(new DictionaryAPIRequest(query));
		} catch (Exception e) {
			log.warn("Exception when process dictionary API for {}", query, e);
			return Mono.empty();
		}
	}
}

