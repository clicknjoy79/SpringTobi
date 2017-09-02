package springbook.learningtest.spring31.web;

import org.junit.Test;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import java.io.IOException;

public class UriComponentBuilderTest extends AbstractDispatcherServletTest {
    @Test
    public void uriCB() throws ServletException, IOException {
        UriComponents uriComponents =
                UriComponentsBuilder.fromUriString("http://example.com/hetels/{hotel}/bookings/{booking}").build();
        System.out.println(uriComponents.expand("42", "21").encode().toUriString());

        int userId = 10;
        int orderId = 20;
        UriComponents uc =
                UriComponentsBuilder.fromUriString("http://www.myshop.com/users/{user}/orders/{order}").build();
        System.out.println(uc.expand(userId, orderId).encode().toUriString());

        UriComponents uc2 = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("www.myshop.com")
                .path("/users/{user}/orders/{order}").build();
        System.out.println(uc2.expand(userId, orderId).encode().toUriString());

        initRequest("/user/edit").addParameter("useId", "whiteship")
                .addParameter("name", "Spring")
                .addParameter("password", "1234");

        ServletUriComponentsBuilder uriComponents1 =
                ServletUriComponentsBuilder.fromRequest(request);
        System.out.println("uriComponentsBuilder: " + uriComponents1.toUriString());
    }

}
