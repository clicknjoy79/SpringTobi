package springbook.learningtest.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Hibernate5 에서는 동작하지 않는 부분이 있어서 Hibernate4로 세팅함
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/hibernate/hibernatetest-context.xml")
public class HibernateTest {
    @Autowired
    MemberDao dao;
    @Autowired
    MemberTemplateDao templateDao;

    @Test
    @Transactional
    public void hibernateTemplate() {
        templateDao.hibernateTemplate.bulkUpdate("delete from Member");
        Member m = new Member(1, "Hibernate", 1.2);
        templateDao.hibernateTemplate.save(m);
        long count = templateDao.hibernateTemplate.execute(new HibernateCallback<Long>() {
            @Override
            public Long doInHibernate(Session session) throws HibernateException {
                return (Long)session.createQuery("select count(m) from Member m").uniqueResult();
            }
        });

        assertThat(count, is(1L));
    }

    @Test
    @Transactional
    public void hibernateApi() {
        Session s = dao.sessionFactory.getCurrentSession();
        s.createQuery("delete from Member").executeUpdate();
        Member m = new Member(1, "Hibernate", 1.2);
        s.save(m);
        long count = (Long)s.createQuery("select count(m) from Member m").uniqueResult();
        assertThat(count, is(1L));
    }

    // 직접 트랜잭션 생성
    @Test
    public void localSessionFactoryBean() {
        ApplicationContext ac = new GenericXmlApplicationContext("hibernate/localsessionfactorybean-context.xml");
        SessionFactory sessionFactory = ac.getBean(SessionFactory.class);
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.createQuery("delete from Member").executeUpdate();
        Member m = new Member(1, "Hibernate", 1.2);
        session.save(m);
        long count = (Long)session.createQuery("select count(m) from Member m").uniqueResult();
        assertThat(count, is(1L));

        tx.commit();
        session.close();
        sessionFactory.close();
    }

}































