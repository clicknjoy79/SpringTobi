package springbook.learningtest.web.hello;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.servlet.ModelAndView;
import springbook.learningtest.web.ConfigurableDispatcherServlet;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HelloControllerTest  {
    @Test
    public void helloController() throws ServletException, IOException {
        ConfigurableDispatcherServlet servlet = new ConfigurableDispatcherServlet();
        servlet.setRelativeLocations(getClass(), "/web/spring-servlet.xml");
        servlet.setClasses(HelloSpring.class);
        servlet.init(new MockServletConfig("spring"));

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        req.addParameter("name", "Spring");
        MockHttpServletResponse res = new MockHttpServletResponse();

        servlet.service(req, res);

        ModelAndView mv = servlet.getModelAndView();
        assertThat(mv.getViewName(), is("/WEB-INF/view/hello.jsp"));
        assertThat(mv.getModel().get("message"), is("Hello Spring"));

    }
















}
