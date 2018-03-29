package thc.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import thc.unirest.UnirestSetup;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HttpService {
    public HttpService() {
        UnirestSetup.setupAll();
    }

    public <T, R> CompletableFuture<R> queryAsync(Function<Callback<T>, ?> queryFunction, Function<HttpResponse<T>, R> parser) {
        CompletableFuture<R> future = new CompletableFuture<>();
        queryFunction.apply(new UnirestCallback<>(future, parser));
        return future;
    }

    public <R> CompletableFuture<R> getAsync(String url, Function<HttpResponse<InputStream>, R> parser) {
        return queryAsync(Unirest.get(url)::asBinaryAsync, parser);
    }

    public <R> CompletableFuture<R> getStringAsync(String url, Function<HttpResponse<String>, R> parser) {
        return queryAsync(Unirest.get(url)::asStringAsync, parser);
    }

    public <R> CompletableFuture<R> getAsync(String url) {
        return getAsync(url, this::nullParser);
    }

    public static class UnirestCallback<T, R> implements Callback<T> {
        private final CompletableFuture<R> cf;
        private final Function<HttpResponse<T>, R> parser;

        public UnirestCallback(CompletableFuture<R> cf, Function<HttpResponse<T>, R> parser) {
            this.cf = cf;
            this.parser = parser;
        }

        @Override
        public void completed(HttpResponse<T> response) {
            cf.complete(parser.apply(response));
        }

        public void failed(UnirestException e) {
            cf.completeExceptionally(e);
        }

        public void cancelled() {
            cf.completeExceptionally(new InterruptedException());
        }
    }

    public <R> R nullParser(HttpResponse<? extends Object> response) {
        return null;
    }
}
