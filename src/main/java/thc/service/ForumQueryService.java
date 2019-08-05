package thc.service;

import com.google.common.collect.Lists;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import thc.domain.ForumThread;
import thc.parser.forum.ForumThreadParser;
import thc.util.HttpClientUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForumQueryService {
    private static Logger log = LoggerFactory.getLogger(ForumQueryService.class);

    private AsyncHttpClient asyncHttpClient;

    public ForumQueryService(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

    public List<ForumThread> query(ForumThreadParser parsers) {
        return query(Lists.newArrayList(parsers));
    }

    public List<ForumThread> query(List<ForumThreadParser> parsers) {
        loginForums(parsers);

        List<CompletableFuture<List<ForumThread>>> futures = parsers.stream()
                .map(this::queryForum)
                .collect(Collectors.toList());

        return futures.stream()
                .flatMap(this::toForumThreadStream)
                .collect(Collectors.toList());
    }

    public Flux<ForumThread> queryFlux(Flux<ForumThreadParser> parsers) {
        parsers = parsers.share();

        Mono<Map<String, Mono<Response>>> loginResponse = parsers.map(p -> p.loginUrl.orElse("https://www.google.com"))
                .distinct()
                .collectMap(k -> k, k -> Mono.fromFuture(asyncHttpClient.prepareGet(k).execute().toCompletableFuture()));

        return parsers.zipWith(loginResponse)
                .log()
                .flatMap(result -> {
                    var parser = result.getT1();
                    var mono = result.getT2().get(parser.loginUrl.orElse("https://www.google.com"));
                    return mono.flatMapMany(l -> queryForumFlux(parser));
                });
    }

    private CompletableFuture<List<ForumThread>> queryForum(ForumThreadParser parser) {
        return asyncHttpClient.prepareGet(parser.url)
                .execute().toCompletableFuture()
                .exceptionally(t -> HttpClientUtils.nullResponseOnError(parser.url, t))
                .thenApply(response -> parser.parse(response));
    }

    private Flux<ForumThread> queryForumFlux(ForumThreadParser parser) {
        return Mono.fromFuture(asyncHttpClient.prepareGet(parser.url).execute().toCompletableFuture())
                .flatMapIterable(response -> parser.parse(response));
    }

    private Stream<ForumThread> toForumThreadStream(CompletableFuture<List<ForumThread>> future) {
        try {
            return future.join().stream();
        } catch (Exception e) {
            log.error("Cannot get forum threads", e);
            return Stream.empty();
        }
    }

    private void loginForums(List<ForumThreadParser> parsers) {
        List<CompletableFuture<Response>> loginResponse = parsers.stream()
                .map(p -> p.loginUrl)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .map(url -> asyncHttpClient.prepareGet(url).execute().toCompletableFuture())
                .collect(Collectors.toList());

        loginResponse.stream().forEach(this::logHttpResponseStatus);
    }

    private void logHttpResponseStatus(CompletableFuture<Response> completableFuture) {
        Response response = completableFuture.join();
        log.debug("logHttpResponseStatus {} - {}", response.getStatusCode(), response.getStatusText());
    }

}