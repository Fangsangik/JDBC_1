package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedAppTest {

    @Test
    void checked() {
        //예외를 터트린다.
        Controller controller = new Controller();
        assertThatThrownBy(controller::request)
                .isInstanceOf(Exception.class);
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient client = new NetworkClient();

        //처리 할 수 없기 때문에 밖으로 던진다.
        public void logic() throws SQLException, ConnectException {
            repository.call();
            client.call();
        }
    }

    //어떤 Network를 통해서 호출한다.
    static class NetworkClient {
        public void call() throws ConnectException {
            throw new ConnectException("연결 실패");
        }
    }

    static class Controller {
        Service service = new Service();

        //처리 할 수 없기 때문에 밖으로 던진다.
        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }

    static class Repository {
        public void call() throws SQLException {
            throw new SQLException("ex");
        }
    }
}
