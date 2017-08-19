package springbook.learningtest.tx;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import springbook.learningtest.hibernate.Member;

import java.util.List;


public class MemberDaoImpl extends JdbcDaoSupport implements MemberDao {
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
}
