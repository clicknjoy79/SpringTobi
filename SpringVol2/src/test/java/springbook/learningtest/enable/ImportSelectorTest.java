package springbook.learningtest.enable;

import org.junit.Test;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ImportSelectorTest {
    @Test
    public void simple() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        Hello h = ac.getBean(Hello.class);
        h.sayHello();
        assertThat(h.getHello(), is("Hello Spring2"));
    }

    @Configuration
    @EnableHello(mode = "mode2")
    static class AppConfig {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(HelloSelector.class)
    @interface EnableHello {
        String mode();
    }

    static class HelloSelector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            String mode = (String) importingClassMetadata.getAnnotationAttributes(EnableHello.class.getName()).get("mode");

            if("mode1".equals(mode)) {
                return new String[] { HelloConfig1.class.getName() };
            } else {
                return new String[] { HelloConfig2.class.getName() };
            }

        }
    }

    @Configuration
    static class HelloConfig1 {
        @Bean Hello hello() {
            Hello h = new Hello();
            h.setName("Spring1");
            return h;
        }
    }

    @Configuration
    static class HelloConfig2 {
        @Bean Hello hello() {
            Hello h = new Hello();
            h.setName("Spring2");
            return h;
        }
    }
}
















































