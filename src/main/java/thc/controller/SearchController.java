package thc.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import thc.parser.search.GoogleImageSearchRequest;
import thc.service.RestParseService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class SearchController {
	private static Logger log = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	RestParseService parseService;

	final protected Cache<String, Object> cache;

	public SearchController() {
		cache = Caffeine
				.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(1, TimeUnit.DAYS)
				.build();
	}

	@RequestMapping(value = "/rest/search/image/{query}", method = GET)
	public Mono<List> searchImage(@PathVariable String query,
								  @RequestParam(required = false, defaultValue = "medium") String imgSize,
								  @RequestParam(required = false, defaultValue = "1") int start) {
		log.debug("searchImage: {}", query);

		GoogleImageSearchRequest request = new GoogleImageSearchRequest(query).setStart(start).setImgSize(imgSize);

		return CacheMono
				.lookup(cache.asMap(), cacheKey(query, imgSize, start), List.class)
				.onCacheMissResume(() -> parseService.process(request));
	}

	private String cacheKey(String query, String imgSize, int start) {
		return query + imgSize + start;
	}

}
