package springbook.learningtest.ioc;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScopeTest {
    @Test
    public void singletonScope() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(
                SingletonBean.class, SingletonClientBean.class
        );
        Set<SingletonBean> beanSet = new HashSet<>();
        beanSet.add(ac.getBean(SingletonBean.class));
        beanSet.add(ac.getBean(SingletonBean.class));
        assertThat(beanSet.size(), is(1));

        beanSet.add(ac.getBean(SingletonClientBean.class).bean1);
        beanSet.add(ac.getBean(SingletonClientBean.class).bean2);
        assertThat(beanSet.size(), is(1));
    }

    static class SingletonBean {}
    static class SingletonClientBean {
        @Autowired
        SingletonBean bean1;
        @Autowired SingletonBean bean2;
    }

    @Test
    public void prototypeScope() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(
                PrototypeBean.class, PrototypeClientBean.class
        );
        Set<PrototypeBean> beanSet = new HashSet<>();
        beanSet.add(ac.getBean(PrototypeBean.class));
        assertThat(beanSet.size(), is(1));
        beanSet.add(ac.getBean(PrototypeBean.class));
        assertThat(beanSet.size(), is(2));

        beanSet.add(ac.getBean(PrototypeClientBean.class).bean1);
        assertThat(beanSet.size(), is(3));
        beanSet.add(ac.getBean(PrototypeClientBean.class).bean2);
        assertThat(beanSet.size(), is(4));
    }

    @Component("prototypeBean")
    @Scope("prototype")
    static class PrototypeBean {}

    static class PrototypeClientBean {
        @Autowired PrototypeBean bean1;
        @Autowired PrototypeBean bean2;
    }

    @Test
    public void objectFactory() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(
                PrototypeBean.class, ObjectFactoryConfig.class
        );
        ObjectFactory<PrototypeBean> factory = ac.getBean(
                "prototypeBeanFactory", ObjectFactory.class
        );

        Set<PrototypeBean> beanSet = new HashSet<>();
        for(int i = 1; i <= 4; i++) {
           beanSet.add(factory.getObject());
           assertThat(beanSet.size(), is(i));
        }
    }

    @Configuration
    static class ObjectFactoryConfig {
        @Bean
        public ObjectFactoryCreatingFactoryBean prototypeBeanFactory() {
            ObjectFactoryCreatingFactoryBean factoryBean = new
                    ObjectFactoryCreatingFactoryBean();
            factoryBean.setTargetBeanName("prototypeBean");
            return factoryBean;
        }
    }

    @Test
    public void serviceLocatorFactoryBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(
                PrototypeBean.class, ServiceLocatorConfig.class
        );
        PrototypeBeanFactory factory = ac.getBean(PrototypeBeanFactory.class);

        Set<PrototypeBean> beanSet = new HashSet<>();
        for(int i = 1; i <= 4; i++) {
            beanSet.add(factory.getPrototypeBean());
            assertThat(beanSet.size(), is(i));
        }
    }

    interface PrototypeBeanFactory {
        PrototypeBean getPrototypeBean();
    }

    @Configuration
    static class ServiceLocatorConfig {
        @Bean
        public ServiceLocatorFactoryBean prototypeBeanFactory() {
            ServiceLocatorFactoryBean factoryBean = new
                    ServiceLocatorFactoryBean();
            // PrototypeBeanFactory를 구현한 오브젝트를 PrototypeBeanFactory 타입으로 등록한다.
            factoryBean.setServiceLocatorInterface(PrototypeBeanFactory.class);
            return factoryBean;
        }
    }

    @Test
    public void providerTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(
                PrototypeBean.class, ProviderClient.class
        );
        ProviderClient client = ac.getBean(ProviderClient.class);

        Set<PrototypeBean> beanSet = new HashSet<>();
        for(int i = 1; i <= 4; i++) {
            beanSet.add(client.prototypeBeanProvider.get());
            assertThat(beanSet.size(), is(i));
        }

    }

    static class ProviderClient {
        @Inject
        Provider<PrototypeBean> prototypeBeanProvider;
    }

    static class AnnotationConfigDispatcherServlet extends DispatcherServlet {
        private Class<?>[] classes;

        public AnnotationConfigDispatcherServlet(Class<?> ...classes) {
            super();
            this.classes = classes;
        }

        @Override
        protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
            AbstractRefreshableWebApplicationContext wac = new AbstractRefreshableWebApplicationContext() {
                protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
                        throws BeansException, IOException {
                    AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
                    reader.register(classes);
                }
            };
            wac.setServletContext(getServletContext());
            wac.setServletConfig(getServletConfig());
            wac.refresh();
            return wac;
        }
    }

    MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void RequestScope() throws ServletException, IOException {
        MockServletConfig ctx = new MockServletConfig(new MockServletContext(), "spring");
        DispatcherServlet ds = new AnnotationConfigDispatcherServlet(
                HelloController.class, HelloService.class, RequestBean.class, BeanCounter.class
        );
        ds.init(ctx);

        BeanCounter counter = ds.getWebApplicationContext().getBean(BeanCounter.class);

        ds.service(
                new MockHttpServletRequest("GET", "/hello"), this.response);
        assertThat(counter.addCounter, is(2));
        assertThat(counter.size(), is(1));

        ds.service(
                new MockHttpServletRequest("GET", "/hello"), this.response);
        assertThat(counter.addCounter, is(4));
        assertThat(counter.size(), is(2));

        for(String name: ((AbstractRefreshableWebApplicationContext)ds.getWebApplicationContext()).getBeanFactory().getRegisteredScopeNames()) {
            System.out.println(name);
        }
    }

    @RequestMapping("/")
    static class HelloController {
        @Autowired HelloService helloService;
        @Autowired Provider<RequestBean> requestBeanProvider;
        @Autowired BeanCounter beanCounter;
        @RequestMapping("/hello")
        public String hello() {
            beanCounter.addCounter++;
            beanCounter.add(requestBeanProvider.get());
            helloService.hello();
            return "";
        }
    }

    static class HelloService {
        @Autowired Provider<RequestBean> requestBeanProvider;
        @Autowired BeanCounter beanCounter;
        public void hello() {
            beanCounter.addCounter++;
            beanCounter.add(requestBeanProvider.get());
        }
    }

    @Scope("request") static class RequestBean {}
    static class BeanCounter extends HashSet { int addCounter = 0; }

}
























