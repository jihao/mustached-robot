package schedule;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application  extends SpringBootServletInitializer {
    
    @Bean
    protected ServletContextListener listener() {
            return new ServletContextListener() {
                    @Override
                    public void contextInitialized(ServletContextEvent sce) {
                            logger.info("ServletContext initialized");
                    }

                    @Override
                    public void contextDestroyed(ServletContextEvent sce) {
                            logger.info("ServletContext destroyed");
                    }
            };
    }

 // http://projects.spring.io/spring-boot/docs/docs/howto.html (Convert an Existing Application to Spring Boot)
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
            return application.sources(Application.class);
    }
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
