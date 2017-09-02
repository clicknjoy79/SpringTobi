package springbook.learningtest.configurationbyjavacode;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@Configuration
@ComponentScan("springbook.services")
//@EnableTransactionManagement
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
@EnableSpringConfigured
public class AppConfig {
//    @Bean
//    PlatformTransactionManager transactionManager() {
//        return new DataSourceTransactionManager();
//    }
}
