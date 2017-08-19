package springbook.learningtest.tx;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import springbook.learningtest.hibernate.Member;

import java.util.List;

@Transactional
public class MemberDaoImplAspectJ extends JdbcDaoSupport implements MemberDaoAspectJ {
    SimpleJdbcInsert insert;

    @Override
    protected void initTemplateConfig() {
        insert = new SimpleJdbcInsert(getDataSource()).withTableName("member");
    }

    @Override
    public void add(Member m) {
        insert.execute(new BeanPropertySqlParameterSource(m));
    }

    @Override
    public void add(List<Member> members) {
        for(Member m : members) add(m);
    }

    @Override
    public void deleteAll() {
        getJdbcTemplate().execute("delete from member");
    }

    @Override
    public long count() {
        return getJdbcTemplate().queryForObject("select count(*) from member", Long.class);
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public void addWithoutTx(List<Member> members) {
        add(members);
    }
}
