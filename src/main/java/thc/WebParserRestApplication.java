package thc;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
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
import thc.parser.forum.DiscussThreadParser;
import thc.parser.forum.TvboxnowThreadParser;
import thc.parser.forum.UwantsThreadParser;
import thc.parser.language.OxfordDictionaryRequest;
import thc.parser.search.GoogleImageSearchRequest;
import thc.service.ForumQueryService;
import thc.service.HttpParseService;
import thc.service.RestParseService;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import java.util.Optional;

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

	@Value("${tvboxnow.username}") String tvboxnowUsername;
	@Value("${tvboxnow.password}") String tvboxnowPassword;
	@Value("${discuss.username}") String discussUsername;
	@Value("${discuss.password}") String discussPassword;
	@Value("${googleapi.key}") String googleAPIKey;
	@Value("${oxford.dictionary.appId}") String oxfordDictionaryAppId;
	@Value("${oxford.dictionary.appKey}") String oxfordDictionaryAppKey;
	@Value("${webclient.log.enable:true}") boolean enableWebClientLog;

    @PostConstruct
    public void configure() {
		TvboxnowThreadParser.USERNAME = tvboxnowUsername;
        TvboxnowThreadParser.PASSWORD = tvboxnowPassword;
        UwantsThreadParser.USERNAME = discussUsername;
        UwantsThreadParser.PASSWORD = discussPassword;
		DiscussThreadParser.USERNAME = discussUsername;
		DiscussThreadParser.PASSWORD = discussPassword;
		GoogleImageSearchRequest.setAPIKeys(googleAPIKey);
		OxfordDictionaryRequest.APP_ID_LIST = Optional.ofNullable(oxfordDictionaryAppId).orElse("").split(OxfordDictionaryRequest.KEY_SEPARATOR);
		OxfordDictionaryRequest.APP_KEY_LIST = Optional.ofNullable(oxfordDictionaryAppKey).orElse("").split(OxfordDictionaryRequest.KEY_SEPARATOR);
    }

    @Bean public ForumQueryService forumQueryService() { return new ForumQueryService(httpClient()); }

    @Bean public HttpParseService httpParseService() { return new HttpParseService(httpClient()); }

    @Bean public RestParseService restParseService() { return new RestParseService(); }
		
	//@Bean
    //public WebMvcConfigurer corsConfigurer() {
    //    return new WebMvcConfigurerAdapter() {
    //        @Override
    //        public void addCorsMappings(CorsRegistry registry) {
    //            registry.addMapping("/rest/**");
    //        }
    //    };
    //}

    @Bean
	public static AsyncHttpClient httpClient() {
    	SslContext sslContext = null;
		try {
			sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
		} catch (SSLException e) {
			e.printStackTrace();
		}
		return asyncHttpClient(config().setSslContext(sslContext));
	}

	/**
	 * Main function for the whole application
	 */
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext c = SpringApplication.run(WebParserRestApplication.class, args);        
	}

}