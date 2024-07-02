package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {
    private MemberRepositoryV1 memberRepositoryV1;

    @BeforeEach
    public void beforeEach(){
        //기본 DriverManger - 항상 새로운 커넥션 획득
        //DriverManager dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        //히카리 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        memberRepositoryV1 = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {
        log.info("start");

        Member member = new Member("memberV0", 10000);
        memberRepositoryV1.save(member);

        Member memberId = memberRepositoryV1.findById(member.getMemberId());
        assertThat(memberId).isNotNull();

        memberRepositoryV1.update(memberId.getMemberId(), 20000);
        Member updatedMember = memberRepositoryV1.findById(memberId.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        memberRepositoryV1.delete(member.getMemberId());
        assertThatThrownBy(() -> memberRepositoryV1.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}