package springbook.learningtest.ioc.spring31;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import springbook.learningtest.ioc.scanner.dao.MyDao;
import springbook.learningtest.ioc.scanner.service.MyService;
import springbook.learningtest.ioc.scanner.service.ServiceMarker;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class JavaCodeBeanDefinitionTest {
    @Test
    public void componentScan() {
        // basePackages
        AnnotationConfigApplicationContext ac1 = new AnnotationConfigApplicationContext(C1.class);
        assertThat(ac1.getBean(MyDao.class), is(notNullValue()));
        assertThat(ac1.getBean(MyService.class), is(notNullValue()));

        // excludes
        ApplicationContext ac2 = new AnnotationConfigApplicationContext(C2.class);
        try {
            ac2.getBean(MyDao.class);
            fail();
        } catch (NoSuchBeanDefinitionException e) {}
        assertThat(ac2.getBean(MyService.class), is(notNullValue()));

        // excludes
        ApplicationContext ac3 = new AnnotationConfigApplicationContext(C3.class);
        try {
             ac3.getBean(MyDao.class);
             fail();
        } catch (NoSuchBeanDefinitionException e) {}
        assertThat(ac3.getBean(MyService.class), is(notNullValue()));

        // basePackageClasses
        ApplicationContext ac4 = new AnnotationConfigApplicationContext(C4.class);
        try {
            ac4.getBean(MyDao.class);
            fail();
        } catch (NoSuchBeanDefinitionException e) {}
        assertThat(ac4.getBean(MyService.class), is(notNullValue()));
    }

    @Configuration
    @ComponentScan("springbook.learningtest.ioc.scanner")
    static class C1 {}

    @Configuration
    @ComponentScan(basePackages = {"springbook.learningtest.ioc.scanner"},
        excludeFilters=@ComponentScan.Filter(Repository.class))
    static class C2 {}

    @Configuration
    @ComponentScan(basePackages = {"springbook.learningtest.ioc.scanner"},
        excludeFilters = @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value=MyDao.class))
    static class C3 {}

    @Configuration
    @ComponentScan(basePackageClasses = {ServiceMarker.class})
    static class C4 {}

    @Test
    public void atImport() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        ac.getBean(DataConfig.class);
        ac.getBean(AppConfig.class);
    }

    @Configuration
    @Import(DataConfig.class)
    static class AppConfig {}

    @Configuration
    static class DataConfig {}

    @Test
    public void atImportResource() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(MainConfig.class);
        assertThat(ac.getBean("name", String.class), is("Toby"));
    }

    @Configuration
    @ImportResource("ioc/extra.xml")
    static class MainConfig {}

}


























