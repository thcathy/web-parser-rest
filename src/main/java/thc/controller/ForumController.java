package thc.controller;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import thc.domain.ForumThread;
import thc.parser.forum.ForumThreadParser;
import thc.service.ForumQueryService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
public class ForumController {	
	private static Logger log = LoggerFactory.getLogger(ForumController.class);
	private Date earliestCreatedDate;
	private int pagePerBatch;
		
	enum ContentType {
		MUSIC(new String[]{
				"http://www.uwants.com/forumdisplay.php?fid=472&page=%d",
				"http://www.uwants.com/forumdisplay.php?fid=471&page=%d",
				"http://www.discuss.com.hk/forumdisplay.php?fid=101&page=%d",
				"http://www.tvboxnow.com/forum-50-%d.html",
				"http://www.tvboxnow.com/forum-153-%d.html"
		}), 
		MOVIE(new String[]{
				"http://www.uwants.com/forumdisplay.php?fid=231&page=%d",
				"http://www.uwants.com/forumdisplay.php?fid=7&page=%d",
				"http://www.uwants.com/forumdisplay.php?fid=406&page=%d",
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
    public List<ForumThread> list(@PathVariable String type, @PathVariable int batch) {
    	log.debug("hkQuotes: type [{}], batch [{}]", type, batch);

        ContentType contentType = ContentType.valueOf(type.toUpperCase());        
    	List<ForumThreadParser> parsers = createParsersByType(contentType, batch);
        List<ForumThread> allThreads = queryService.query(parsers);
        return filterAndSortForumThread(allThreads);
    }
        
    private List<ForumThreadParser> createParsersByType(ContentType type, int batch) {
    	return type.urls.stream()
    			.flatMap(url -> 
                        forPages(batch).map(i -> ForumThreadParser.buildParser(url,i))
    			)
    			.collect(Collectors.toList());    			
    }
    
    private List<ForumThread> filterAndSortForumThread(List<ForumThread> threads) {
        return threads.stream()
                .filter(f -> f.getCreatedDate().compareTo(earliestCreatedDate) >= 0)
                .sorted((a,b)->b.getCreatedDate().compareTo(a.getCreatedDate()))
                .collect(Collectors.toList());
    }

	private int toPage(int batch) {
		return pagePerBatch * batch;
	}

	private int fromPage(int batch) {
		return 1 + (pagePerBatch * (batch-1));
	}		
	
	private Stream<Integer> forPages(int batch) {
		return IntStream.rangeClosed(fromPage(batch), toPage(batch)).boxed();
	}
}
