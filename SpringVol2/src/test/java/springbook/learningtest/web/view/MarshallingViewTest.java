package springbook.learningtest.web.view;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.xml.MarshallingView;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MarshallingViewTest extends AbstractDispatcherServletTest {
    @Test
    public void marshallingView() throws ServletException, IOException {
        setRelativeLocations("web/marshallingview.xml");
        initRequest("/hello").addParameter("name", "Spring");
        runService();
        assertThat(getContentAsString().indexOf("<info><message>Hello Spring</message></info>") >= 0, is(true));
    }

    @RequestMapping("/hello")
    static class HelloController implements Controller {
        @Resource
        MarshallingView helloMarshallingView;

        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            Map<String, Object> model = new HashMap<>();
            model.put("info", new Info("Hello " + request.getParameter("name")));
            return new ModelAndView(helloMarshallingView, model);
        }
    }
}
