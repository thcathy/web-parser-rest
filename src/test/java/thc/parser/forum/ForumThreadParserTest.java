package thc.parser.forum;

import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.Dsl;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.ForumThread;
import thc.service.ForumQueryService;

import java.util.List;

public class ForumThreadParserTest {
	private static Logger log = LoggerFactory.getLogger(ForumThreadParserTest.class);
	
	private ForumQueryService queryService = new ForumQueryService(Dsl.asyncHttpClient());

    static {
        TvboxnowThreadParser.USERNAME = System.getProperty("tvboxnow.username");
        TvboxnowThreadParser.PASSWORD = System.getProperty("tvboxnow.password");
        UwantsThreadParser.USERNAME = System.getProperty("discuss.username");
        UwantsThreadParser.PASSWORD = System.getProperty("discuss.password");
    }
	
	@Test
	public void parse_GivenRightURL_ShouldReturnSomeForumThread() {
		// Test Uwants
		String uwantsSource = "Uwants";
		ForumThreadParser uwants = new UwantsThreadParser("https://www.uwants.com/forumdisplay.php?fid=472&page=1", uwantsSource, "UTF-8");
		List<ForumThread> result = queryService.query(uwants);
        
		checkForumThreadList(uwantsSource, "www.uwants.com", result);
		
		// Test Discuss
		String discussSource = "Discuss";
		ForumThreadParser discuss = new UwantsThreadParser("https://www.discuss.com.hk/forumdisplay.php?fid=101&page=2", discussSource, "UTF-8");
		result = queryService.query(discuss);
		checkForumThreadList(discussSource, "www.discuss.com.hk", result);
		
		// Test Tvbox
		String tvboxSource = "Tvboxnow";
		ForumThreadParser tvb = new TvboxnowThreadParser("http://www.tvboxnow.com/forum-50-1.html", tvboxSource);
		result = queryService.query(tvb);
        checkForumThreadList(tvboxSource, "www.tvboxnow.com", result);
	}

	private void checkForumThreadList(String uwantsSource, String urlPrefix, List<ForumThread> result) {
		Assert.assertTrue("Return should not empty", result.size() > 0);
				
		log.info("First ForumThread: {}", result.get(0).toString());
		result.forEach(f->{
			Assert.assertEquals("Source should be same as the one put in constructor", uwantsSource, f.getSource());			
			Assert.assertTrue("URL should start with correct prefix", f.getUrl().startsWith(urlPrefix));
			assert StringUtils.isNotBlank(f.getTitle());
		});		
	}
		
}
