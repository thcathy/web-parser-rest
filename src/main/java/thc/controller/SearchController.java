package thc.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import thc.domain.WebItem;
import thc.parser.search.GoogleImageSearchRequest;
import thc.service.HttpParseService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class SearchController {
	private static Logger log = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	HttpParseService parseService;

	final protected Cache<String, Object> cache;

	public SearchController() {
		cache = Caffeine
				.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(1, TimeUnit.DAYS)
				.build();
	}

	@RequestMapping(value = "/rest/search/image/{query}", method = GET)
	public Mono<List> searchImage(@PathVariable String query) {
		log.debug("searchImage: {}", query);

		GoogleImageSearchRequest request = new GoogleImageSearchRequest(query);

		return CacheMono
				.lookup(cache.asMap(), query, List.class)
				.onCacheMissResume(parseService.processFlux(request));
	}

	public List<WebItem> oldSearchImage(@PathVariable String query) {
		log.debug("searchImage: {}", query);

		GoogleImageSearchRequest request = new GoogleImageSearchRequest(query);
		return parseService.process(request).join();
	}

}
