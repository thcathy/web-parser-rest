package thc.controller;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Flux;
import thc.domain.ForumThread;
import thc.parser.forum.ForumThreadParser;
import thc.service.ForumQueryService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//@RestController
@Deprecated
public class ForumController {	
	private static Logger log = LoggerFactory.getLogger(ForumController.class);
	private Date earliestCreatedDate;
	private int pagePerBatch;
		
	enum ContentType {
		MUSIC(new String[]{
				"https://www.uwants.com/forumdisplay.php?fid=472&page=%d",
				"https://www.uwants.com/forumdisplay.php?fid=471&page=%d",
				"https://www.discuss.com.hk/forumdisplay.php?fid=101&page=%d",
				"http://www.tvboxnow.com/forum-50-%d.html",
				"http://www.tvboxnow.com/forum-153-%d.html"
		}), 
		MOVIE(new String[]{
				"https://www.uwants.com/forumdisplay.php?fid=231&page=%d",
				"https://www.uwants.com/forumdisplay.php?fid=7&page=%d",
				"https://www.uwants.com/forumdisplay.php?fid=406&page=%d",
				"http://www.tvboxnow.com/forum-231-%d.html",
				"http://www.tvboxnow.com/forum-232-%d.html",
				"http://www.tvboxnow.com/forum-233-%d.html"
		});
		
		final private List<String> urls;
		
		ContentType(String[] urls) { 
			this.urls = Collections.unmodifiableList(Arrays.asList(urls));
		}
	}
		
    @Autowired
    ForumQueryService queryService; 
	
    @Autowired
	public ForumController(@Value("${forum.threadEarliestDay}") int threadShouldNotOlderDay, @Value("${forum.pagePerBatch}") int pagePerBatch) {	
		earliestCreatedDate = DateUtils.addDays(new Date(), -threadShouldNotOlderDay);
		this.pagePerBatch = pagePerBatch;
	}
			
    @RequestMapping(value = "/rest/forum/list/{type}/{batch}", method = RequestMethod.GET)
    public Flux<ForumThread> list(@PathVariable String type, @PathVariable int batch) {
    	log.debug("list: type [{}], batch [{}]", type, batch);

        ContentType contentType = ContentType.valueOf(type.toUpperCase());        
    	var parsers = createParsersByType(contentType, batch);
        Flux<ForumThread> allThreads = queryService.queryFlux(parsers);
        return filterAndSortForumThread(allThreads);
    }
        
    private Flux<ForumThreadParser> createParsersByType(ContentType type, int batch) {
		return Flux.fromIterable(type.urls)
				.flatMap(url ->
						Flux.range(fromPage(batch), toPage(batch))
								.map(i -> ForumThreadParser.buildParser(url,i))
				);
    }
    
    private Flux<ForumThread> filterAndSortForumThread(Flux<ForumThread> threads) {
        return threads
                .filter(f -> f.getCreatedDate().compareTo(earliestCreatedDate) >= 0)
                .sort((a,b)->b.getCreatedDate().compareTo(a.getCreatedDate()));
    }

	private int toPage(int batch) {
		return pagePerBatch * batch;
	}

	private int fromPage(int batch) {
		return 1 + (pagePerBatch * (batch-1));
	}
}
