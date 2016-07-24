package thc;

import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
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
import thc.service.ForumQueryService;
import thc.service.HttpService;
import thc.unirest.UnirestSetup;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

@SpringBootApplication
public class WebParserRestApplication extends SpringBootServletInitializer {

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

    @PostConstruct
    public void configure() {        
        TvboxnowThreadParser.USERNAME = env.getProperty("tvboxnow.username");
        TvboxnowThreadParser.PASSWORD = env.getProperty("tvboxnow.password");
        UwantsThreadParser.USERNAME = env.getProperty("discuss.username");
        UwantsThreadParser.PASSWORD = env.getProperty("discuss.password");
        
        UnirestSetup.setupAll();
        Unirest.setConcurrency(
                env.getProperty("http.max_connection", Integer.class , 20), 
                env.getProperty("http.max_connection_per_route", Integer.class, 20)
        );
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
        	
	/**
	 * Main function for the whole application
	 */
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext c = SpringApplication.run(WebParserRestApplication.class, args);        
	}

}