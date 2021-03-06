package springbook.learningtest.configurationbyjavacode;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitialization implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new
                AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        ContextLoaderListener listener = new ContextLoaderListener(rootContext);
        servletContext.addListener(listener);

        AnnotationConfigWebApplicationContext servletAppContext = new
                AnnotationConfigWebApplicationContext();
        servletAppContext.register(WebConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(servletAppContext);

        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher", dispatcherServlet);
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");

        FilterRegistration.Dynamic filter = servletContext.addFilter("encodingFilter",
                CharacterEncodingFilter.class);
        filter.setInitParameter("encoding", "UTF-8");
        filter.addMappingForServletNames(null, false, "dispatcher");

    }
}




























