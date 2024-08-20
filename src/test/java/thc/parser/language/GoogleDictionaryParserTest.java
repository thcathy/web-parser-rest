package thc.parser.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoogleDictionaryParserTest {
    @Test
    public void parseHello() {
        var result = new GoogleDictionaryParser("hello").parse().block();
        assertEquals(3, result.meanings.size());
        assertTrue(result.meanings.contains("used as a greeting or to begin a phone conversation."));
    }

    @Test
    public void parseAirplane() {
        var result = new GoogleDictionaryParser("air plane").parse().block();
        assertEquals(1, result.meanings.size());
        assertTrue(result.meanings.stream().anyMatch(s -> s.contains("a powered flying vehicle with fixed wings")));

        result = new GoogleDictionaryParser("air-plane").parse().block();
        assertEquals(1, result.meanings.size());
        assertTrue(result.meanings.stream().anyMatch(s -> s.contains("a powered flying vehicle with fixed wings")));

        result = new GoogleDictionaryParser("airplane").parse().block();
        assertEquals(1, result.meanings.size());
        assertTrue(result.meanings.stream().anyMatch(s -> s.contains("a powered flying vehicle with fixed wings")));
    }

}
