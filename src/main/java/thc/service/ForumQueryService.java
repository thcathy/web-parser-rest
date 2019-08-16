package thc.service;

import org.asynchttpclient.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import thc.domain.ForumThread;
import thc.parser.forum.ForumThreadParser;

public class ForumQueryService {
    private static Logger log = LoggerFactory.getLogger(ForumQueryService.class);

    private AsyncHttpClient asyncHttpClient;

    public ForumQueryService(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

    public Flux<ForumThread> queryFlux(Flux<ForumThreadParser> parsers) {
        parsers = parsers.share();

        var loginMonos = parsers.map(p -> p.loginUrl)
                .distinct()
                .collectMap(k -> k, k -> Mono.fromFuture(asyncHttpClient.prepareGet(k).execute().toCompletableFuture()))
                .subscribeOn(Schedulers.elastic())
                .block();

        return parsers.flatMap(p ->
            loginMonos.get(p.loginUrl).flatMapMany(l -> queryForumFlux(p))
        );
    }

    private Flux<ForumThread> queryForumFlux(ForumThreadParser parser) {
        return Mono.fromFuture(asyncHttpClient.prepareGet(parser.url).execute().toCompletableFuture())
                .flatMapIterable(response -> parser.parse(response));
    }

}