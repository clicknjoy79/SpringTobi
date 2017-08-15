package springbook.learningtest.ioc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import springbook.learningtest.ioc.bean.BeanC;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ValueInjectionTest {
    @Test
    public void valueInjection() {
        ApplicationContext context = new AnnotationConfigApplicationContext(
                BeanSP.class, ConfigSP.class, DatabasePropertyPlaceHolder.class
        );

        BeanSP bean = context.getBean(BeanSP.class);
        assertThat(bean.name, is("Windows 7"));
        assertThat(bean.userName, is("Spring"));
        assertThat(bean.hello.name, is("Spring"));
        assertThat(bean.osName, is("Windows 7"));
    }

    static class BeanSP {
        @Value("#{systemProperties['os.name']}")
        String name;
        @Value("${database.username}")
        String userName;
        @Value("${os.name}")       // 프라퍼티 파일에 숨어있는 히든 프라퍼티 인듯 ???
        String osName;
        @Autowired
        Hello hello;
    }

    static class ConfigSP {
        @Bean
        public Hello hello(@Value("${database.username}") String username) {
            Hello hello = new Hello();
            hello.name = username;
            return hello;
        }
    }

    static class Hello { String name; }
    static class DatabasePropertyPlaceHolder extends PropertyPlaceholderConfigurer {
        public DatabasePropertyPlaceHolder() {
            this.setLocation(new ClassPathResource("ioc/database.properties"));
        }
    }

    @Test
    public void importResource() {
        ApplicationContext context = new AnnotationConfigApplicationContext(
                ConfigIR.class, ConfigSP.class
        );
        BeanSP bean = context.getBean(BeanSP.class);

        assertThat(bean.name, is("Windows 7"));
        assertThat(bean.userName, is("Spring"));
        assertThat(bean.hello.name, is("Spring"));
        assertThat(bean.osName, is("Windows 7"));
    }

    @ImportResource("ioc/properties2.xml")
    @Configuration
    static class ConfigIR {
        @Bean BeanSP beanSP() { return new BeanSP(); }
    }

    @Test
    public void propertyEditor() {
        ApplicationContext context = new AnnotationConfigApplicationContext(BeanPE.class);
        BeanPE bean = context.getBean(BeanPE.class);
        assertThat(bean.charset, is(Charset.forName("UTF-8")));
        assertThat(bean.intarr, is(new int[] {1, 2, 3}));
        assertThat(bean.flag, is(true));
        assertThat(bean.rate, is(1.2));
        assertThat(bean.file.exists(), is(true));
    }

    static class BeanPE {
        @Value("UTF-8") Charset charset;
        @Value("1, 2, 3") int[] intarr;
        @Value("true") boolean flag;
        @Value("1.2") double rate;
        @Value("classpath:ioc/test-applicationContext.xml") File file;
    }

    @Test
    public void collectionInjection() {
        ApplicationContext context = new GenericXmlApplicationContext(
                new ClassPathResource("ioc/collection.xml"));
        BeanC bean = context.getBean(BeanC.class);

        assertThat(bean.getNameList().size(), is(3));
        assertThat(bean.getNameList().get(0), is("Spring"));
        assertThat(bean.getNameList().get(1), is("IoC"));
        assertThat(bean.getNameList().get(2), is("DI"));

        assertThat(bean.getNameSet().size(), is(3));
        assertThat(bean.getAges().get("Kim"), is(30));
        assertThat(bean.getAges().get("Lee"), is(35));
        assertThat(bean.getAges().get("Ann"), is(40));

        assertThat(bean.getSettings().get("username"), is("Spring"));
        assertThat(bean.getSettings().get("password"), is("Book"));

        assertThat(bean.getBeans().size(), is(2));
    }

}































