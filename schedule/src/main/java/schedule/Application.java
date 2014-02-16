package schedule;

import java.sql.Driver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application  extends SpringBootServletInitializer {
    
	@Bean
    public DataSource dataSource() {
		Driver driver = new org.h2.Driver();
		String url = "jdbc:h2:file:C:\\design_env\\github\\mustached-robot\\schedule\\data\\mustached-robot\\db";
		String username = "sa";
		String password = "";
		// works, why?
		return new SimpleDriverDataSource(driver, url, username, password);
        //return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan("schedule");
        return lef;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.H2);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
    
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
