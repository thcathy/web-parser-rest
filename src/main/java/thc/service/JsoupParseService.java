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
            var url = parser.url();
            log.info("Query url: {}", url);
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.0.1 Safari/605.1.15")
                    .timeout(300000);
            setHeaders(connection, parser.headers());
            return parser.parseResponse(connection.get());
    })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume((e) -> {
            log.warn("Error when process jsoup request: {}, {}", e.toString(), parser.url());
            return Mono.just(parser.defaultValue());
        });
    }

    private void setHeaders(Connection connection, Map<String, String> headers) {
        headers.forEach(connection::header);
    }


}
