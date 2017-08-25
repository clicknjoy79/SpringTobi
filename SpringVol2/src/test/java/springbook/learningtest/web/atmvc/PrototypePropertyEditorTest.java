package springbook.learningtest.web.atmvc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;
import java.beans.PropertyEditorSupport;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PrototypePropertyEditorTest extends AbstractDispatcherServletTest {
    @Test
    public void fakeCodePropertyEditor() throws ServletException, IOException {
        setClasses(UserController.class);
        initRequest("/add.do").addParameter("id", "1")
                .addParameter("name", "Spring")
                .addParameter("userType", "2");
        runService();
        User user = (User)getModelAndView().getModel().get("user");
        assertThat(user.getUserType().getId(), is(2));
        try {
             user.getUserType().getName();
        } catch (UnsupportedOperationException e) {}
    }

    @Controller
    static class UserController {
        @InitBinder
        public void initBinder(WebDataBinder dataBinder) {
            dataBinder.registerCustomEditor(Code.class, new FakeCodePropertyEditor());
        }

        @RequestMapping("/add")
        public void add(@ModelAttribute User user) {
            System.out.println(user);
        }
    }

    static class FakeCodePropertyEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            Code code = new FakeCode();
            code.setId(Integer.parseInt(text));
            setValue(code);
        }

        @Override
        public String getAsText() {
            return String.valueOf(((Code)getValue()).getId());
        }
    }

    static class User {
        int id; String name; Code userType;

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

        public Code getUserType() {
            return userType;
        }

        public void setUserType(Code userType) {
            this.userType = userType;
        }

        @Override
        public String toString() {
            return "User [id=" + id + ", name=" + name + ", userType=" +
                    userType + "]";
        }
    }

    static class Code {
        int id;
        String name;

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

        @Override
        public String toString() {
            return "Code [id=" + id + ", name=" + name + "]";
        }
    }

    static class FakeCode extends Code {
        public String getName(){
            throw new UnsupportedOperationException();
        }
        public void setName(String name) {
            throw new UnsupportedOperationException();
        }

    }

    @Test
    public void prototypePropertyEditor() throws ServletException, IOException {
        setClasses(UserController2.class, CodePropertyEditor.class, CodeService.class);
        initRequest("/add.do").addParameter("id", "1")
                .addParameter("name", "Spring")
                .addParameter("userType", "3");
        runService();
        User user = (User)getModelAndView().getModel().get("user");
        assertThat(user.getUserType().getName(), is("Code3"));
    }

    @Controller
    static class UserController2 {
        @Inject
        Provider<CodePropertyEditor> editorProvider;

        @InitBinder
        public void initBinder(WebDataBinder dataBinder) {
            dataBinder.registerCustomEditor(Code.class, editorProvider.get());
        }

        @RequestMapping("/add")
        public void add(@ModelAttribute User user) {
            System.out.println(user);
        }
    }

    @Scope("prototype")
    static class CodePropertyEditor extends PropertyEditorSupport {
        @Autowired
        CodeService codeService;

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            Code code = codeService.getCode(Integer.parseInt(text));
            setValue(code);
        }

        @Override
        public String getAsText() {
            return String.valueOf(((Code)getValue()).getId());
        }
    }

    static class CodeService {
        public Code getCode(int id) {
            Code c = new Code();
            c.setId(id);
            c.setName("Code" + id);
            return c;
        }
    }



}








































