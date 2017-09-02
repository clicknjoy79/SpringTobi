package springbook.learningtest.spring31.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RequestConditionTest extends AbstractDispatcherServletTest {
    @Test
    public void patternRC() throws ServletException, IOException {
        setClasses(AppConfig.class, PatternController.class, ParamsController.class,
                RequestMethodController.class, HeadersController.class, ConsumesController.class,
                Quiz4.class, Quiz5.class, Quiz6.class);

        runService("/a/c").assertViewName("hello.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        runService("/a/c.html");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
//        assertThat(response.getStatus(), is(HttpServletResponse.SC_NOT_FOUND));

        runService("/a/c/");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
//        assertThat(response.getStatus(), is(HttpServletResponse.SC_NOT_FOUND));

        runService("/a/d");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        runService("/b/c");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        runService("/b/d");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        runService("/").assertViewName("requestmethod.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/").addParameter("p1", "").runService()
                .assertViewName("requestmethod.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/")
                .addParameter("p1", "")
                .addParameter("p2", "")
                .runService()
                .assertViewName("requestmethod.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/")
                .addParameter("p1", "")
                .addParameter("p2", "")
                .addParameter("p3", "")
                .addParameter("p4", "").runService()
                .assertViewName("params.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/f");
        request.addHeader("h1", "");
        request.addHeader("h2", "");
        request.addHeader("h3", "");
        request.addHeader("h4", "");
        runService().assertViewName("headers.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/g");
        request.setContentType("c3/text");
        runService().assertViewName("consumes.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/g");
        request.setContentType("c4/text");
        runService().assertViewName("consumes.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

//        initRequest("/g");
//        request.addHeader("accept", "r1/text");
//        runService().assertViewName("produces.jsp");
//        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/");
        request.addHeader("a", "");
        request.setContentType("application/json");
        request.setContentType("multipart/form-data");
        request.addHeader("c", "");
        request.addHeader("d", "");
        runService().assertViewName("quiz4.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/");
        request.addHeader("a", "");
        request.addHeader("b", "");
        request.addHeader("c", "");
        request.addHeader("d", "");
        request.setContentType("application/json");
//        request.setContentType("multipart/form-data");
        runService().assertViewName("quiz5.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/");
        request.setContentType("application/xml");
        runService().assertViewName("requestmethod.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

        initRequest("/");
        request.setContentType("application/json");
        runService().assertViewName("quiz6.jsp");
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

    }

    @Configuration
    static class AppConfig extends WebMvcConfigurationSupport {
        @Autowired RequestMappingHandlerMapping rmhm;

        @Override
        public RequestMappingHandlerMapping requestMappingHandlerMapping() {
            RequestMappingHandlerMapping rmhm = new RequestMappingHandlerMapping();
//            rmhm.setUseSuffixPatternMatch(false);
//            rmhm.setUseTrailingSlashMatch(false);
            return rmhm;
        }
    }

    @Controller
    @RequestMapping({"/a", "/b"})
    static class PatternController {
        @RequestMapping({"/c", "/d"})
        public String hello() { return "hello.jsp"; }
    }

    @Controller
    @RequestMapping(params = {"p1", "p2"})
    static class ParamsController {
        @RequestMapping(params = {"p3", "p4"})
        public String hello2() { return "params.jsp"; }
    }

    @Controller
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    static class RequestMethodController {
        @RequestMapping(method = {RequestMethod.PUT, RequestMethod.DELETE})
        public String hello3() { return "requestmethod.jsp"; }
    }

    @Controller
    @RequestMapping(headers = {"h1", "h2"})     // AND 조합
    static class HeadersController {
        @RequestMapping(value = "/f", headers = {"h3", "h4"})
        public String hello4() { return "headers.jsp"; }
    }

    @Controller
    @RequestMapping
    static class ConsumesController {
        @RequestMapping(value = "/g", consumes = {"c3/text", "c4/text"})
        public String hello5() { return "consumes.jsp"; }
    }

//    @Controller
//    @RequestMapping(produces = {"r1/text", "r2/text"})
//    static class ProducesController {
//        @RequestMapping(value = "/g", produces = {"r3/text", "r4/text"})
//        public String hello6() { return "produces.jsp"; }
//    }

    @Controller
    @RequestMapping(headers = {"a", "Content-Type=application/json", "Content-Type=multipart/form-data"})
    public static class Quiz4 {
        @RequestMapping(headers = {"c", "d"})
        public String hello() { return "quiz4.jsp"; }
    }

    @Controller
    @RequestMapping(headers = {"a", "b"})
    public static class Quiz5 {
        @RequestMapping(headers = {"c", "d", "Content-Type=application/json"}, consumes = "multipart/form-data")
        public String hello() { return "quiz5.jsp"; }
    }

    @Controller
    @RequestMapping(consumes = {"application/xml", "application/x-www-form-urlencoded"})
    public static class Quiz6 {
        @RequestMapping(consumes = {"multipart/form-data", "application/json"})
        public String hello() { return "quiz6.jsp"; }
    }
}

















































































