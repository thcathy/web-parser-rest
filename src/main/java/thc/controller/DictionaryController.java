package thc.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import thc.domain.DictionaryResult;
import thc.parser.language.CambridgeDictionaryParser;
import thc.parser.language.OxfordDictionaryRequest;
import thc.service.RestParseService;

import java.util.concurrent.TimeUnit;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class DictionaryController {
	private static Logger log = LoggerFactory.getLogger(DictionaryController.class);

	@Autowired RestParseService parseService;

	final protected Cache<String, Object> cache;

	public DictionaryController() {
		cache = Caffeine
				.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(1, TimeUnit.DAYS)
				.build();
	}

	@RequestMapping(value = "/rest/dictionary/{query}", method = GET)
	public Mono<DictionaryResult> query(@PathVariable String query) {
		log.debug("process: {}", query);

		return CacheMono
				.lookup(cache.asMap(), query, DictionaryResult.class)
				.onCacheMissResume(() ->
						new CambridgeDictionaryParser(query).parse()
								.filter(this::hasPronunciationUrl)
								.switchIfEmpty(queryOxfordDictionary(query, OxfordDictionaryRequest.REGION_GB))
								.filter(this::hasPronunciationUrl)
								.switchIfEmpty(queryOxfordDictionary(query, OxfordDictionaryRequest.REGION_US)));
	}

	private boolean hasPronunciationUrl(DictionaryResult result) {
		return StringUtils.isNotEmpty(result.pronunciationUrl);
	}

	private Mono<DictionaryResult> queryOxfordDictionary(String query, String region) {
		OxfordDictionaryRequest request = new OxfordDictionaryRequest(query, region);
		try {
			return parseService.process(request);
		} catch (Exception e) {
			log.warn("Exception when process oxford dictionary for {} in region", query, region, e);
			return Mono.empty();
		}
	}
}

