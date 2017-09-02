package springbook.learningtest.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestContextTest {
    @Autowired BeanA a;
    @Resource BeanA beanA;
    BeanA b;
    BeanA c;

    @Autowired
    public void setBeanA(BeanA b) { this.b = b;}

    @Autowired
    public void init(BeanA c) { this.c = c; }

    @Test
    @DirtiesContext
    public void test1() {
        assertThat(a, is(notNullValue()));
        assertThat(beanA, is(notNullValue()));
        assertThat(b, is(notNullValue()));
        assertThat(c, is(notNullValue()));
    }

    @Configuration
    static class Config {
        @Bean public BeanA beanA() { return new BeanA(); }
    }

    static class BeanA {
        @PostConstruct public void init() {
            System.out.println("BeanA created !!!");
        }
    }
}
