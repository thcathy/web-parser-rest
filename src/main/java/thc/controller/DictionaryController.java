package thc.controller;

import com.mashape.unirest.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thc.domain.DictionaryResult;
import thc.parser.language.LongmanDictionaryParser;
import thc.service.HttpService;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class DictionaryController {
	private static Logger log = LoggerFactory.getLogger(DictionaryController.class);

	@Autowired HttpService httpService;

    @RequestMapping(value = "/rest/dictionary/{query}", method = GET)
    public Optional<DictionaryResult> query(@PathVariable String query) {
    	log.debug("query: {}", query);

		LongmanDictionaryParser parser = new LongmanDictionaryParser(query);
		HttpRequest request = parser.createRequest();
		return httpService.queryAsync(request::asJsonAsync, parser::parse).join();
    }

}
