package springbook.learningtest.ioc.spring31;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import springbook.learningtest.ioc.propertysource.SimpleDS;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertySourceTest {
    @Test
    public void standardEnvironment() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.refresh();
        // 기본적으로 시스템 프로퍼티 소스와 환경변수 프로퍼티 소스가 들어있다.
        for(PropertySource<?> ps : ac.getEnvironment().getPropertySources()) {
            System.out.println(ps.getName());
        }

        System.out.println(ac.getEnvironment().getProperty("os.name"));
        System.out.println(ac.getEnvironment().getProperty("Path"));
    }

    @Test
    public void addPropertySource() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        Properties p = new Properties();
        p.put("db.username", "홍길동");
        PropertySource<?> ps = new PropertiesPropertySource("customPropertySource", p);

        ac.getEnvironment().getPropertySources().addFirst(ps);

        assertThat(ac.getEnvironment().getProperty("db.username"), is("홍길동"));
    }

    @Configuration
    static class AppConfig {}

    @Test
    public void pspc() {    // PropertySourcePlaceholderConfigurer 테스트
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(AppConfig2.class);

        Properties p = new Properties();
        p.put("db.username", "spring");
        PropertySource<?> ps = new PropertiesPropertySource("customPropertySource", p);

        ac.getEnvironment().getPropertySources().addFirst(ps);
        ac.refresh();

        assertThat(ac.getBean(AppConfig2.class).username, is("spring"));
        assertThat(ac.getBean(SimpleDS.class).getUsername(), is("spring"));

        assertThat(ac.getBean(AppConfig2.class).password, is("book"));
    }

    @Configuration
    @ImportResource("ioc/appconfig2.xml")
    @org.springframework.context.annotation.PropertySource(name="DB Settings", value = {"ioc/appconfig2.properties"})
    static class AppConfig2 {
        @Value("${db.username}") String username;
        @Value("${db.password}") String password;

        @Bean
        static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
}























