package thc.service;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import thc.domain.ForumThread;
import thc.parser.forum.ForumThreadParser;

public class ForumQueryService {
    private static Logger log = LoggerFactory.getLogger(ForumQueryService.class);

    private AsyncHttpClient asyncHttpClient;

    public ForumQueryService(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

    public Flux<ForumThread> queryFlux(Flux<ForumThreadParser> parsers) {
        final var sharedParsers = parsers.share();

        var loginMaps = sharedParsers.map(p -> p.loginUrl)
                .distinct()
                .collectMap(k -> k, k -> login(k));

        return loginMaps.flatMapMany(m ->
                sharedParsers.flatMap(
                        p -> m.get(p.loginUrl).flatMapMany(l -> queryForumFlux(p))
                )
        );

    }

    private Mono<Response> login(String url) {
        log.debug("login: {}", url);
        return Mono.fromFuture(asyncHttpClient.prepareGet(url).execute().toCompletableFuture())
                .doOnError(e -> log.error("error when login", e));
    }

    private Flux<ForumThread> queryForumFlux(ForumThreadParser parser) {
        log.debug("query url: {}", parser.url);
        return Mono.fromFuture(asyncHttpClient.prepareGet(parser.url).execute().toCompletableFuture())
                .flatMapIterable(response -> parser.parse(response))
                .doOnError(e -> log.error("error when query", e));
    }

}