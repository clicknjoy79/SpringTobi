package springbook.learningtest.web.atmvc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class FormatterTest extends AbstractDispatcherServletTest{
    @Test
    public void formatterTest() throws ServletException, IOException {
        setClasses(UserController.class, ConverterConfig.class);
        initRequest("/hello.do").addParameter("money", "$1,234.56" )
                .addParameter("date", "01/02/1999");
        runService();
    }

    @Controller
    static class UserController {
        @Autowired ConversionService conversionService;

        @InitBinder
        public void initBinder(WebDataBinder dataBinder) {
            dataBinder.setConversionService(this.conversionService);
        }

        @RequestMapping("/hello")
        void hello(User user) {
            System.out.println(user.date);
            System.out.println(user.money);
        }
    }

    static class User {
        @DateTimeFormat(pattern = "dd/MM/yyyy")
        Date date;
        public Date getDate() {
            return date;
        }
        public void setDate(Date date) {
            this.date = date;
        }

        @NumberFormat(pattern = "$###,##0.00")
        BigDecimal money;
        public BigDecimal getMoney() {
            return money;
        }
        public void setMoney(BigDecimal money) {
            this.money = money;
        }
    }

    @Configuration
    static class ConverterConfig {
        @Bean
        public FormattingConversionServiceFactoryBean fomattingConversionServiceFactoryBean() {
            return new FormattingConversionServiceFactoryBean();
        }
    }
}
