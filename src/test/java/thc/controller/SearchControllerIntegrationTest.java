package thc.controller;

import com.google.common.base.Stopwatch;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import thc.WebParserRestApplication;
import thc.domain.WebItem;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebParserRestApplication.class)
@Ignore
public class SearchControllerIntegrationTest {
	private Logger log = LoggerFactory.getLogger(SearchControllerIntegrationTest.class);

    @Autowired SearchController controller;

	@Test
	public void searchImage_shouldReturnWebItems() {
        Stopwatch timer = Stopwatch.createStarted();

        List<WebItem> items = (List<WebItem>) controller.searchImage("book+clipart").block();
        log.info("searchImage_shouldReturnWebItems took: {}", timer.stop());
        items.forEach(this::checkItem);

        assertEquals(10, items.size());

        items = (List<WebItem>) controller.searchImage("book+clipart").block();
        assertEquals(10, items.size());
        items.forEach(this::checkItem);
    }

    @Test
    public void searchImage_givenRubbish_shouldReturnEmptyWebItemArray() {
        List<WebItem> items = (List<WebItem>) controller.searchImage("lksdajflksdalkfjlkwejr clipart").block();
        assertEquals(0, items.size());
    }

    private void checkItem(WebItem webItem) {
        log.info("WebItem: {}", webItem);
        assertTrue(webItem.url.startsWith("http"));
        assertTrue(webItem.mime.startsWith("image"));
        assertTrue(webItem.imageHeight > 10);
        assertTrue(webItem.imageWidth > 10);
    }
}
