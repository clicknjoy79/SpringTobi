package springbook.learningtest.web.hello;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleHelloControllerTest extends AbstractDispatcherServletTest {
    @Test
    public void helloController() throws ServletException, IOException {
        ModelAndView mv = setRelativeLocations("/web/spring-servlet.xml")
                .setClasses(HelloSpring.class)
                .initRequest("/hello", RequestMethod.GET).addParameter("name", "Spring")
                .runService()
                .getModelAndView();

        assertThat(mv.getViewName(), is("/WEB-INF/view/hello.jsp"));
        assertThat(mv.getModel().get("message"), is("Hello Spring"));
    }

    @Test
    public void helloControllerWithAssertMethods() throws ServletException, IOException {
        this.setRelativeLocations("/web/spring-servlet.xml")
                .setClasses(HelloSpring.class)
                .initRequest("/hello", RequestMethod.GET).addParameter("name", "Spring")
                .runService()
                .assertModel("message", "Hello Spring")
                .assertViewName("/WEB-INF/view/hello.jsp");
    }

    @Test
    public void helloControllerWithServletPath() throws ServletException, IOException {
        this.setRelativeLocations("/web/spring-servlet.xml")
                .setClasses(HelloSpring.class)
                .setServletPath("/app")
                .initRequest("/app/hello", RequestMethod.GET).addParameter("name", "Spring")
                .runService()
                .assertModel("message", "Hello Spring")
                .assertViewName("/WEB-INF/view/hello.jsp");
    }
}




















































