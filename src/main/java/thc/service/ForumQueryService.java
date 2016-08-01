package thc.service;

import com.google.common.collect.Lists;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.ForumThread;
import thc.parser.forum.ForumThreadParser;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by wongtim on 21/07/2016.
 */
public class ForumQueryService {
    private static Logger log = LoggerFactory.getLogger(ForumQueryService.class);

    private HttpService httpService;

    public ForumQueryService(HttpService httpService) {
        this.httpService = httpService;
    }

    public List<ForumThread> query(ForumThreadParser parsers) {
        return query(Lists.newArrayList(parsers));
    }

    public List<ForumThread> query(List<ForumThreadParser> parsers) {
        loginForums(parsers);

        List<CompletableFuture<List<ForumThread>>> futures = parsers.stream()
                .map(p -> httpService.getAsync(p.url, p::parse))
                .collect(Collectors.toList());

        return futures.stream()
                .flatMap(f -> f.join().stream())
                .collect(Collectors.toList());
    }

    private void loginForums(List<ForumThreadParser> parsers) {
        List<Future<HttpResponse<InputStream>>> loginResults = parsers.stream().map(p -> p.loginUrl).filter(Optional::isPresent).map(Optional::get)
                .distinct()
                .map(l -> Unirest.get(l).asBinaryAsync())
                .collect(Collectors.toList());
        
        loginResults.stream().forEach(this::logHttpResponseStatus);
    }

    private void logHttpResponseStatus(Future<HttpResponse<InputStream>> result) {
        try {
            log.debug("logHttpResponseStatus {}", result.get().getStatus());
        } catch (ExecutionException e) {
            log.debug("Ignore concurrent execution exception as response processed by another thread");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
