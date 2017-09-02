package springbook.learningtest.aop;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ConfigurableTest {
    @Test
    @Ignore
    public void configurable() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(AppConfig.class, Service.class);
        ac.refresh();

        for(String name : ac.getBeanDefinitionNames()) {
            System.out.println(name + "\t" + ac.getBean(name).getClass().getSimpleName());
        }
        Client c = new Client();
        assertThat(c.service, is(notNullValue()));
    }

    @Configuration
    @EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
    @EnableSpringConfigured
    public static class AppConfig {
    }

    @Configurable
    public static class Client {
        @Autowired
        private Service service;
    }

    public static class Service {
    }
}
