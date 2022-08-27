package dev.yoon.springdb_1.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static dev.yoon.springdb_1.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    /**
     * 데이터베이스에 연결하려면 JDBC가 제공하는 DriverManager.getConnection(..) 를 사용하면 된다.
     * 이렇게 하면 라이브러리에 있는 데이터베이스 드라이버를 찾아서 해당 드라이버가 제공하는 커넥션을 반환해준다. 여기서는 H2 데이터베이스 드라이버가 작동해서 실제 데이터베이스와 커넥션을 맺고 그 결과를 반환해준다.
     */
    public static Connection getConnection() {
        try {
            // DriverManager를 통해 Connection 구현체를 가져온다.
            // MySQL 드라이버라면 MySQL Connection, ...
            // 현재 H2 드라이버를 사용하므로 -> `org.h2.jdbc.JdbcConnection`을 반환한다.
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
