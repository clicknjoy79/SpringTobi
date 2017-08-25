package springbook.learningtest.web.view;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ResourceBundleViewResolverTest extends AbstractDispatcherServletTest {
    @Test
    public void rbvr() throws ServletException, IOException {
        setClasses(RBVR.class, TempInternalResourceViewResolver.class, HelloController.class, MainController.class);
        runService("/hello");
        assertThat(response.getForwardedUrl(), is("/WEB-INF/view/hello1.jsp"));

        runService("/main");
        System.out.println(response.getForwardedUrl());
    }

    static class RBVR extends ResourceBundleViewResolver {
        {
            this.setBasename("web.views");
            setOrder(1);
        }
    }

    static class TempInternalResourceViewResolver extends InternalResourceViewResolver {
        {
            this.setPrefix("/WEB-INF/view/");
            this.setSuffix(".jsp");
        }
    }

    @RequestMapping("/hello")
    static class HelloController implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return new ModelAndView("hello");
        }
    }

    @RequestMapping("/main")
    static class MainController implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return new ModelAndView("main");
        }
    }

}
























