package springbook.learningtest.web.atmvc;

import groovy.transform.ToString;
import org.junit.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.NestedServletException;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AnnotationMethodTest extends AbstractDispatcherServletTest{
    @Test
    public void simple() throws ServletException, IOException {
        setClasses(ViewResolver.class, SimpleController.class);
        runService("/hello").assertViewName("hello");
        initRequest("/complex").addParameter("name", "Spring");
        request.setCookies(new Cookie("auth", "ABCD"));
        runService();
        assertViewName("myview");
        assertModel("info", "Spring/ABCD");
    }

    @RequestMapping
    static class SimpleController {
        @RequestMapping("/hello") void hello() {}
        @RequestMapping("/complex")
        String complex(@RequestParam("name") String name,
                       @CookieValue(value = "auth", required = false, defaultValue = "NONE") String auth,
                       ModelMap map) {
            map.put("info", name + "/" + auth);
            return "myview";
        }
    }

    static class ViewResolver extends InternalResourceViewResolver {{ setSuffix(".jsp");}}

    @Test
    public void pathVar() throws ServletException, IOException {
        setClasses(PathController.class);
        runService("/hello/toby/view/1").assertViewName("toby/1");
        runService("/hello/toby/view/badtype");
        assertThat(response.getStatus(), is(400));
    }

    @RequestMapping static class PathController {
        @RequestMapping("/hello/{user}/view/{id}")
        String pathVar(@PathVariable("user") String user, @PathVariable("id") int id) {
            return user + "/" + id;
        }
    }

    @Test
    public void requestParamAndModelAttribute() throws ServletException, IOException {
        setClasses(RequestParamController.class);
        initRequest("/hello").addParameter("id", "10").runService().assertViewName("10");
        initRequest("/hello2").addParameter("id", "11").runService().assertViewName("11");
        initRequest("/hello3").addParameter("id", "12")
                .addParameter("name", "Spring")
                .runService().assertViewName("12/Spring");
        initRequest("/hello").runService();
        assertThat(response.getStatus(), is(400));
        runService("/hello2").assertViewName("-1");
        initRequest("/hello4").addParameter("id", "15").runService().assertViewName("15");

        initRequest("/hello5").addParameter("id", "1")
                .addParameter("name", "Spring");
        runService().assertViewName("1/Spring");
        assertThat(getModelAndView().getModel().get("user"), is(notNullValue()));

        initRequest("/hello").addParameter("id", "bad").runService();
        assertThat(response.getStatus(), is(400));

        initRequest("/hello5").addParameter("id", "bad")
                .addParameter("name", "Spring");
        runService();
        assertThat(response.getStatus(), is(400));
        assertThat(getModelAndView(), is(nullValue()));

        initRequest("/hello6").addParameter("id", "bad")
                .addParameter("name", "Spring").runService();
        assertThat(response.getStatus(), is(200));
        assertThat(getModelAndView().getViewName(), is("id"));
    }

    @RequestMapping static class RequestParamController {
        @RequestMapping("/hello")
        String hello(@RequestParam("id") int id) { return "" + id; }
        @RequestMapping("/hello2")
        String hello2(@RequestParam(required = false, defaultValue = "-1") int id) {
            return "" + id;
        }
        @RequestMapping("/hello3")
        String hello3(@RequestParam Map<String, String> params) {
            return params.get("id") + "/" + params.get("name");
        }
        @RequestMapping("/hello4")
        String hello4(int id) { return "" + id; }
        @RequestMapping("/hello5")
        String hello5(@ModelAttribute("user") User user) {
            return user.getId() + "/" + user.getName();
        }
        @RequestMapping("/hello6")
        String hello6(@ModelAttribute("user") User user, Errors errors) {
            return errors.getFieldErrors().get(0).getField();
        }
    }

    static class User {
        int id; String name;
        User() {}
        User(int id, String name) { this.id = id; this.name = name; }

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

    @Test
    public void modelmap() throws ServletException, IOException {
        setClasses(ModelController.class);
        assertThat(runService("/hello.do").getModelAndView().getModel().get("user"), is(notNullValue()));
        assertThat(getModelAndView().getModel().get("us"), is(notNullValue()));
        assertThat(getModelAndView().getModel().get("string"), is("Spring"));
    }

    @RequestMapping static class ModelController {
        @RequestMapping("/hello") void hello(ModelMap model) {
            User u = new User(1, "Spring");
            model.addAttribute(u);
            model.addAttribute("us", u);
            model.addAttribute("Spring");   // String 타입 이름(string)으로 "Spring" 객체가 저장
        }
    }

    @Test
    public void autoAddedModel() throws ServletException, IOException {
        setClasses(ReturnController.class);

        initRequest("/hello1").addParameter("id", "1")
                .addParameter("name", "Spring").runService();
        assertViewName("hello1.jsp");
        for(String name : getModelAndView().getModel().keySet()) {
            System.out.println(name + ":" + getModelAndView().getModel().get(name).getClass());
        }

        assertModel("msg", "hi");
        assertModel("string", "string");
        assertModel("ref", "data");
        assertThat(getModelAndView().getModel().size(), is(5));

        assertThat(((User)getModelAndView().getModel().get("user")).getId(), is(1));
        assertThat(((User)getModelAndView().getModel().get("user")).getName(), is("Spring"));

        runService("/hello2.do").assertModel("name", "spring");
        runService("/hello3.do").assertModel("name", "spring");
        runService("/hello4.do").assertModel("name", "spring");

        runService("/hello5.do");
        assertThat(((Map<String, String>)getModelAndView().getModel().get("map")).get("msg"), is("Hello Spring"));
    }

    @Controller
    static class ReturnController {
        @ModelAttribute("ref") String ref() { return "data"; }

        @RequestMapping("/hello1")
        ModelAndView hello1(User u, Model model) {
            model.addAttribute("string");
            return new ModelAndView("hello1.jsp").addObject("msg", "hi");
        }

        @RequestMapping("/hello2")
        Model hello2() { return new ExtendedModelMap().addAttribute("name", "spring"); }
        @RequestMapping("/hello3")
        ModelMap hello3() {
            return new ModelMap().addAttribute("name", "spring");
        }
        @RequestMapping("/hello4")
        Map hello4() {
            Map map = new HashMap();
            map.put("name", "spring");
            return map;
        }

        @RequestMapping("/hello5")
        void hello5(ModelMap modelMap) {
            modelMap.addAttribute("map", new HashMap<String, String>() {{put("msg", "Hello Spring");}});
        }
    }

    @Test
    public void value() throws ServletException, IOException {
        setClasses(ValueController.class);
        runService("/hello.do").assertViewName(System.getProperty("os.name"));
    }

    @Controller static class ValueController {
        @RequestMapping("/hello")
        String hello(@Value("#{systemProperties['os.name']}") String osname) {
            return osname;
        }
    }



}






































