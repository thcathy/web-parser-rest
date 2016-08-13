package thc.controller;

import com.google.common.base.Stopwatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import thc.WebParserRestApplication;
import thc.domain.WebItem;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebParserRestApplication.class)
public class SearchControllerIntegrationTest {
	private Logger log = LoggerFactory.getLogger(SearchControllerIntegrationTest.class);

    @Autowired SearchController controller;

	@Test
	public void searchImage_shouldReturnWebItems() throws Exception {
        Stopwatch timer = Stopwatch.createStarted();

        List<WebItem> items = controller.searchImage("book+clipart");
        log.info("searchImage_shouldReturnWebItems took: {}", timer.stop());

        assertEquals(10, items.size());
        items.forEach(this::checkItem);
    }

    private void checkItem(WebItem webItem) {
        log.info("WebItem: {}", webItem);
        assertTrue(webItem.url.startsWith("http"));
        assertTrue(webItem.mime.startsWith("image"));
        assertTrue(webItem.imageHeight > 10);
        assertTrue(webItem.imageWidth > 10);
    }
}
