package springbook.learningtest.web.controllers;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.Controller;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.persistence.SecondaryTable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


public class InterceptorTest extends AbstractDispatcherServletTest{
    @Test
    public void preHandleReturnValue() throws ServletException, IOException {
        setClasses(InterceptorConfig.class, Controller1.class);
        runService("/hello").assertViewName("controller1.jsp");
        assertThat(getBean(Interceptor1.class).handler, is(getBean(Controller1.class)));

        getBean(Interceptor1.class).ret = false;
        assertThat(runService("/hello").getModelAndView(), is(nullValue()));
    }

    @Component("/hello")
    static class Controller1 implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return new ModelAndView("controller1.jsp");
        }
    }

    static class Interceptor1 extends HandlerInterceptorAdapter {
        Object handler;
        boolean ret = true;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            this.handler = handler;
            return ret;
        }
    }

    @Configuration
    static class InterceptorConfig {
        @Bean
        public HandlerMapping beanNameUrlHM() {
            BeanNameUrlHandlerMapping hm = new BeanNameUrlHandlerMapping();
            hm.setInterceptors(new Object[] {interceptor1()});
            return hm;
        }

        @Bean
        public HandlerInterceptor interceptor1() {
            return new Interceptor1();
        }
    }

    @Test
    public void postHandle() throws ServletException, IOException {
        setClasses(InterceptorConfig2.class, Controller1.class);
        runService("/hello")
                .assertViewName("modified viewname")
                .assertModel("message", "Hello Spring");
    }

    @Configuration
    static class InterceptorConfig2 {
        @Bean
        public HandlerMapping handlerMapping() {
            return new BeanNameUrlHandlerMapping() {
                {this.setInterceptors(new Object[] {interceptor2()});}
            };
        }

        @Bean
        public HandlerInterceptor interceptor2() {
            return new Interceptor2();
        }
    }

    static class Interceptor2 extends HandlerInterceptorAdapter {
        boolean ret = true;

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            modelAndView.setViewName("modified viewname");
            modelAndView.addObject("message", "Hello Spring");
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            return ret;
        }
    }

}

































