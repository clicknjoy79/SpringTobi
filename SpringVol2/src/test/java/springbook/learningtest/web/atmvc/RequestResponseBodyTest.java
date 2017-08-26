package springbook.learningtest.web.atmvc;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RequestResponseBodyTest extends AbstractDispatcherServletTest{
    @Test
    public void requestBody() throws ServletException, IOException {
        setClasses(Ctrl1.class);
        setRelativeLocations("web/requestbody.xml");
        initRequest("/hello.do", "POST");
        request.addHeader("content-type", "text/html");
        request.setContent("name=Spring".getBytes());
        runService();
        assertThat(response.getStatus(), is(200));

        initRequest("/hello2.do", "POST");
        request.setContentType("text/plain;charset=UTF-8");
        request.setContent("이름=홍길동".getBytes());
        runService();
        assertThat(response.getStatus(), is(200));

        initRequest("/hello3", "POST");
        // JSON 은 기본적으로 UTF-8로 인코딩 된다.
        request.setContentType("application/json");
        request.setContent("{\"id\":1,\"name\":\"홍길동\"}".getBytes());
        runService();
        assertThat(response.getStatus(), is(200));

    }

    @Controller static class Ctrl1 {
        @RequestMapping("/hello")
        public void form(@RequestBody String body) {
            assertThat(body, is("name=Spring"));
        }
        @RequestMapping("/hello2")
        public void form2(@RequestBody String body) {
            assertThat(body, is("이름=홍길동"));
        }
        @RequestMapping("/hello3")
        public String form3(@RequestBody User user) {
            assertThat(user.getId(), is(1));
            assertThat(user.getName(), is("홍길동"));
            return "next";
        }
    }

    static class User {
        int id;
        String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public User() {}

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
    }

}
