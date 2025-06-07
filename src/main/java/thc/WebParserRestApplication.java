package thc;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import jakarta.annotation.PostConstruct;
import org.asynchttpclient.AsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import thc.parser.language.DictionaryAPIComRequest;
import thc.parser.search.GoogleImageSearchRequest;
import thc.service.ForumQueryService;
import thc.service.HttpParseService;
import thc.service.JsoupParseService;
import thc.service.RestParseService;

import javax.net.ssl.SSLException;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

@SpringBootApplication
@EnableCaching
public class WebParserRestApplication {
	private static Logger log = LoggerFactory.getLogger(WebParserRestApplication.class);

	@Configuration
	@PropertySource({"classpath:application.properties"})
	static class Default {}

	@Configuration
	@Profile("dev")
	@PropertySource({"classpath:application.properties", "classpath:application-dev.properties"})
	static class Dev {}
		
	// application properties
	@Autowired
	private Environment env;

	@Value("${googleapi.key}") String googleAPIKey;
	@Value("${dictionaryapi.key}") String dictionaryAPIKey;
	@Value("${webclient.log.enable:true}") boolean enableWebClientLog;

    @PostConstruct
    public void configure() {
		GoogleImageSearchRequest.setAPIKeys(googleAPIKey);
		DictionaryAPIComRequest.API_KEY = dictionaryAPIKey;
    }

    @Bean public ForumQueryService forumQueryService() { return new ForumQueryService(httpClient()); }

    @Bean public HttpParseService httpParseService() { return new HttpParseService(httpClient()); }

    @Bean public RestParseService restParseService() { return new RestParseService(); }

    @Bean public JsoupParseService jsoupParseService() { return new JsoupParseService(); }

    @Bean
	public static AsyncHttpClient httpClient() {
    	SslContext sslContext = null;
		try {
			sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} catch (SSLException e) {
			e.printStackTrace();
		}
		return asyncHttpClient(config()
						.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
						.setSslContext(sslContext)
		);
	}

	/**
	 * Main function for the whole application
	 */
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext c = SpringApplication.run(WebParserRestApplication.class, args);        
	}

}
