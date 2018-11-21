package thc;

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
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import thc.parser.forum.TvboxnowThreadParser;
import thc.parser.forum.UwantsThreadParser;
import thc.parser.language.LongmanDictionaryParser;
import thc.parser.language.OxfordDictionaryParser;
import thc.parser.search.GoogleImageSearch;
import thc.service.ForumQueryService;
import thc.service.HttpService;
import thc.unirest.UnirestSetup;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.Optional;

import static org.asynchttpclient.Dsl.asyncHttpClient;

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
	@Value("${http.max_connection:20}") int httpMaxConnection;
	@Value("${http.max_connection_per_route:20}") int httpMaxConnectionPerRoute;
	@Value("${googleapi.key}") String googleAPIKey;
	@Value("${pearsonapi.key}") String pearsonAPIKey;
	@Value("${oxford.dictionary.appId}") String oxfordDictionaryAppId;
	@Value("${oxford.dictionary.appKey}") String oxfordDictionaryAppKey;
	@Value("${webclient.log.enable:true}") boolean enableWebClientLog;

    @PostConstruct
    public void configure() {
		TvboxnowThreadParser.USERNAME = tvboxnowUsername;
        TvboxnowThreadParser.PASSWORD = tvboxnowPassword;
        UwantsThreadParser.USERNAME = discussUsername;
        UwantsThreadParser.PASSWORD = discussPassword;
		GoogleImageSearch.setAPIKeys(googleAPIKey);
		LongmanDictionaryParser.CONSUMER_KEY = pearsonAPIKey;
		OxfordDictionaryParser.APP_ID_LIST = Optional.ofNullable(oxfordDictionaryAppId).orElse("").split(OxfordDictionaryParser.KEY_SEPARATOR);
		OxfordDictionaryParser.APP_KEY_LIST = Optional.ofNullable(oxfordDictionaryAppKey).orElse("").split(OxfordDictionaryParser.KEY_SEPARATOR);

		UnirestSetup.MAX_TOTAL_HTTP_CONNECTION = httpMaxConnection;
		UnirestSetup.MAX_HTTP_CONNECTION_PER_ROUTE = httpMaxConnectionPerRoute;
        UnirestSetup.setupAll();
    }

    // Serivce Beans
	@Bean public HttpService httpService() { return new HttpService(); }

    @Bean public ForumQueryService forumQueryService() { return new ForumQueryService(httpService()); }

     		
	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}
		
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/rest/**");
            }
        };
    }

    @Bean
	public AsyncHttpClient httpClient() {
    	return asyncHttpClient();
	}

	/**
	 * Main function for the whole application
	 */
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext c = SpringApplication.run(WebParserRestApplication.class, args);        
	}

}