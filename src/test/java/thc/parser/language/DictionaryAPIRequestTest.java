package thc.parser.language;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import thc.domain.DictionaryResult;
import thc.service.RestParseService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class DictionaryAPIRequestTest {

    @Before
    public void setup() {
        DictionaryAPIRequest.API_KEY = System.getenv("dictionaryapi_key");
    }

    @Test
    public void testParseBanana() {
        Optional<DictionaryResult> result = new RestParseService().process(new DictionaryAPIRequest("banana")).blockOptional();

        assertTrue(result.isPresent());
        assertEquals("bə-ˈna-nə", result.get().IPA);
        Assert.assertThat(result.get().pronunciationUrl, Matchers.equalTo("https://media.merriam-webster.com/audio/prons/en/us/mp3/b/banana01.mp3"));
    }

    @Test
    public void testParseFailResult() {
        Optional<DictionaryResult> result = new RestParseService().process(new DictionaryAPIRequest("it is not a word")).blockOptional();
        assertFalse(result.isPresent());
    }

    @Test
    public void playedShouldNotPresent() {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream("dictionaryapi/play.json")) {
            var node = new ObjectMapper().readTree(inputStream);
            var request = new DictionaryAPIRequest("played");
            var result = request.parseResponse(node).blockOptional();
            assertFalse(result.isPresent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getSubDirectory() {
        DictionaryAPIRequest request = new DictionaryAPIRequest(null);
        assertThat(request.getSubDirectory("bix123")).isEqualTo("bix");
        assertThat(request.getSubDirectory("ggxyz")).isEqualTo("gg");
        assertThat(request.getSubDirectory("0d00001")).isEqualTo("number");
        assertThat(request.getSubDirectory("9d00001")).isEqualTo("number");
        assertThat(request.getSubDirectory("_d00001")).isEqualTo("number");
        assertThat(request.getSubDirectory("$d00001")).isEqualTo("number");
        assertThat(request.getSubDirectory("hola001sp")).isEqualTo("h");
    }
}
