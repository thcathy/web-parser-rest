package thc.controller;

import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import thc.WebParserRestApplication;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebParserRestApplication.class)
public class DictionaryControllerIntegrationTest {
	private Logger log = LoggerFactory.getLogger(DictionaryControllerIntegrationTest.class);

    @Autowired DictionaryController controller;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

	@Test
	public void query_shouldReturnResult() throws Exception {
        Stopwatch timer = Stopwatch.createStarted();

        mockMvc.perform(get("/rest/dictionary/apple"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("word", is("apple")));

        log.info("query_shouldReturnResult took: {}", timer.stop());
    }

    @Test
    public void query_notAWord_shouldReturn404() throws Exception {
        Stopwatch timer = Stopwatch.createStarted();

        mockMvc.perform(get("/rest/dictionary/-------"))
                .andExpect(status().isNotFound());

        log.info("query_shouldReturnResult took: {}", timer.stop());
    }

    @Test
    public void queryToward_shouldReturnResult() throws Exception {
        mockMvc.perform(get("/rest/dictionary/toward"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("word", is("toward")))
                .andExpect(jsonPath("pronunciationUrl", is("http://audio.oxforddictionaries.com/en/mp3/toward_gb_1_8.mp3")))
                .andExpect(jsonPath("pronunciationLang", is("British English")))
                .andExpect(jsonPath("IPA", is("twɔːd")));
    }

    @Test
    public void querySenior_shouldReturnResult() throws Exception {
        mockMvc.perform(get("/rest/dictionary/senior"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("word", is("senior")))
                .andExpect(jsonPath("pronunciationUrl", is("http://audio.oxforddictionaries.com/en/mp3/senior_gb_1_8.mp3")))
                .andExpect(jsonPath("pronunciationLang", is("British English")))
                .andExpect(jsonPath("definition", is("of or for older or more experienced people")))
                .andExpect(jsonPath("IPA", is("ˈsiːnɪə")));
    }

}
