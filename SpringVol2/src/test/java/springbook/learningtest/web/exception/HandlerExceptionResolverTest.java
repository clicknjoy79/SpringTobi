package springbook.learningtest.web.exception;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import java.io.IOException;

public class HandlerExceptionResolverTest extends AbstractDispatcherServletTest {
    @Test
    public void annotationMethod() throws ServletException, IOException {
        setClasses(HelloController.class);
        runService("/hello");
        assertViewName("dataException");
        assertModel("msg", "hi");
    }

    @RequestMapping
    static class HelloController {
        @RequestMapping("/hello")
        void hello() { throw new DataRetrievalFailureException("hi"); }

        @ExceptionHandler(DataAccessException.class)
        ModelAndView dataAccessExceptionHandler(DataAccessException ex) {
            return new ModelAndView("dataException")
                    .addObject("msg", ex.getMessage());
        }
    }

    @Test
    public void responseStatus() throws ServletException, IOException {
        setClasses(HelloController2.class);
        runService("/hello");
        System.out.println(response.getStatus());
        System.out.println(response.getErrorMessage());
    }

    @RequestMapping
    static class HelloController2 {
        @RequestMapping("/hello")
        void hello() { throw new NotInServiceException(); }

        @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "서비스 일시 중지")
        static class NotInServiceException extends RuntimeException {}
    }

}




















