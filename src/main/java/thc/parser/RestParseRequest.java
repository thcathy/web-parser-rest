package thc.parser;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

public interface RestParseRequest<U> {
    String url();

    default Map<String, String> headers() {
        return Collections.emptyMap();
    }

    Mono<U> parseResponse(JsonNode node);
}