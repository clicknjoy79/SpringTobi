package springbook.learningtest.web.view;

import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.JstlView;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContentNegotiatingViewResolverTest extends AbstractDispatcherServletTest {
    @Test
    public void ContentNegotiatingViewResolver() throws ServletException, IOException {
        setClasses(SampleContentAtomView.class, HelloController.class);
        setRelativeLocations("web/content_negotiating_view_resolver.xml");
        initRequest("/content.atom").addParameter("name", "AtomView!!!");
        runService();

        initRequest("/content.html").addParameter("name", "JstlView!!!");
        runService();

        initRequest("/content.json").addParameter("name", "JsonView!!!");
        runService();
        assertThat(getContentAsString().indexOf("{\"message\":\"Hello JsonView!!!\"}") >= 0, is(true));
    }

    @Component("content")
    static class SampleContentAtomView implements View {
        @Override
        public String getContentType() {
            return "application/atom+xml";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            System.out.println("SampleContentAtomView rendering.....");
            System.out.println(model.get("message"));
        }
    }

    static class MyJstlView extends JstlView {
        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            System.out.println("MyJstlView rendering...");
            System.out.println(model.get("message"));

            super.render(model, request, response);
        }
    }

    @RequestMapping("/content")
    static class HelloController implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return new ModelAndView("content")
                    .addObject("message", "Hello " + request.getParameter("name"));
        }
    }
}
