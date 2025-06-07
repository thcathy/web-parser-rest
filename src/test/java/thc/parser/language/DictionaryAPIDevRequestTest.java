package thc.parser.language;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import thc.domain.DictionaryResult;
import thc.service.RestParseService;

import java.util.Optional;

import static org.junit.Assert.*;

public class DictionaryAPIDevRequestTest {
    @Test
    public void testParsePlayed() {
        Optional<DictionaryResult> result = new RestParseService().process(new DictionaryAPIDevRequest("played")).blockOptional();
        assertTrue(result.isPresent());
        assertEquals("pleɪd", result.get().IPA);
        MatcherAssert.assertThat(result.get().pronunciationUrl, Matchers.equalTo("https://api.dictionaryapi.dev/media/pronunciations/en/played-us.mp3"));
    }

    @Test
    public void testParseFailResult() {
        Optional<DictionaryResult> result = new RestParseService().process(new DictionaryAPIDevRequest("it is not a word")).blockOptional();
        assertFalse(result.isPresent());
    }
}
