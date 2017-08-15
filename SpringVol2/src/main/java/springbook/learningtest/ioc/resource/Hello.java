package springbook.learningtest.ioc.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class Hello {
    @Value("Spring")
    String name;

    @Resource
    Printer printer;        // 타입으로 와이어링(좋은 방법은 아님)

    public Hello() {}

    public Hello(String name, Printer printer) {
        this.name = name;
        this.printer = printer;
    }

    public String sayHello() {
        return "Hello " + name;
    }

    public void print() {
        this.printer.print(sayHello());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public String getName() {
        return name;
    }

    public Printer getPrinter() {
        return printer;
    }
}
