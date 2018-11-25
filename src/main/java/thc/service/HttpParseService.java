package thc.service;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.parser.HttpParseRequest;

import java.util.concurrent.CompletableFuture;

public class HttpParseService {
    protected final Logger log = LoggerFactory.getLogger(HttpParseService.class);

    private final AsyncHttpClient httpClient;

    public HttpParseService(AsyncHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public <U> CompletableFuture<U> process(HttpParseRequest<U> parser) {
        log.info("Start process: {}", parser.url());

        BoundRequestBuilder builder = httpClient.prepareGet(parser.url()).setFollowRedirect(true);
        addHeader(builder, parser);
        addQueryParam(builder, parser);

        return builder.execute().toCompletableFuture()
                .exceptionally(t -> nullResponseOnError(parser.url(), t))
                .thenApply(response -> parser.parseResponse(response.getResponseBodyAsStream()));
    }


    private <U> void addQueryParam(BoundRequestBuilder builder, HttpParseRequest<U> parser) {
        parser.queryParams().entrySet()
                .forEach(entry -> builder.addQueryParam(entry.getKey(), entry.getValue()));
    }

    private <U> void addHeader(BoundRequestBuilder builder, HttpParseRequest<U> parser) {
        parser.headers().entrySet()
                .forEach(entry -> builder.addHeader(entry.getKey(), entry.getValue()));
    }

    public Response nullResponseOnError(String url, Throwable t) {
        log.error("Error when querying: {}", url, t);
        return null;
    }

}
