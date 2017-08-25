package springbook.learningtest.web.view;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonViewTest extends AbstractDispatcherServletTest {
    @Test
    public void jsonView() throws ServletException, IOException {
        setClasses(HelloController.class);
        initRequest("/hello").addParameter("name", "Spring");
        runService();
        assertThat(getContentAsString(), is("{\"message\":\"Hello Spring\"}"));

    }

    @RequestMapping("/hello")
    static class HelloController implements Controller {
        MappingJackson2JsonView jackson2JsonView = new MappingJackson2JsonView();

        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            Map<String, Object> model = new HashMap<>();
            model.put("message", "Hello " + request.getParameter("name"));

            return new ModelAndView(jackson2JsonView, model);
        }
    }


}
