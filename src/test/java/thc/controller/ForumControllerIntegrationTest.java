package thc.controller;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import thc.domain.ForumThread;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

@Ignore
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = WebParserRestApplication.class)
public class ForumControllerIntegrationTest {
	private Logger log = LoggerFactory.getLogger(ForumControllerIntegrationTest.class);
	
	@Autowired ForumController controller;	
	@Value("${forum.threadEarliestDay}") int threadShouldNotOlderDay;

	private static int MIN_NUM_OF_THREADS = 120;

	@Test
	public void list_MusicPage1_ShouldReturnDescendingForumThreadsNotOlderThanConfig() {
		Stopwatch timer = Stopwatch.createStarted();
		
		List<ForumThread> contents = controller.list("MUSIC", 1).collectList().block();

		log.info("list_MusicPage1_ShouldReturnDecendingForumThreadsNotOlderThanConfig took: {}", timer.stop());

		assertTrue("Number of thread " + contents.size() + " < " + MIN_NUM_OF_THREADS, contents.size() > MIN_NUM_OF_THREADS);
		boolean descSortedByDate = IntStream.range(0, contents.size()-1)
				.allMatch(i -> contents.get(i).getCreatedDate().getTime() >= contents.get(i+1).getCreatedDate().getTime());
		assertTrue("Contents are decending ordered by created date", descSortedByDate);
		contents.forEach(x -> {
			assert StringUtils.isNotBlank(x.getUrl());
			assert StringUtils.isNotBlank(x.getTitle());
		});

		Date earliestCreatedDate = DateUtils.addDays(new Date(), -threadShouldNotOlderDay);
		contents.forEach(x -> {
			assertTrue("The thread created on " + x.getCreatedDate() + " is older than " + threadShouldNotOlderDay + " days", x.getCreatedDate().compareTo(earliestCreatedDate) >= 0);
		});
	}

	@Test
	public void list_MoviePage1_ShouldReturnDescendingForumThreadsNotOlderThanConfig() {
		Stopwatch timer = Stopwatch.createStarted();
		
		List<ForumThread> contents = controller.list("MOVIE", 1).collectList().block();

		log.info("list_MoviePage1_ShouldReturnDecendingForumThreadsNotOlderThanConfig took: {}", timer.stop());

		assertTrue("Number of thread " + contents.size() + " < " + 40, contents.size() > 40);
		boolean descSortedByDate = IntStream.range(0, contents.size()-1)
				.allMatch(i -> contents.get(i).getCreatedDate().getTime() >= contents.get(i+1).getCreatedDate().getTime());
		assertTrue("Contents are decending ordered by created date", descSortedByDate);
		contents.forEach(x -> {
			log.debug(x.toString());
			assert StringUtils.isNotBlank(x.getUrl());
			//assert StringUtils.isNotBlank(x.getTitle()); // title can be blank
		});

		Date earliestCreatedDate = DateUtils.addDays(new Date(), -threadShouldNotOlderDay);
		contents.forEach(x -> {
			assertTrue("The thread created on " + x.getCreatedDate() + " is older than " + threadShouldNotOlderDay + " days", x.getCreatedDate().compareTo(earliestCreatedDate) >= 0);
		});
	}
}
