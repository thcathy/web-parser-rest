package thc.controller;

import com.mashape.unirest.request.HttpRequest;
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
import thc.parser.language.LongmanDictionaryParser;
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

	@Autowired HttpService httpService;

	@CacheResult(cacheName = "dictionary")
    @RequestMapping(value = "/rest/dictionary/{query}", method = GET)
    public ResponseEntity<DictionaryResult> query(@PathVariable String query) {
    	log.debug("query: {}", query);

		LongmanDictionaryParser parser = new LongmanDictionaryParser(query);
		HttpRequest request = parser.createRequest();
		Optional<DictionaryResult> result = httpService.queryAsync(request::asJsonAsync, parser::parse).join();
		if (result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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


