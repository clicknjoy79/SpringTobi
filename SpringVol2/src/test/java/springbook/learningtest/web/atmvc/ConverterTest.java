package springbook.learningtest.web.atmvc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springbook.learningtest.web.AbstractDispatcherServletTest;
import springbook.user.domain.Level;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class ConverterTest extends AbstractDispatcherServletTest {
    @Test
    public void inheritedGenericConversionService() throws ServletException, IOException {
        //setClasses(SearchController.class, MyConversionService.class);
        setClasses(SearchController.class, MyConversionServiceFactoryBean.class);

        initRequest("/user/search.do").addParameter("level", "1");
        runService();
        assertModel("level", Level.BASIC);
    }

    @Controller
    public static class SearchController {
        @Autowired
        ConversionService conversionService;

        @InitBinder
        public void initBinder(WebDataBinder dataBinder) {
            dataBinder.setConversionService(this.conversionService);
        }

        @RequestMapping("/user/search")
        public void search(@RequestParam Level level, Model model) {
            model.addAttribute("level", level);
        }
    }

    static class MyConversionService extends GenericConversionService {{
        this.addConverter(new LevelToStringConverter());
        this.addConverter(new StringToLevelConverter());
    }}

    static class MyConversionServiceFactoryBean extends ConversionServiceFactoryBean {{
        this.setConverters(new LinkedHashSet<>(Arrays.asList(
                new Converter[] { new LevelToStringConverter(), new StringToLevelConverter()}
        )));
    }}

    static class LevelToStringConverter implements Converter<Level, String> {
        @Override
        public String convert(Level source) {
            return String.valueOf(source.intValue());
        }
    }

    static class StringToLevelConverter implements Converter<String, Level> {
        @Override
        public Level convert(String source) {
            return Level.valueOf(Integer.parseInt(source));
        }
    }

    @Test
    public void compositeGenericConversionService() throws ServletException, IOException {
        setRelativeLocations("web/conversionservice.xml");
        setClasses(SearchController.class);
        initRequest("/user/search.do").addParameter("level", "1");
        runService();
        assertModel("level", Level.BASIC);
    }
}



























