package springbook.learningtest.ioc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.learningtest.ioc.resource.Hello;

import javax.inject.Inject;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnnotationConfigurationTest {
    private String beanBasePath = "annotation/";

    @Test
    public void atResource() {
        ApplicationContext context = new GenericXmlApplicationContext(
                beanBasePath + "resource.xml");
        Hello hello = context.getBean("hello", Hello.class);
        hello.print();

        assertThat(context.getBean("myprinter").toString(), is("Hello Spring"));

    }

    @Test
    public void atAutowiredCollection() {
        ApplicationContext context = new AnnotationConfigApplicationContext(
          Client.class, ServiceA.class, ServiceB.class
        );
        Client client = context.getBean(Client.class);
        assertThat(client.serviceSet.size(), is(2));
        assertThat(client.serviceArr.length, is(2));
        assertThat(client.serviceMap.size(), is(2));
        assertThat(client.serviceList.size(), is(2));
        assertThat(client.serviceCollection.size(), is(2));
    }

    static class Client {
        @Autowired Set<Service> serviceSet;
        @Autowired Service[] serviceArr;
        @Autowired Map<String, Service> serviceMap;
        @Autowired List<Service> serviceList;
        @Autowired Collection<Service> serviceCollection;
    }

    interface Service {}
    static class ServiceA implements Service {}
    static class ServiceB implements Service {}

    @Test
    public void atQualifier() {
        ApplicationContext context = new AnnotationConfigApplicationContext(
                QClient.class, QServiceA.class, QServiceB.class
        );
        QClient client = context.getBean(QClient.class);
        assertThat(client.service, is(instanceOf(QServiceA.class)));

    }

    static class QClient{
        @Autowired
        @Qualifier("main")
        Service service;
    }

    @Qualifier("main")
    static class QServiceA implements Service {}
    static class QServiceB implements Service {}

    @Test
    public void atInject() {
        ApplicationContext context = new AnnotationConfigApplicationContext(
                IClient.class, IServiceA.class, IServiceB.class
        );
        IClient client = context.getBean(IClient.class);
        assertThat(client.service, is(instanceOf(IServiceA.class)));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier("main")
    @interface Main {}

    static class IClient {
        @Inject
        @Main
        Service service;
    }

    @Main
    static class IServiceA implements Service {}
    static class IServiceB implements Service {}

}































