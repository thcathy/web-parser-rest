package thc;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.reactive.function.client.ClientResponse;
import static org.asynchttpclient.Dsl.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebParserRestApplication.class)
public class AsyncHttpClientTest {

    @Autowired
    AsyncHttpClient asyncHttpClient = asyncHttpClient();

    @Test
    public void webClientIsCreatedOnApplicationStart() {
        ListenableFuture<Response> whenResponse = asyncHttpClient.prepareGet("https://api.github.com/search/repositories/")
                .addQueryParam("q","web-parser-rest")
                .execute();

        try {
            Response response = whenResponse.get();
            System.out.println(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        };
    }
}
