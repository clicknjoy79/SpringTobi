package springbook.learningtest.datasource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * RoutingDataSource는 스키마가 같은 DB 사이의 스키마가 동일한 경우에만 적용된다.
 * 예를 들어 운영 DB 와 테스트 DB 를 바꿔가면서 사용해야 할 경우에 쓰인다. 스키마가 다르면 적용 불가능
 * 집에서 테스트
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RoutingDataSourceTest {
    @Autowired UserDao userDao;

    @Test
    @Ignore
    public void rountingTest() throws SQLException {
        userDao.addUser(new User(123, "Spring123")); // 쓰기 작업이므로 masterDataSource를 사용한다.
        try {
            User user1 = userDao.searchUser(1); // readonly 스키마에 존재하는 데이터이므로 없어야 된다.
            assertThat(user1.getId(), is(1));
            assertThat(user1.getName(), is("Toby"));
        } catch (EmptyResultDataAccessException e) {
            System.out.println(e.getMessage());
            System.out.println("데이터가 존재하지 않음");
        }

        User user2 = userDao.searchUser(123);
        assertThat(user2.getId(), is(123));
        assertThat(user2.getName(), is("Spring123"));
    }


    @Configuration
    @EnableTransactionManagement
    static class AppConfig {
        @Bean DataSource dataSource() {
            Map<Object, Object> dataSourceMap = new HashMap<>();
            dataSourceMap.put("READWRITE", masterDataSource());
            dataSourceMap.put("READONLY", readOnlyDataSource());
            ReadOnlyRoutingDataSource dataSource = new ReadOnlyRoutingDataSource();

            dataSource.setTargetDataSources(dataSourceMap);
            dataSource.setDefaultTargetDataSource(masterDataSource());
            return dataSource;
        }

        @Bean DataSource masterDataSource() {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
            dataSource.setUrl("jdbc:mysql://localhost/springbook?characterEncoding=UTF-8");
            dataSource.setUsername("root");
            dataSource.setPassword("1234");
            return dataSource;
        }

        // readonly DataBase 생성후 user(id - > int , name - > varchar) 테이블을 만든다.
        // id와 name에 1과 Toby를 주입한다.
        @Bean DataSource readOnlyDataSource() {
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
            dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
            // 이러면 스키마가 다르므로 적용 안됨........
            dataSource.setUrl("jdbc:mysql://localhost/readonly?characterEncoding=UTF-8");
            dataSource.setUsername("root");
            dataSource.setPassword("1234");
            return dataSource;
        }

        @Bean PlatformTransactionManager platformTransactionManager() {
            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
            transactionManager.setDataSource(dataSource());
            return transactionManager;
        }

        @Bean UserDao userDao() {
            return new UserDao();
        }
    }


    static class UserDao {
        JdbcTemplate jdbcTemplate;

        @Resource
        void init(DataSource dataSourceRouter) { jdbcTemplate = new JdbcTemplate(dataSourceRouter);}

        @Transactional
        public void addUser(User user) {
            jdbcTemplate.update("insert into USER(id, name) VALUES (?, ?)", user.getId(), user.getName());
        }

        @Transactional(readOnly = true)
        public User searchUser(int id) {
            User user = jdbcTemplate.queryForObject("select * from user where id = ?", new Object[] { id }, new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    user.setId(id);
                    user.setName(name);
                    return user;
                }
            });
            return user;

        }
    }

    static class User {
        int id;
        String name;
        User(int id, String name) { this.id = id; this.name = name; }
        User() {}

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}












































