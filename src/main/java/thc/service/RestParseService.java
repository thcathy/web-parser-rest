package thc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import thc.parser.RestParseRequest;

public class RestParseService {
    protected final Logger log = LoggerFactory.getLogger(RestParseService.class);

    WebClient webClient = WebClient.create();

    public <T> Mono<T> process(RestParseRequest<T> parser) {
        return webClient.get()
                .uri(parser.url())
                .headers(headers -> addHeaders(parser, headers))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorReturn(NullNode.getInstance())
                .flatMap(body -> parser.parseResponse(body));
    }

    private <T> void addHeaders(RestParseRequest<T> parser, HttpHeaders httpHeaders) {
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        parser.headers().entrySet()
                .forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue()));
    }

}