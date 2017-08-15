package springbook.learningtest.ioc.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class HelloService2 {
    private Printer printer;

    @Autowired
    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    @Bean
    private Hello hello() {
        Hello hello = new Hello();
        hello.setPrinter(this.printer);
        return hello;
    }

    @Bean
    private Hello hello2() {
        Hello hello = new Hello();
        hello.setPrinter(this.printer);
        return hello;
    }

    @Bean
    private Printer printer() {
        return new StringPrinter();
    }
}
