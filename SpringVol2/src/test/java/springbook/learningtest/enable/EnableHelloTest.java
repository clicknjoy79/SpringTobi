package springbook.learningtest.enable;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnableHelloTest {
    @Test
    public void simple() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        Hello h = ac.getBean(Hello.class);
        h.sayHello();
        assertThat(h.getHello(), is("Hello Toby"));

    }

    @Configuration
    @EnableHello
    static class AppConfig {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(HelloConfig.class)
    @interface EnableHello {}

    @Configuration
    static class HelloConfig {
        @Bean Hello hello() {
            Hello h = new Hello();
            h.setName("Toby");
            return h;
        }
    }
}




















