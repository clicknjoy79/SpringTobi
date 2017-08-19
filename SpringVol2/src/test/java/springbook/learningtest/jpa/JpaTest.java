package springbook.learningtest.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import springbook.learningtest.hibernate.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/jpa/jpatest-context.xml")
public class JpaTest {
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MemberDao dao;

    @Autowired
    MemberRepositoryDao repositoryDao;

    @Test
    @Transactional
    public void sharedEntityManager() {   // 여러 스레드에서 공유되는 EntityManager
        dao.em.createQuery("delete from Member").executeUpdate();
        Member m = new Member(10, "Spring", 7.8);
        dao.em.persist(m);
        Long count = (Long)dao.em.createQuery("select count(m) from Member m").getSingleResult();
        assertThat(count, is(1L));
    }

    @Test
    @Transactional
    public void entityManagerFactory() {
        EntityManager em = dao.emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from Member").executeUpdate();
        Member m = new Member(10, "Spring", 7.8);
        em.persist(m);
        Long count = (Long)em.createQuery("select count(m) from Member m").getSingleResult();
        assertThat(count, is(1L));
        em.getTransaction().commit();
    }

    @Test(expected = PersistenceException.class)
    @Transactional
    public void jpaApiException() {
        dao.addDuplicatedId();
    }

    @Test(expected = DataAccessException.class)
    @Transactional
    public void jpaApiRepositoryException() {
        repositoryDao.addDuplicatedId();
    }

}

































