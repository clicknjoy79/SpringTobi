package springbook.learningtest.web.spring_el;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.Convert;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.util.*;

@Controller
@SessionAttributes("user")
public class UserController {
    @Autowired Validator validator;
    @Autowired ConversionService conversionService;

    @RequestMapping(value = "/hello1", method = RequestMethod.GET)
    public String hello(Model model) {
        model.addAttribute(new User(1, "홍길동", new String[] { "당구, 축구" }));
        return "hello1";
    }

    @RequestMapping(value = "/hello1", method = RequestMethod.POST)
    public String hello1(@Valid User user, BindingResult result, SessionStatus status) {
        System.out.println(user);
        System.out.println(result);

        if(result.hasErrors()) {
            return "hello1";
        } else {
            status.setComplete();
            return "index";
        }
    }

    @RequestMapping(value = "/hello2", method = RequestMethod.GET)
    public String hello2(Model model) {
        User user = new User();
        user.setId(1);
        user.setName("예를 들어 홍길동");
        user.setInterests(new String[] { "A", "C" });
        user.setUserType(new Type(3, "손님"));
        user.setUserLocal(new Local(3, "전남"));
        model.addAttribute(user);
        return "hello2";
    }

    @RequestMapping(value = "/hello2", method = RequestMethod.POST)
    public String hello22(@Valid User user, BindingResult result, SessionStatus status) {
        System.out.println(user);
        System.out.println(result);
        return "hello2";
    }

    @ModelAttribute("interests")
    public Map<String, String> interests() {
        Map<String, String> interests = new HashMap<>();
        interests.put("A", "Java");
        interests.put("B", "C#");
        interests.put("C", "Ruby");
        interests.put("D", "Python");
        return interests;
    }

    @ModelAttribute("types")
    public List<Type> types() {
        List<Type> list = new ArrayList<>();
        list.add(new Type(1, "관리자"));
        list.add(new Type(2, "회원"));
        list.add(new Type(3, "손님"));
        return list;
    }

    @ModelAttribute("locals")
    public List<Local> locals() {
        List<Local> list = new ArrayList<>();
        list.add(new Local(1, "서울"));
        list.add(new Local(2, "전북"));
        list.add(new Local(3, "전남"));
        return list;
    }

    static class StringToTypeConverter implements Converter<String, Type> {
        TypeService typeService = new TypeService();

        @Override
        public Type convert(String source) {
            return typeService.getType(source);
        }
    }

    static class TypetoStringConverter implements Converter<Type, String> {
        @Override
        public String convert(Type source) {
            return String.valueOf(source.getId());
        }
    }

    static class StringToLocalConverter implements Converter<String, Local> {
        LocalService localService = new LocalService();

        @Override
        public Local convert(String source) {
            return localService.getLocal(source);
        }
    }

    static class LocalToStringConverter implements Converter<Local, String> {
        @Override
        public String convert(Local source) {
            return String.valueOf(source.getId());
        }
    }

    static class TypeService {
        Type getType(String id) {
            switch (id) {
                case "1" : return new Type(1, "관리자");
                case "2" : return new Type(2, "회원");
                case "3" : return new Type(3, "손님");
                default: throw new IllegalArgumentException("unkown id");
            }
        }
    }

    static class LocalService {
        Local getLocal(String id) {
            switch (id) {
                case "1" : return new Local(1, "서울");
                case "2" : return new Local(2, "전북");
                case "3" : return new Local(3, "전남");
                default: throw new IllegalArgumentException("unkown id");
            }
        }
    }

    @Component("myConversionService")
    static class MyConversionService extends FormattingConversionServiceFactoryBean {{
        setConverters(new LinkedHashSet<>(Arrays.asList(
                new Converter[] { new LocalToStringConverter(), new StringToLocalConverter(),
                new TypetoStringConverter(), new StringToTypeConverter()}
        )));
    }}

    static class Type {
        int id;
        String name;

        Type(int id, String name) {
            this.id = id;
            this.name = name;
        }
        Type() {}

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
            return "Type[id = " + id + ", name = " + name + "]";
        }

//        @Override
//        public int hashCode() {
//            final int prime = 31;
//            int result = 1;
//            result = prime * result
//                    + ((id == 0) ? 0 : id);
//            result = prime * result
//                    + ((name == null) ? 0 : name.hashCode());
//            result = prime * result + ((id == 0) ? 0 : id);
//            return result;
//        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Type other = (Type) obj;
            if(getId() == other.getId())
                return true;
            else
                return false;
        }
    }
    static class Local{
        int id;
        String name;
        Local(int id, String name) {
            this.id = id;
            this.name = name;
        }
        Local() {}

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
            return "Local[id = " + id + ", name = " + name + "]";
        }

//        @Override
//        public int hashCode() {
//            final int prime = 31;
//            int result = 1;
//            result = prime * result
//                    + ((id == 0) ? 0 : id);
//            result = prime * result
//                    + ((name == null) ? 0 : name.hashCode());
//            result = prime * result + ((id == 0) ? 0 : id);
//            return result;
//        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Local other = (Local) obj;

            if(getId() == other.getId())
                return true;
            else
                return false;
        }
    }
    @InitBinder void initBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(this.validator);
    }
}
