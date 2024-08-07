package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {

        try {
            //데이터베이스에 연결하기 위함
            //getConnection -> connection을 가져옴 (h2)
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get Connection ={}, class ={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
