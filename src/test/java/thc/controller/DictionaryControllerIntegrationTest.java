package thc.controller;

import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import thc.WebParserRestApplication;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebParserRestApplication.class)
public class DictionaryControllerIntegrationTest {
	private Logger log = LoggerFactory.getLogger(DictionaryControllerIntegrationTest.class);

    @Autowired DictionaryController controller;

    @Before
    public void setup() {}

	@Test
	public void query_shouldReturnResult() {
        Stopwatch timer = Stopwatch.createStarted();

        var result = controller.query("apple").block();
        assertThat(result.word).isEqualTo("apple");

        log.info("query_shouldReturnResult took: {}", timer.stop());
    }

    @Test
    public void query_notAWord_shouldReturn404() {
        Stopwatch timer = Stopwatch.createStarted();

        var result = controller.query("--------").blockOptional();
        assertThat(result.isPresent()).isFalse();

        log.info("query_shouldReturnResult took: {}", timer.stop());
    }

    @Test
    public void queryToward_shouldReturnResult() {
        var result = controller.query("toward").block();
        assertThat(result.word).isEqualTo("toward");
        assertThat(result.pronunciationUrl).isEqualTo("https://dictionary.cambridge.org/media/english/us_pron/e/eus/eus74/eus74594.mp3");
        assertThat(result.pronunciationLang).isEqualTo("British English");
        assertThat(result.IPA).isEqualTo("tɔrd");
    }

    @Test
    public void querySenior_shouldReturnResult() {
        var result = controller.query("senior").block();
        assertThat(result.word).isEqualTo("senior");
        assertThat(result.pronunciationUrl).isEqualTo("https://dictionary.cambridge.org/media/english/uk_pron/u/uks/uksen/uksen__012.mp3");
        assertThat(result.pronunciationLang).isEqualTo("British English");
        assertThat(result.IPA).isEqualTo("ˈsiː.ni.ə");
    }

    @Test
    public void queryCenter_shouldReturnResult() {
        var result = controller.query("center").block();
        assertThat(result.word).isEqualTo("center");
        assertThat(result.pronunciationUrl).isEqualTo("https://dictionary.cambridge.org/media/english/uk_pron/u/ukc/ukcen/ukcensu007.mp3");
        assertThat(result.pronunciationLang).isEqualTo("British English");
        assertThat(result.IPA).isEqualTo("ˈsen.tə");
    }

    @Test
    public void queryAnymore_shouldReturnResult() {
        var result = controller.query("anymore").block();
        assertThat(result.word).isEqualTo("anymore");
        assertThat(result.pronunciationUrl).isEqualTo("https://dictionary.cambridge.org/media/english/uk_pron/u/uka/ukant/ukantis017.mp3");
        assertThat(result.pronunciationLang).isEqualTo("British English");
        assertThat(result.IPA).isEqualTo("ˌen.iˈmɔː");
    }

    @Test
    public void queryProgram_shouldReturnResult() {
        var result = controller.query("program").block();
        assertThat(result.word).isEqualTo("program");
        assertThat(result.pronunciationUrl).isEqualTo("https://dictionary.cambridge.org/media/english/uk_pron/u/ukp/ukpro/ukprofi026.mp3");
        assertThat(result.pronunciationLang).isEqualTo("British English");
        assertThat(result.IPA).isEqualTo("ˈprəʊ.ɡræm");
    }

}