package springbook.learningtest.tx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import springbook.learningtest.hibernate.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tx/jdbcjpatxtest-context.xml")
public class JdbcJpaTxTest {
    private static final String LONG_STR = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

    @Autowired MemberJdbcDao jdbcDao;
    @Autowired MemberJpaDao jpaDao;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired EntityManagerFactory emf;

    @Test
    public void jdbcDaoWithoutTx() {
        jdbcDao.deleteAll();
        assertThat(jdbcDao.count(), is(0L));

        try {
            rollbackJob();
            fail("DataAccessException expected");
        } catch (DataAccessException e) {}

        assertThat(jdbcDao.count(), is(1L));
    }

    private void rollbackJob() {
        jdbcDao.add(new Member(1, "Spring", 1.2));
        jdbcDao.add(new Member(2, LONG_STR, 1.2));
    }

    @Test
    public void jdbcDaoWithTx() {
        jdbcDao.deleteAll();
        assertThat(jdbcDao.count(), is(0L));

        try {
            new TransactionTemplate(transactionManager).execute(
                    new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            rollbackJob();
                        }
                    }
            );
            fail("DataAccessException expected");
        } catch (DataAccessException e) {}
        assertThat(jdbcDao.count(), is(0L));
    }

    @Test
    public void jdbcAndJpaTx() {
        jdbcDao.deleteAll();
        assertThat(jdbcDao.count(), is(0L));

        try {
            new TransactionTemplate(transactionManager).execute(
                    new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            jdbcDao.add(new Member(1, "Spring", 1.2));
                            jpaDao.add(new Member(2, "Jpa", 1.2));
                            assertThat(jdbcDao.count(), is(2L));
                            jpaDao.add(new Member(3, LONG_STR, 1.2));
                        }
                    }
            );
        } catch (DataAccessException e) {}
        assertThat(jdbcDao.count(), is(0L));

    }

    static class MemberJdbcDao extends JdbcDaoSupport {
        SimpleJdbcInsert insert;

        protected void initTemplateConfig() {
            insert = new SimpleJdbcInsert(getDataSource()).withTableName("member");
        }

        void add(Member m) {
            insert.execute(new BeanPropertySqlParameterSource(m));
        }

        void deleteAll() {
            getJdbcTemplate().execute("delete from member");
        }

        long count() {
            return getJdbcTemplate().queryForObject("select count(*) from member", Long.class);
        }
    }

    @Repository
    static class MemberJpaDao {
        @PersistenceContext
        EntityManager entityManager;

        void add(Member m) {
            entityManager.persist(m);
            entityManager.flush();
        }
    }

}
