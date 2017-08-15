package springbook.learningtest.ioc;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.Assert;
import springbook.learningtest.ioc.bean.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ApplicationContextTest {
    @Test
    public void registerBean() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("hello1", Hello.class);

        Hello hello1 = ac.getBean("hello1", Hello.class);
        Assert.notNull(hello1, "must be not null");

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        ac.registerBeanDefinition("hello2", helloDef);

        Hello hello2 = ac.getBean("hello2", Hello.class);
        assertThat(hello1, is(not(hello2)));
        assertThat(ac.getBeanFactory().getBeanDefinitionCount(), is(2));
    }

    @Test
    public void registerBeanWithDependency() {
        StaticApplicationContext ac = new StaticApplicationContext();

        ac.registerBeanDefinition("printer",
                new RootBeanDefinition(StringPrinter.class));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        helloDef.getPropertyValues().addPropertyValue("printer",
                new RuntimeBeanReference("printer"));  // 아이디가 printer 인 빈에 대한 레퍼런스를 프로퍼티로 등록
        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString(), is("Hello Spring"));

    }

    @Test
    public void genericApplicationContext() {
        GenericApplicationContext ac = new GenericApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
        reader.loadBeanDefinitions(
                "ioc/genericApplicationContext.xml"       // 메타정보 로딩
        );
        ac.refresh();     // 메타정보로 컨텍스트 초기와

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
    }

    @Test
    public void genericXmlApplicationContext() {
        GenericApplicationContext ac = new GenericXmlApplicationContext(
                "ioc/genericApplicationContext.xml"
        );

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
    }

    @Test(expected = BeanCreationException.class)
    public void createContextWithoutParent() {
        ApplicationContext child = new GenericXmlApplicationContext(
                "ioc/childContext.xml");
    }

    @Test
    public void contextHierachy() {
        // GenericXmlApplicationContext는 루트 컨텍스트를 만든다.
        ApplicationContext parent = new GenericXmlApplicationContext(
                "ioc/parentContext.xml");

        // parent 컨텍스트를 부모로 하는 자식 컨텍스트 생성
        GenericApplicationContext child = new GenericApplicationContext(parent);

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
        reader.loadBeanDefinitions("ioc/childContext.xml");
        child.refresh();

        Printer printer = child.getBean("printer", Printer.class);
        Assert.notNull(printer, "expected not null");

        Hello hello = child.getBean("hello", Hello.class);
        Assert.notNull(hello, "not null");

        hello.print();
        assertThat(printer.toString(), is("Hello Child"));
    }

    @Test
    public void simpleBeanScanning() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(
                "springbook.learningtest.ioc.bean"
        );
        AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);

        Assert.notNull(hello, "not null");

    }

    @Test
    public void filterdBeanScanning() {
        ApplicationContext ctx = new GenericXmlApplicationContext(
                "ioc/filteredScanningContext.xml");

        Hello hello = ctx.getBean("hello", Hello.class);
        Assert.notNull(hello, "not null");
    }

    @Test
    public void configurationBean() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);
        AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
        Assert.notNull(hello, "not null");

        // 설정 클래스 자체도 빈으로 등록된다.
        AnnotatedHelloConfig config = ctx.getBean("annotatedHelloConfig",
                AnnotatedHelloConfig.class);
        Assert.notNull(config, "not null");
        assertThat(hello, is(sameInstance(config.annotatedHello())));
        assertThat(config.annotatedHello(), is(sameInstance(config.annotatedHello())));

        System.out.println(ctx.getBean("systemProperties").getClass());
    }

    @Test
    public void configurationClassWithoutConfigurationAnnotation() {
        ApplicationContext ctx = new GenericXmlApplicationContext(
                "ioc/helloServiceContext.xml");

        Hello hello = ctx.getBean("hello", Hello.class);
        Hello hello2 = ctx.getBean("hello2", Hello.class);

        assertThat(hello.getPrinter(), is(not(sameInstance(hello2.getPrinter()))));

        for(String beanName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
    }

    @Test
    public void configurationClassWithoutConfigurationAnnotation2() {
        ApplicationContext ctx = new GenericXmlApplicationContext(
                "ioc/helloServiceContext2.xml");

        Hello hello = ctx.getBean("hello", Hello.class);
        Hello hello2 = ctx.getBean("hello2", Hello.class);

        assertThat(hello.getPrinter(), is(sameInstance(hello2.getPrinter())));

        for(String beanName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
    }

    @Test
    public void constructorInjection() {
        ApplicationContext context = new GenericXmlApplicationContext(
                "ioc/constructorInjection.xml");
        Hello hello = context.getBean("hello", Hello.class);
        hello.print();

        assertThat(context.getBean("printer").toString(), is("Hello Spring"));

    }

    @Test
    public void autowireByType() {
        ApplicationContext context = new GenericXmlApplicationContext(
                "ioc/autowire.xml"
        );

        Hello hello = context.getBean("hello", Hello.class);
        assertThat(hello.getPrinter(), is(instanceOf(StringPrinter.class)));

        hello.print();
        assertThat(hello.getPrinter().toString(), is("Hello Spring"));
    }


}





























