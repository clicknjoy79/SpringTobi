package springbook.learningtest.ioc;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.learningtest.ioc.bean.AbstractController;
import springbook.learningtest.ioc.bean.CustomerDao;
import springbook.learningtest.ioc.bean.PrototypeByProvider;
import springbook.learningtest.ioc.bean.ServiceRequest;

import javax.inject.Inject;
import javax.inject.Provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class PrototypeBeanDLTest {
    @Test
    public void prototypeDLByObjectFactory() {
        ApplicationContext context = new AnnotationConfigApplicationContext(ServiceRequestFactory.class);
        ObjectFactory<ServiceRequest> factory = context.getBean(ServiceRequestFactory.class);

        ServiceRequest request1 = factory.getObject();
        ServiceRequest request2 = factory.getObject();
        assertThat(request1, is(not(request2)));
        assertThat(request1.getCustomerDao(), is(request2.getCustomerDao()));
    }

    static class ServiceRequestFactory implements ObjectFactory<ServiceRequest> {
        @Override
        public ServiceRequest getObject() throws BeansException {
            return new ServiceRequest();
        }
    }

    @Test
    public void prototypeDLByObjectFactoryCreatingFactoryBean() {
        ApplicationContext context = new GenericXmlApplicationContext(
                "ioc/servicerequestfactory.xml");
        ObjectFactory<ServiceRequest> factory = context.getBean("serviceRequestFactory", ObjectFactory.class);
        ServiceRequest request1 = factory.getObject();
        ServiceRequest request2 = factory.getObject();
        assertThat(request1, is(not(request2)));
        assertThat(request1.getCustomerDao(), is(request2.getCustomerDao()));
    }

    @Test
    public void prototypeDLByServiceLocatorFactoryBean() {
        ApplicationContext context = new AnnotationConfigApplicationContext(
                ServiceFactory.class, ServiceRequest.class, CustomerDao.class);

        ServiceRequestFactory2 factory = context.getBean(ServiceRequestFactory2.class);
        ServiceRequest request1 = factory.getServiceRequest();
        ServiceRequest request2 = factory.getServiceRequest();
        assertThat(request1, is(not(request2)));
        assertThat(request1.getCustomerDao(), is(request2.getCustomerDao()));

    }

    interface ServiceRequestFactory2 {
        ServiceRequest getServiceRequest();
    }

    static class ServiceFactory {
        @Bean
        public ServiceLocatorFactoryBean serviceLocatorFactoryBean() {
            ServiceLocatorFactoryBean serviceLocatorFactoryBean = new
                    ServiceLocatorFactoryBean();
            serviceLocatorFactoryBean.setServiceLocatorInterface(ServiceRequestFactory2.class);
            return serviceLocatorFactoryBean;
        }
    }

    @Test
    public void prototypeDLByMethodCreation() {
        ApplicationContext context = new GenericXmlApplicationContext(
                "ioc/servicerequestcontroller.xml");

        AbstractController controller = context.getBean("serviceRequestController", AbstractController.class);
        ServiceRequest request1 = controller.getServiceRequest();
        ServiceRequest request2 = controller.getServiceRequest();
        ServiceRequest request3 = controller.getServiceRequest();
        assertThat(request1, is(not(request2)));
        assertThat(request2, is(not(request3)));
        assertThat(request3, is(not(request1)));

        assertThat(request1.getCustomerDao(), is(request2.getCustomerDao()));
        assertThat(request2.getCustomerDao(), is(request3.getCustomerDao()));
        assertThat(request3.getCustomerDao(), is(request1.getCustomerDao()));
    }

    @Test
    public void prototypeDLByProvider() {
        ApplicationContext context = new GenericXmlApplicationContext(
                "ioc/prototypeprovider.xml");
        PrototypeByProvider provider = context.getBean(PrototypeByProvider.class);

        ServiceRequest request1 = provider.getServiceRequest();
        ServiceRequest request2 = provider.getServiceRequest();
        ServiceRequest request3 = provider.getServiceRequest();

        assertThat(request1, is(not(request2)));
        assertThat(request2, is(not(request3)));
        assertThat(request3, is(not(request1)));

        assertThat(request1.getCustomerDao(), is(request2.getCustomerDao()));
        assertThat(request2.getCustomerDao(), is(request3.getCustomerDao()));
        assertThat(request3.getCustomerDao(), is(request1.getCustomerDao()));
    }

}
























