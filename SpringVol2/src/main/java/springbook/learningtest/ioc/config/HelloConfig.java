package springbook.learningtest.ioc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springbook.learningtest.ioc.bean.Hello;
import springbook.learningtest.ioc.bean.Printer;
import springbook.learningtest.ioc.bean.StringPrinter;

@Configuration
public class HelloConfig {
    @Bean
    public Hello hello() {
        Hello hello = new Hello();
        hello.setName("Spring1");
        hello.setPrinter(printer());
        return hello;
    }

    @Bean
    public Hello hello2() {
        Hello hello = new Hello();
        hello.setName("Spring2");
        hello.setPrinter(printer());
        return hello;
    }

    @Bean
    public Printer printer() {
        return new StringPrinter();
    }
}
