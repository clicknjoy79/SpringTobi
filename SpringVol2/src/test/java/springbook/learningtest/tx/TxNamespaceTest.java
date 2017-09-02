package springbook.learningtest.tx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.learningtest.hibernate.Member;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tx/txnamespacetest-context.xml")
public class TxNamespaceTest {
    private static final String LONG_STR = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

    @Autowired
    MemberDao dao;

    @Test
    public void tx() {
        dao.deleteAll();
        assertThat(dao.count(), is(0L));

        dao.add(Arrays.asList(new Member[] {
                new Member(1, "Spring", 1.2),
                new Member(2, "Spring", 1.2)
        }));

        assertThat(dao.count(), is(2L));

        try {
            dao.add(Arrays.asList(new Member[] {
                    new Member(3, "Spring", 1.2),
                    new Member(4, LONG_STR, 1.2)
            }));
            fail();
        }  catch (DataAccessException e) {}

        assertThat(dao.count(), is(2L));
    }


}




























