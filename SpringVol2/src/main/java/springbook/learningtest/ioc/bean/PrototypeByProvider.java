package springbook.learningtest.ioc.bean;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;

@Component
public class PrototypeByProvider {
    @Inject
    Provider<ServiceRequest> serviceRequestProvider;

    public ServiceRequest getServiceRequest() {
        return serviceRequestProvider.get();
    }
}
