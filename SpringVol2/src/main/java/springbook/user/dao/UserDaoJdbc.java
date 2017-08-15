package springbook.user.dao;

import javax.sql.DataSource;

public class UserDaoJdbc {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
