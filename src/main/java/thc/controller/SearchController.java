package thc.controller;

import com.mashape.unirest.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thc.domain.WebItem;
import thc.parser.search.GoogleImageSearch;
import thc.service.HttpService;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class SearchController {
	private static Logger log = LoggerFactory.getLogger(SearchController.class);

	@Autowired HttpService httpService;

    @RequestMapping(value = "/rest/search/image/{query}", method = GET)
    public List<WebItem> searchImage(@PathVariable String query) {
    	log.debug("searchImage: {}", query);

		HttpRequest request = GoogleImageSearch.createRequest(query);
		return httpService.queryAsync(request::asJsonAsync, GoogleImageSearch::parse).join();
    }

}
