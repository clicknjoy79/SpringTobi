package springbook.learningtest.web.atmvc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GenericControllerTest extends AbstractDispatcherServletTest {
    @Test
    public void gencon() throws ServletException, IOException {
        setClasses(UserController.class, MemberController.class, UserService.class, MemberService.class);
        runService("/user/add").assertViewName("user/add");
        runService("/user/update").assertViewName("user/update");
        runService("/user/view").assertViewName("user/view");
        runService("/user/delete").assertViewName("user/delete");
        runService("/user/list").assertViewName("user/list");

        runService("/member/add").assertViewName("member/add");
        runService("/member/update").assertViewName("member/update");
        runService("/member/view").assertViewName("member/view");
        runService("/member/delete").assertViewName("member/delete");
        runService("/member/list").assertViewName("member/list");

        assertThat(getBean(UserController.class).service, is(instanceOf(UserService.class)));
        assertThat(getBean(MemberController.class).service, is(instanceOf(MemberService.class)));
    }

    abstract static class GenericController<T, P, S> {
        @Autowired S service;
        @RequestMapping("/add") void add(T entity){}
        @RequestMapping("/update") void update(T entity) {}
        @RequestMapping("/view") T view(P id) { return null; }
        @RequestMapping("/delete") void delete(P id) {}
        @RequestMapping("/list") List<T> list() { return null; }
    }

    @RequestMapping("/user")
    static class UserController extends GenericController<User, Integer, UserService> {}
    static class User {int id; String name;}
    static class UserService {}
    @RequestMapping("/member")
    static class MemberController extends GenericController<Member, Integer, MemberService> {}
    static class Member {int id; String name;}
    static class MemberService {}
}









































