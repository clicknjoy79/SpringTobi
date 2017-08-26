package springbook.learningtest.web.view;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestToViewNameTranslatorTest extends AbstractDispatcherServletTest {
    @Test
    public void defaultRequestToViewNameTranslator() throws ServletException, IOException {
        setClasses(Config.class);
        runService("/hello").assertViewName("hello.jsp");
        runService("/hello/world").assertViewName("hello/world.jsp");
        runService("/hi").assertViewName("hi.jsp");
    }

    @Configuration
    static class Config {
        @Bean
        HandlerMapping handlerMapping() {
            return new BeanNameUrlHandlerMapping() {
                {
                    this.setDefaultHandler(defaultHandler());
                }
            };
        }

        @Bean
        RequestToViewNameTranslator viewNameTranslator() {
            return new DefaultRequestToViewNameTranslator(){
                {
                    this.setSuffix(".jsp");
                }
            };
        }

        @Bean
        Controller defaultHandler() {
            return new Controller() {
                @Override
                public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    return new ModelAndView();
                }
            };
        }

    }


}
