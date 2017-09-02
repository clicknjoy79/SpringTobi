package springbook.learningtest.aspect;

import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Test;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class AtAspectTest1 {
    @Test
    public void simple() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(HelloImpl.class, HelloWorld.class, HelloAspect.class);
        AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(ac);

        ac.refresh();

        Hello hello = ac.getBean(Hello.class);
        System.out.println(hello.makeHello("Spring"));
        System.out.println(hello.makeHello("Spring", 2));
        System.out.println(hello.add(1, 2));

        HelloWorld helloWorld = ac.getBean(HelloWorld.class);
        helloWorld.hello();
        User u  = new User(); u.id = 1; u.name = "Spring";
        helloWorld.printUser(u);

        System.out.println("\n\n");
        HelloAspect aspect = ac.getBean(HelloAspect.class);
        System.out.println("resultCombine: " + aspect.resultCombine);
        System.out.println("resultReturnString: " + aspect.resultReturnString);
        System.out.println("resultHelloClass: " + aspect.resultHelloClass);
        System.out.println("resultCommon: " + aspect.resultCommon);
    }

    @Aspect static class HelloAspect {
        List resultCombine = new ArrayList();
        List resultReturnString = new ArrayList();
        List resultHelloClass = new ArrayList();
        List resultCommon = new ArrayList();

        public void clear() { resultCombine.clear(); resultReturnString.clear(); resultHelloClass.clear(); resultCommon.clear(); }

//        @Pointcut("execution(String *(..))") private void returnString() {}
        @Pointcut("@args(springbook.learningtest.aspect.AtAspectTest1.Anno)") // 메소드로 넘어오는 parameter 객체에 @Anno가 있는 경우
        private void returnString() {}
//        @Pointcut("within(springbook.learningtest.aspect.*)") private void helloClass() {}
//        @Pointcut("returnString() || helloClass()") private void combine() {}
//        @Pointcut("returnString() && args(String)") private void common() {}

        @Before("returnString()") public void callLogReturnString(JoinPoint jp) {
            System.out.println("\t" + jp.getSignature().getDeclaringTypeName());
            System.out.println("\t" + jp.getSignature().getName());
            for(Object arg : jp.getArgs()) {
                System.out.println("\t" + arg);
            }
            resultReturnString.add(jp.getSignature().getDeclaringType().getSimpleName() + "/" + jp.getSignature().getName());
        }

//        @Before("helloClass()") public void callLogHelloClass(JoinPoint jp) {
//            resultHelloClass.add(jp.getSignature().getDeclaringType().getSimpleName() + "/" + jp.getSignature().getName());
//        }

//        @Before("common()") public void callLogCommon(JoinPoint jp) {
//            resultCommon.add(jp.getSignature().getDeclaringType().getSimpleName() + "/" + jp.getSignature().getName());
//        }
//        @Before("args(springbook.learningtest.aspect.AtAspectTest1.User)")
//        public void user(JoinPoint jp) {
//            ((User)(jp.getArgs()[0])).name = "Toby";
//        }
//        @AfterReturning(pointcut = "execution(* makeHello(..))", returning = "ret")
//        public void ret(JoinPoint jp, String ret) {
//            System.out.println("RET  = " + ret);
//        }


//        @Before("@target(an)") // @Anno 붙은 오브젝트의 메소드 실행에 적용
//        public void an(JoinPoint jp, Anno an) {
//            System.out.println(an);
//        }
    }

    @Target({ElementType.TYPE, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Anno {
        String value() default "a";
    }

    interface Hello {
        int add(int a, int b);
        String makeHello(String name);
        String makeHello(String name, int repeat);
    }

    @Anno("b") static class HelloImpl implements Hello {
        @Override
        public String makeHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String makeHello(String name, int repeat) {
            String names = "";
            while(repeat > 0) { names += name; repeat--; }
            return "Hello " + names;
        }

        @Override
        public int add(int a, int b) {
            return a + b;
        }

        public String convert(int a) { return String.valueOf(a); }
        protected int increase(int a) { return a++; }
    }

    @Anno("b") static class HelloWorld {
        public void hello() {
            System.out.println("Hello World!!!");
        }
        public void printUser(User u) {
            System.out.println(u);
        }
    }

    @Anno
    static class User {
        int id; String name;
        public String toString() { return "User [id = " + id + ", name = " + name + "]"; }
    }
}






































