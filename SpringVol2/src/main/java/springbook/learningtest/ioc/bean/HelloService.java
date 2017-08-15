package springbook.learningtest.ioc.bean;

import org.springframework.context.annotation.Bean;

/**
 * @configuration 이 없는 설정 클래스의 @Bean 메소드가 호출되서 등록되면 일단 컨텍스트에 싱글톤으로 등록이 된다.
 * ==> hello, hello2, printer 가 싱글톤으로 등록된다.
 * 하지만 메소드 내부에서 printer() 처럼 호출되는 경우에는 다른 객체가 만들어 진다.
 * 일반적인 @configuration과 미묘하게 다르다.
 */
public class HelloService {
    @Bean
    public Hello hello() {
        Hello hello = new Hello();
        hello.setPrinter(printer());
        return hello;
    }

    @Bean
    public Hello hello2() {
        Hello hello = new Hello();
        hello.setPrinter(printer());
        return hello;
    }

    @Bean
    public Printer printer() {
        return new StringPrinter();
    }

}
