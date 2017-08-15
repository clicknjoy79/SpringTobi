package springbook.learningtest.ioc;

import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.learningtest.ioc.bean.BeanDefinitionUtils;
import springbook.learningtest.ioc.config.SimpleConfig;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class BeanRoleTest {
    @Test
    public void simpleConfig() {
        GenericApplicationContext ac = new GenericXmlApplicationContext("ioc/beanrole.xml");
        BeanDefinitionUtils.printBeanDefinitions(ac);

        SimpleConfig sc = ac.getBean(SimpleConfig.class);
        assertThat(sc.hello, is(notNullValue()));
        sc.hello.sayHello();
    }

}
