package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedTest {

    /**
     * Exception을 상속받은 예외는 체크 예외가 된다
     */

    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    @Test
    void checked_catch() {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw() {
        Service service = new Service();
        Assertions.assertThatThrownBy(service::callThrow)
                .isInstanceOf(MyCheckedException.class);
    }

    static class Service {

        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch() {
            try {
                repository.call(); //Compiler가 체크
            } catch (MyCheckedException e) {
                log.info("예외처리, message ={}", e.getMessage(), e);
            }
        }

        /**
         * 예외를 던져서 처리하는 코드
         * 체크 예외는 예외를 잡지 않고 밖으로 던지려면 throws 예외를 메서드에 필수로 선언
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {

        //체크 예외는 밖으로 던지는 것을 선언
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
