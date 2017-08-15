package springbook.learningtest.ibatis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.learningtest.jdbc.Member;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ibatis/ibatistest-context.xml")
public class IBatisTest {
    @Autowired IBatisDao dao;

    @Test
    public void ibatis() {
        dao.deleteAll();
        dao.insert(new Member(5, "iBatis", 1.2));
        dao.insert(new Member(6, "sqlMap", 3.3));

        Member m = dao.select(5);
        assertThat(m.getId(), is(5));
        assertThat(m.getName(), is("iBatis"));
        assertThat(m.getPoint(), is(1.2));

        List<Member> ms = dao.selectAll();
        assertThat(ms.size(), is(2));
    }

}
