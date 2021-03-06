package springbook.learningtest.web.view;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RedirectViewTest extends AbstractDispatcherServletTest{
    @Test
    public void redirectView() throws ServletException, IOException {
        setClasses(HelloController.class);
        runService("/hello");
        assertThat(this.response.getRedirectedUrl(), is("/main?name=Spring"));
    }

    @RequestMapping("/hello")
    static class HelloController implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
//            return new ModelAndView(new RedirectView("/main", true)).addObject("name", "Spring");
            return new ModelAndView("redirect:/main").addObject("name", "Spring");

        }
    }
}
