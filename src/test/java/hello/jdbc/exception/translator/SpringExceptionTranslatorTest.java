package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SpringExceptionTranslatorTest {
    DataSource dataSource;

    @BeforeEach
    void init() {
       dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

    }

    //문법 오류
    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammar";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();

        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);
            int errorCode = e.getErrorCode();
            log.info("errorCode ={}", errorCode);
            log.info("error", e);
        }
    }

    @Test
    //BadSqlGrammarException 터진다.
    void exceptionTranslator() {
        String sql = "select bad grammar";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeQuery();

        } catch (SQLException e) {
            assertThat(e.getErrorCode()).isEqualTo(42122);

            //Repository에서 예외 변환을 하는 것이 아니라
            //여기서 예외 변환 실행 (Spring 예외 추상화 실행)
            SQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);

            //dataException 반환
            DataAccessException rstEx = exTranslator.translate("select", sql, e);

            log.info("resultEx", rstEx);
            assertThat(rstEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }
    }
}
