package springbook.learningtest.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/cache/cache.xml")
public class XmlCacheTest {
    @Autowired Service service;

    @Test
    public void cache() {
        assertThat(service.getCount(), is(0));

        service.cacheable();
        assertThat(service.getCount(), is(1));

        service.cacheable();
        assertThat(service.getCount(), is(1));

        service.cacheable();
        assertThat(service.getCount(), is(1));
    }


    static class Service {
        int count;

        public int getCount() { return count; }

        @Cacheable("count")
        public String cacheable() {
            count++;
            return "CACHE";
        }
    }
}
