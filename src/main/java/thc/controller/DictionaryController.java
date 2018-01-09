package thc.controller;

import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thc.domain.DictionaryResult;
import thc.parser.language.CambridgeDictionaryParser;
import thc.parser.language.LongmanDictionaryParser;
import thc.parser.language.OxfordDictionaryParser;
import thc.service.HttpService;

import javax.cache.CacheManager;
import javax.cache.annotation.CacheResult;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class DictionaryController {
	private static Logger log = LoggerFactory.getLogger(DictionaryController.class);
	public static final int QUERY_TIMEOUT_SECOND = 3;

	@Autowired HttpService httpService;

	@CacheResult(cacheName = "dictionary")
    @RequestMapping(value = "/rest/dictionary/{query}", method = GET)
    public ResponseEntity<DictionaryResult> query(@PathVariable String query) {
    	log.debug("query: {}", query);

		Optional<DictionaryResult> result = queryOxfordDictionary(query, OxfordDictionaryParser.REGION_GB);
		if (result.isPresent() && StringUtils.isNotEmpty(result.get().pronunciationUrl))
			return ResponseEntity.ok(result.get());

		//retry with cambridge dictionary
		result = new CambridgeDictionaryParser(query).parse();
		if (result.isPresent() && StringUtils.isNotEmpty(result.get().pronunciationUrl))
			return ResponseEntity.ok(result.get());

		// retry with region US for wording in US
		result = queryOxfordDictionary(query, OxfordDictionaryParser.REGION_US);
		if (result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}

	private Optional<DictionaryResult> queryOxfordDictionary(String query, String region) {
		OxfordDictionaryParser parser = new OxfordDictionaryParser(query, region);
		HttpRequest request = parser.createRequest();
		try {
			return httpService.queryAsync(request::asJsonAsync, parser::parse).get(QUERY_TIMEOUT_SECOND, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.warn("Exception when query oxford dictionary for {} in region", query, region, e);
			return Optional.empty();
		}
	}

	@Component
	public static class CachingSetup implements JCacheManagerCustomizer
	{
		@Override
		public void customize(CacheManager cacheManager)
		{
			cacheManager.createCache("dictionary", new MutableConfiguration<>()
					.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.DAYS, 1)))
					.setStoreByValue(false)
					.setStatisticsEnabled(true));
		}
	}
}


