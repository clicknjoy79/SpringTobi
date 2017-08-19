package springbook.learningtest.ibatis;

import com.ibatis.sqlmap.client.SqlMapClient;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import springbook.learningtest.hibernate.Member;

import java.util.List;

public class IBatisDao {
    private SqlMapClientTemplate sqlMapClientTemplate;

    public void setSqlMapClient(SqlMapClient sqlMapClient) {
        sqlMapClientTemplate = new SqlMapClientTemplate(sqlMapClient);
    }

    public void insert(Member m) {
        sqlMapClientTemplate.insert("insertMember", m);
    }

    public void deleteAll() {
        sqlMapClientTemplate.delete("deleteMemberAll");
    }

    public Member select(int id) {
        return (Member)sqlMapClientTemplate.queryForObject("findMemberById", id);
    }

    public List<Member> selectAll() {
        return sqlMapClientTemplate.queryForList("findMembers");
    }
}
