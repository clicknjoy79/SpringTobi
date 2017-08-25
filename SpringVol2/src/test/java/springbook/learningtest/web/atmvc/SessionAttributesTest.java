package springbook.learningtest.web.atmvc;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SessionAttributesTest extends AbstractDispatcherServletTest {
    @Test
    public void sessionAttr() throws ServletException, IOException {
        setClasses(UserController.class);
        initRequest("/user/edit").addParameter("id", "1");
        runService();

        HttpSession session = request.getSession();
        assertThat(session.getAttribute("user"), is(getModelAndView().getModel().get("user")));

        initRequest("/user/edit", RequestMethod.POST)
                .addParameter("id", "2")
                .addParameter("name", "Spring2");
        request.setSession(session);
        runService();
        assertThat(((User)getModelAndView().getModel().get("user")).getEmail(), is("mail@spring.com"));
        assertThat(((User)getModelAndView().getModel().get("user")).getId(), is(2));
        assertThat(((User)getModelAndView().getModel().get("user")).getName(), is("Spring2"));

        assertThat(session.getAttribute("user"), is(nullValue()));
    }

    @Controller
    @SessionAttributes("user")
    static class UserController {
        @RequestMapping(value = "/user/edit", method = RequestMethod.GET)
        User form(@RequestParam int id) { return new User(id, "Spring", "mail@spring.com");}

        @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
        void submit(@ModelAttribute User user, SessionStatus sessionStatus) {
            sessionStatus.setComplete();
        }
    }

    static class User {
        int id; String name; String email;
        User(int id, String name, String email) {
            this.id = id; this.name = name; this.email = email;
        }
        User() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}











































