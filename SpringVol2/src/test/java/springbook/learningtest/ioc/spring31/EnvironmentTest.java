package springbook.learningtest.ioc.spring31;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnvironmentTest {
    @Test
    public void defaultProfile() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(SimpleEnvConfig.class);
        assertThat(ac.getEnvironment().getDefaultProfiles().length, is(1));
        assertThat(ac.getEnvironment().getDefaultProfiles()[0], is("default"));
    }

    @Configuration
    static class SimpleEnvConfig {}

    static List<String> applicationBeans(GenericApplicationContext ac) {
        List<String> beans = new ArrayList<>();
        for (String name : ac.getBeanDefinitionNames()) {
            if(ac.getBeanDefinition(name).getRole() == BeanDefinition.ROLE_APPLICATION &&
                    name.indexOf("ImportAwareBeanPostProcessor") < 0) {
                beans.add(name);
            }
        }
        return beans;
    }

    @Test
    public void activeProfiles() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(ActiveProfileConfig.class);
        ac.refresh();
        assertThat(applicationBeans(ac).size(), is(1));
        assertThat(applicationBeans(ac).contains("environmentTest.ActiveProfileConfig"), is(true));

        ac = new AnnotationConfigApplicationContext();
        ac.getEnvironment().setActiveProfiles("p1");
        ac.register(ActiveProfileConfig.class);
        ac.refresh();
        assertThat(applicationBeans(ac).size(), is(3));

        ac = new AnnotationConfigApplicationContext();
        ac.getEnvironment().setActiveProfiles("p2");
        ac.register(ActiveProfileConfig.class);
        ac.refresh();
        assertThat(applicationBeans(ac).size(), is(2));
        assertThat(applicationBeans(ac).contains("environmentTest.ActiveProfileConfig"), is(true));
        assertThat(applicationBeans(ac).contains("springbook.learningtest.ioc.spring31.EnvironmentTest$ProfileConfig"), is(true));
        assertThat(applicationBeans(ac).contains("springbook.learningtest.ioc.spring31.EnvironmentTest$ProfileBean"), is(false));
    }

    @Configuration
    @Import({ProfileBean.class, ProfileConfig.class})
    static class ActiveProfileConfig {}

    @Component
    @Profile("p1")
    static class ProfileBean {}

    @Configuration
    @Profile({"p1", "p2"})
    static class ProfileConfig {}

    @Test
    public void propertySourcePlaceholderConfigurer() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(SimplePropertySource.class);
        SimplePropertySource sps = ac.getBean(SimplePropertySource.class);
        assertThat(sps.name, is("Toby"));

        Environment env = ac.getBean(Environment.class);
        assertThat(env.getProperty("name"), is("Toby"));
    }

    @Configuration
    @PropertySource("ioc/settings.properties")
    static class SimplePropertySource {
        @Autowired Environment env;
        @Value("${name}") String name;

        @Bean
        static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Test
    public void embeddedDb() {
        ApplicationContext ac = new GenericXmlApplicationContext("ioc/edb.xml");
        ac.getBean(DataSource.class);
    }

    @Test
    public void profiles() throws IllegalStateException, NamingException {
        // dev profile
        GenericXmlApplicationContext ac2 = new GenericXmlApplicationContext();
        ac2.getEnvironment().setActiveProfiles("dev");
        ac2.load("ioc/profile.xml");
        ac2.refresh();
        assertThat(ac2.getBean(DataSource.class), is(instanceOf(BasicDataSource.class)));
        ac2.close();

        // production profile
        SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        DriverManagerDataSource ds = new DriverManagerDataSource();
        builder.bind("jdbc/DefaultDS", ds);
        builder.activate();

        GenericXmlApplicationContext ac3 = new GenericXmlApplicationContext();
        ac3.getEnvironment().setActiveProfiles("production");
        ac3.load("ioc/profile.xml");
        ac3.refresh();
        assertThat(ac3.getBean(DataSource.class), is(instanceOf(DriverManagerDataSource.class)));
        ac3.close();
    }

    @Test
    public void activeProfile() {
        // system property
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "dev");

        GenericXmlApplicationContext ac2 = new GenericXmlApplicationContext();
        ac2.load("ioc/profile.xml");
        ac2.refresh();
        assertThat(ac2.getBean(DataSource.class), is(instanceOf(BasicDataSource.class)));
        ac2.close();
    }
}

































