package springbook.learningtest.tx;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import springbook.learningtest.hibernate.Member;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tx/jtatxtest-context.xml")
public class JtaTxTest {
    private static final String LONG_STR = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

    @Resource MemberJdbcDao jdbcDao1;
    @Resource MemberJdbcDao jdbcDao2;
    @Autowired MemberHibernateDao hibernateDao;
    @Autowired PlatformTransactionManager transactionManager;


    @Test
    public void jtaTx() {
        jdbcDao1.deleteAll();
        jdbcDao2.deleteAll();
        hibernateDao.deleteAll();
        jdbcDao1.add(new Member(2, "TX1", 1.2));
        jdbcDao2.add(new Member(2, "TX2", 1.2));

        assertThat(jdbcDao1.count(), is(1L));
        assertThat(jdbcDao2.count(), is(1L));
        assertThat(hibernateDao.count(), is(0L));

        try {
            new TransactionTemplate(transactionManager).execute(
                    new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            jdbcDao1.add(new Member(1, "Spring", 1.2));
                            jdbcDao2.add(new Member(1, "Spring", 1.2));
                            hibernateDao.add(new Member(1, "Spring", 1.2));
                            assertThat(jdbcDao1.count(), is(2L));
                            assertThat(jdbcDao2.count(), is(2L));
                            assertThat(hibernateDao.count(), is(1L));

                            jdbcDao1.add(new Member(2, LONG_STR, 1.2));
                        }
                    }
            );
            fail("DataAccessException expected");
        } catch (DataAccessException e) {e.printStackTrace();}

        assertThat(jdbcDao1.count(), is(1L));
        assertThat(jdbcDao2.count(), is(1L));
        assertThat(hibernateDao.count(), is(0L));
    }


    static class MemberJdbcDao extends JdbcDaoSupport {
        SimpleJdbcInsert insert;

        @Override
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
    public static class MemberJpaDao {
        @PersistenceContext
        EntityManager entityManager;

        public void add(Member m) {
            entityManager.persist(m);
            entityManager.flush();
        }

        public void deleteAll() {
            entityManager.createQuery("delete from Member").executeUpdate();
        }

        public long count() {
            return entityManager.createQuery("select count(m) from Member m", Long.class).getSingleResult();
        }

    }

    static class MemberHibernateDao extends HibernateDaoSupport {
        void add(Member m) {
            getHibernateTemplate().persist(m);
            getHibernateTemplate().flush();
        }

        void deleteAll() {
            getHibernateTemplate().bulkUpdate("delete from Member");
        }

        long count() {
            return getHibernateTemplate().execute(
                    new HibernateCallback<Long>() {
                        @Override
                        public Long doInHibernate(Session session) throws HibernateException {
                            return (Long)session.createQuery("select count(m) from Member m").uniqueResult();
                        }
                    }
            );
        }
    }

}
