package springbook.learningtest.ioc.config;

import javax.annotation.PostConstruct;

public class Hello {
    @PostConstruct
    public void init() {
        System.out.println("Init");
    }

    public void sayHello() {
        System.out.println("Hello!!!@@@");
    }
}
