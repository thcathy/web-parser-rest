package thc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thc.domain.WebItem;
import thc.parser.search.GoogleImageSearchRequest;
import thc.service.HttpParseService;

import javax.cache.CacheManager;
import javax.cache.annotation.CacheResult;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class SearchController {
	private static Logger log = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	HttpParseService parseService;

	@CacheResult(cacheName = "image_search")
    @RequestMapping(value = "/rest/search/image/{query}", method = GET)
    public List<WebItem> searchImage(@PathVariable String query) {
    	log.debug("searchImage: {}", query);

		GoogleImageSearchRequest request = new GoogleImageSearchRequest(query);
		return parseService.process(request).join();
    }

	@Component
	public static class CachingSetup implements JCacheManagerCustomizer
	{
		@Override
		public void customize(CacheManager cacheManager)
		{
			cacheManager.createCache("image_search", new MutableConfiguration<>()
					.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.DAYS, 1)))
					.setStoreByValue(false)
					.setStatisticsEnabled(true));
		}
	}

}
