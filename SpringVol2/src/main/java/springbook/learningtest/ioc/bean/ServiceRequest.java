package springbook.learningtest.ioc.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ServiceRequest {
    Customer customer;

    @Autowired
    CustomerDao customerDao;

    public CustomerDao getCustomerDao() {
        return customerDao;
    }
}
