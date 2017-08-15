package springbook.learningtest.ioc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleConfig {
    @Autowired
    public Hello hello;

    @Bean
    Hello hello() {
        return new Hello();
    }

}
