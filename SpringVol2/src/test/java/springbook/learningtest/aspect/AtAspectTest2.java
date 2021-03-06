package springbook.learningtest.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Test;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import springbook.learningtest.web.hello.HelloController;

public class AtAspectTest2 {
    @Test
    public void simple() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(HelloBean.class, SimpleMornitoringAspect.class);
        ac.register(Client.class, HiBean.class);
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(ac);
        AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(ac);
        ac.refresh();

        for(String name : ac.getBeanDefinitionNames()) {
            System.out.println(name + "\t\t" + ac.getBean(name).getClass());
        }

        ac.getBean(Client.class).doHello(); // Hi Hello
        ac.getBean(Client.class).doHiBean();    // Hi Hi Bean

        ac.getBean("hello", HelloBean.class).sayHello("Spring");  // Hello Spring
        System.out.println(ac.getBean("hello", HelloBean.class).add(1, 2));
        ac.getBean("hi", Hello.class).sayHello("Spring"); // Hi Spring
    }


    interface Hello { String sayHello(String name); }

    @Component("hello") static class HelloBean {
        public String sayHello(String name) { return "Hello " + name; }
        public int add(int a, int b) { return a + b; }
    }

    @Component("client") static class Client {
        @Autowired HiBean hiBean;
        @Autowired Hello hello;

        public void doHiBean() {
            System.out.println(hiBean.sayHello("Hi Bean"));
        }
        public void doHello() {
            System.out.println(hello.sayHello("Hello"));
        }
    }

    @Component("hi") static class HiBean implements Hello {
        public String sayHello(String name) {
            return "Hi " + name;
        }
    }

    @Aspect
    static class SimpleMornitoringAspect {
        @Pointcut("execution(* *(..))")
        private void all() {}

        @Around("all()")
        public Object pringParametersAndReturnVal(ProceedingJoinPoint pjp) throws Throwable {
            System.out.println("Class: " + pjp.getTarget().getClass());
            System.out.println("Method: " + pjp.getSignature().getName());
            System.out.print("Args : ");
            for(Object arg : pjp.getArgs()) {
                System.out.println(arg + " / ");
            }

            Object ret = pjp.proceed();

            System.out.println("\nReturn : " + ret);

            return ret;
        }
    }
}


























