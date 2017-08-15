package springbook.learningtest.ioc.resource;

import org.springframework.stereotype.Component;

@Component("myprinter")
public class StringPrinter implements Printer {
    private StringBuffer buffer = new StringBuffer();

    @Override
    public void print(String message) {
        this.buffer.append(message);
    }

    public String toString() {
        return this.buffer.toString();
    }
}
