package springbook.learningtest.remote;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import springbook.learningtest.web.AbstractDispatcherServletTest;
import springbook.learningtest.web.spring_el.User;

import javax.servlet.ServletException;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * 서버 구동후 테스트 할 것
 */
public class RestTemplateTest extends AbstractDispatcherServletTest{
    @Test
    public void openApi() {
        RestTemplate template = new RestTemplate();

        String key = "086b55df8c6b66bc105b67cfc3880dcb";

        String result =
                template.getForObject("http://apis.daum.net/search/blog?apikey=" + key + "&q={key}&output={output}",
                        String.class, "SpringFramework", "json");
        System.out.println(result);
        assertThat(result.contains("\"result\":\"10\""), is(true));

    }

    @Test
    @Ignore
    public void restTemplate() throws ServletException, IOException {
        RestTemplate restTemplate = new RestTemplate();

        User user = restTemplate.getForObject("http://localhost/user/search/2", User.class);
        assertThat(user.getId(), is(2));
        assertThat(user.getName(), is("2: 홍길동"));
        assertThat(user.getInterests(), is(nullValue()));
        assertThat(user.getUserType(), is(nullValue()));
        assertThat(user.getUserLocal(), is(nullValue()));
    }
}
