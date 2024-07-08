package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UncheckedAppTest {

    @Test
    void un_checked() {
        //예외를 터트린다. 
        Controller controller = new Controller();
        assertThatThrownBy(controller::request)
                .isInstanceOf(Exception.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e);
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient client = new NetworkClient();

        //처리 할 수 없기 때문에 밖으로 던진다.
        public void logic() {
            repository.call();
            client.call();
        }
    }

    //어떤 Network를 통해서 호출한다.
    static class NetworkClient {
        public void call() throws RuntimeSQLException {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    //체크 예외가 터지면 여기서 잡는다.
    //던질 때는 Runtime 예외로 변경해서 던진다.
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
