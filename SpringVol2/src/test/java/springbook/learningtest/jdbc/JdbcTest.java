package springbook.learningtest.jdbc;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/jdbc/test-applicationContext.xml")
public class JdbcTest {
    @Autowired DataSource dataSource;

    SimpleJdbcInsert sji;
    NamedParameterJdbcTemplate jt;

    @BeforeClass
    public static void init() throws IllegalStateException, NamingException, SQLException {
        // SimpleNamingContextBuilder로 바인딩하는 경우 JVM 레벨에 등록되므로 시작할 때
        // binding 된 값들을 제거하고 시작해야 한다.
        SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        SimpleDriverDataSource ds = new SimpleDriverDataSource(
                new com.mysql.jdbc.Driver(),
                "jdbc:mysql://localhost/springbook?characterEncoding=UTF-8",
                "root", "1234");
        builder.bind("jdbc/DefaultDS", ds);
        builder.activate();
    }

    @Before
    public void before() {
        System.out.println(dataSource);
        jt = new NamedParameterJdbcTemplate(dataSource);
        sji = new SimpleJdbcInsert(dataSource).withTableName("member");
    }

    @Test
    public void simpleJdbcCall() {
        System.out.println(dataSource);
        jt.update("delete from member", new MapSqlParameterSource(null));
        jt.update("insert into member(id, name, point) values(1, 'Spring', 0)",
                new MapSqlParameterSource(null));
        jt.update("insert into member(id, name, point) values(2, 'Book', 0)",
                new MapSqlParameterSource(null));

        SimpleJdbcCall sjc = new SimpleJdbcCall(dataSource).withFunctionName("find_name");
        String ret = sjc.executeFunction(String.class, 1);
        assertThat(ret, is("Spring"));
        assertThat(sjc.executeFunction(String.class, 2), is("Book"));
    }

    @Test
    public void jdbcTemplate() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(SimpleDao.class);
        SimpleDao dao = ac.getBean(SimpleDao.class);
        dao.deleteAll();

        //update()
        Map<String, Object> m = new HashMap<>();
        m.put("id", 1);
        m.put("name", "Spring");
        m.put("point", 3.5);
        dao.insert(m);
        dao.insert(new MapSqlParameterSource()
                .addValue("id", 2)
                .addValue("name", "Book")
                .addValue("point", 10.1));
        dao.insert(new Member(3, "Jdbc", 20.5));

        // query
        assertThat(dao.rowCount(), is(3));
        assertThat(dao.rowCount(5), is(2));
        assertThat(dao.rowCount(1), is(3));

        // queryForObject(Class)
        assertThat(dao.name(1), is("Spring"));
        assertThat(dao.point(1), is(3.5));

        // queryForObject(RowMapper)
        Member mret = dao.get(1);
        assertThat(mret.id, is(1));
        assertThat(mret.name, is("Spring"));
        assertThat(mret.point, is(3.5));

        // query(RowMapper)
        assertThat(dao.find(1).size(), is(3));
        assertThat(dao.find(5).size(), is(2));
        assertThat(dao.find(100).size(), is(0));

        // queryForMap
        Map<String, Object> mmap = dao.getMap(1);
        assertThat(mmap.get("id"), is(1));
        assertThat(mmap.get("name"), is("Spring"));
        assertThat(mmap.get("point"), is(3.5));

        // batchUpdates()
        Map<String, Object>[] paramMaps = new HashMap[2];
        paramMaps[0] = new HashMap<>();
        paramMaps[0].put("id", 1);
        paramMaps[0].put("name", "Spring2");
        paramMaps[1] = new HashMap<>();
        paramMaps[1].put("id", 2);
        paramMaps[1].put("name", "Book2");
        dao.updates(paramMaps);

        assertThat(dao.name(1), is("Spring2"));
        assertThat(dao.name(2), is("Book2"));

        dao.jdbcTemplate.batchUpdate("update member set name = :name where id = :id",
                new SqlParameterSource[] {
                    new MapSqlParameterSource().addValue("id", 1).addValue("name", "Spring3"),
                    new BeanPropertySqlParameterSource(new Member(2, "Book3", 0))
                });
        assertThat(dao.name(1), is("Spring3"));
        assertThat(dao.name(2), is("Book3"));
    }

    static class SimpleDao {
        NamedParameterJdbcTemplate jdbcTemplate;

        @Autowired
        public void init(DataSource dataSource) {
            System.out.println(dataSource);
            this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        }

        public void updates(Map<String, Object>[] paramsList) {
            this.jdbcTemplate.batchUpdate("update member set name = :name where id = :id", paramsList);
        }

        public Map<String, Object> getMap(int id) {
            return this.jdbcTemplate.queryForMap("select * from member where id = :id", new MapSqlParameterSource().addValue("id", id));
        }

        public List<Member> find(double point) {
            return this.jdbcTemplate.query("select * from member where point > :point",
                    new MapSqlParameterSource().addValue("point", point),
                    new BeanPropertyRowMapper<>(Member.class));
        }

        public Member get(int id) {
            return this.jdbcTemplate.queryForObject("select * from member where id = :id",
                    new MapSqlParameterSource().addValue("id", id),
                    new BeanPropertyRowMapper<>(Member.class));
        }

        public String name(int id) {
            return this.jdbcTemplate.queryForObject("select name from member where id = :id",
                    new MapSqlParameterSource().addValue("id", id),
                    String.class);
        }

        public double point(int id) {
            return this.jdbcTemplate.queryForObject("select point from member where id = :id",
                    new MapSqlParameterSource().addValue("id", id),
                    Double.class);
        }

        public int rowCount() {
            return this.jdbcTemplate.query("select count(*) from member",
                    new ResultSetExtractor<Integer>() {
                        @Override
                        public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                            rs.next();
                            return rs.getInt(1);
                        }
                    });
        }

        public int rowCount(double min) {
            return this.jdbcTemplate.queryForObject("select count(*) from member where point > :min",
                    new MapSqlParameterSource().addValue("min", min),
                    int.class);
        }

        public void deleteAll() {
            this.jdbcTemplate.update("delete from member",
                    new MapSqlParameterSource(null));
        }

        public void insert(Map map) {
            this.jdbcTemplate.update("insert into member(id, name, point) values(:id, :name, :point)",
                    new MapSqlParameterSource(map));
        }

        public void insert(Member m) {
            this.jdbcTemplate.update("insert into member(ID, NAME, POINT) values(:id, :name, :point)",
                    new BeanPropertySqlParameterSource(m));
        }

        public void insert(SqlParameterSource m) {
            this.jdbcTemplate.update("insert into member(id, name, point) values(:id, :name, :point)",
                    m);
        }

        @Bean
        public DataSource dataSource() {
            try {
                return new SimpleDriverDataSource(
                        new com.mysql.jdbc.Driver(),
                        "jdbc:mysql://localhost/springbook?characterEncoding=UTF-8",
                        "root",
                        "1234"
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void simpleJdbcInsertWithGeneratedKey() {
        jt.update("delete from member", new MapSqlParameterSource(null));

        MapSqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("id", 1)
                .addValue("name", "Spring")
                .addValue("point", 10.5);
        sji.execute(paramSource);

        Member m = new Member(2, "Jdbc", 3.3);
        sji.execute(new BeanPropertySqlParameterSource(m));

        JdbcTemplate jt = new JdbcTemplate(dataSource);
        List<Map<String, Object>> list = jt.queryForList("select * from member order by id");

        assertThat(list.size(), is(2));

        assertThat(list.get(0).get("id"), is(1));
        assertThat(list.get(0).get("name"), is("Spring"));
        assertThat(list.get(0).get("point"), is(10.5));

        assertThat(list.get(1).get("id"), is(2));
        assertThat(list.get(1).get("name"), is("Jdbc"));
        assertThat(list.get(1).get("point"), is(3.3));
    }



}
































