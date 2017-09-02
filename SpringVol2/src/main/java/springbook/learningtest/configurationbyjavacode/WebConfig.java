package springbook.learningtest.configurationbyjavacode;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import springbook.learningtest.web.spring_el.UserController;

/**
 * 자바 코드로 설정하는 경우에는 서블릿 컨텍스트에서는 오로지 웹 계층과 관련된 빈만 등록가능 하다.
 * 서비스 계층이나 DAO 계층의 빈은 등록할 수 없다.(해당 빈은 AppConfig에서 등록해야 인식할 수 있다)
 */
@Configuration
@EnableWebMvc
@ComponentScan("springbook.learningtest.web.spring_el")
@ComponentScan("springbook.learningtest.aop")
public class WebConfig extends WebMvcConfigurerAdapter {
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new
                InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("web.messages");
        return messageSource;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/ui/**")
                .addResourceLocations("classpath:/META-INF/ui/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new UserController.StringToTypeConverter());
        registry.addConverter(new UserController.StringToLocalConverter());
        registry.addConverter(new UserController.LocalToStringConverter());
        registry.addConverter(new UserController.TypetoStringConverter());
    }
}
