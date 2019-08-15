package thc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import thc.parser.RestParseRequest;

import java.net.URI;
import java.util.Map;

public class RestParseService {
    protected final Logger log = LoggerFactory.getLogger(RestParseService.class);

    public <T> Mono<T> process(RestParseRequest<T> parser) {
        return WebClient.builder().baseUrl(parser.url()).build().get()
                .uri(builder -> buildURI(builder, parser))
                .headers(headers -> addHeaders(parser, headers))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(this::whenError)
                .flatMap(body -> parser.parseResponse(body));
    }

    private Mono<? extends JsonNode> whenError(Throwable throwable) {
        log.error("Error when query", throwable);
        return Mono.just(NullNode.getInstance());
    }

    private <T> URI buildURI(UriBuilder builder, RestParseRequest<T> parser) {
        for (Map.Entry<String, String> entry : parser.queryParams().entrySet()) {
            builder = builder.queryParam(entry.getKey(), entry.getValue());
        }
        var uri = builder.build();
        log.debug(uri.toString());
        return uri;
    }

    private <T> void addHeaders(RestParseRequest<T> parser, HttpHeaders httpHeaders) {
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        parser.headers().entrySet()
                .forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue()));
    }

}