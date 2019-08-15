package thc.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import thc.parser.JsoupParseRequest;

import java.util.Map;

public class JsoupParseService {
    protected final Logger log = LoggerFactory.getLogger(JsoupParseService.class);

    public <T> Mono<T> process(JsoupParseRequest<T> parser) {
        return Mono.fromCallable(() -> {
            Connection connection = Jsoup.connect(parser.url()).validateTLSCertificates(false);
            setHeaders(connection, parser.headers());
            return parser.parseResponse(connection.get());
    })
                .subscribeOn(Schedulers.elastic())
                .onErrorResume((e) -> {
            log.warn("Error when process jsoup request: {}, {}", e.toString(), parser.url());
            return Mono.just(parser.defaultValue());
        });
    }

    private void setHeaders(Connection connection, Map<String, String> headers) {
        headers.entrySet().forEach((e) -> connection.header(e.getKey(), e.getValue()));
    }


}
