package springbook.learningtest.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ConfigurableDispatcherServlet extends DispatcherServlet {
    protected Class<?>[] classes;
    private String[] locations;

    private ModelAndView modelAndView;

    public ConfigurableDispatcherServlet(String[] locations) {
        this.locations = locations;
    }

    public ConfigurableDispatcherServlet(Class<?> ...classes) {
        this.classes = classes;
    }

    public void setLocations(String ...locations) {
        this.locations = locations;
    }

    public void setClasses(Class<?> ...classes) {
        this.classes = classes;
    }

    public void setRelativeLocations(Class clazz, String ...relativeLocations) {
        String[] locations = new String[relativeLocations.length];
        for(int i = 0; i < relativeLocations.length; i++) {
            locations[i] = relativeLocations[i];
        }
        this.setLocations(locations);
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        modelAndView = null;
        super.service(req, res);
    }

    // 등록된 리소스로 어플리케이션 컨텍스트를 생성하는 코드
    // init() 메소드 안에서 실행된다.
    @Override
    protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
        AbstractRefreshableWebApplicationContext wac = new AbstractRefreshableWebApplicationContext() {
            @Override
            protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
                if(locations != null) {
                    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(beanFactory);
                    xmlReader.loadBeanDefinitions(locations);
                }

                if(classes != null) {
                    AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
                    reader.register(classes);
                }
            }
        };
        wac.setServletContext(getServletContext());
        wac.setServletConfig(getServletConfig());
        wac.refresh();
        return wac;
    }

    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.modelAndView = mv;
        super.render(mv, request, response);
    }

    public ModelAndView getModelAndView() {
        return modelAndView;
    }
}



























