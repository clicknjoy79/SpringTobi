package springbook.learningtest.web.atmvc;

import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HandlerMethodArgumentResolverTest extends AbstractDispatcherServletTest {
    @Test
    public void hmarTest() throws ServletException, IOException {
        setClasses(WebConfig.class, UserController.class);
        initRequest("/user/update");
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", new User(1, "홍길동"));
        request.setSession(session);
        runService();
        assertThat(((User)getModelAndView().getModel().get("user")).getId(), is(1));
        assertThat(((User)getModelAndView().getModel().get("user")).getName(), is("홍길동"));

        initRequest("/user/add").addParameter("id", "2").addParameter("name", "유재석");
        runService();
        assertThat(((User)getModelAndView().getModel().get("user")).getId(), is(2));
        assertThat(((User)getModelAndView().getModel().get("user")).getName(), is("유재석"));

    }


    @Configuration
    @EnableWebMvc
    static class WebConfig extends WebMvcConfigurerAdapter{
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(new CustomArgumentResolver());
        }
    }

    static class CustomArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return (User.class.isAssignableFrom(parameter.getParameterType()) &&
                    parameter.getParameterAnnotation(Login.class) != null);
        }

        @Override
        public User resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            HttpSession session = request.getSession();
            User user = (User)session.getAttribute("loginUser");
            return user;

        }
    }

    @Controller
    static class UserController {
        @RequestMapping("/user/update")
        public String update(@Login User user, Model model) {
            model.addAttribute(user);
            return "next";
        }

        @RequestMapping("/user/add")
        public String add(User user) {
            return "next";
        }
    }

    static class User {
        int id;
        String name;
        User(int id, String name) { this.id = id; this.name = name; }
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
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Login {}


}




















