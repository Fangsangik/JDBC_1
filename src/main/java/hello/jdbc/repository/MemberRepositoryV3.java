package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        // DB에 전달할 SQL과 파라미터로 전달할 데이터들 준비
        PreparedStatement pstmt = null;


        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId()); //첫 ? 값에 값 대입
            pstmt.setInt(2, member.getMoney());// 두번째 ? 값에 값 대입
            pstmt.executeUpdate(); // 준비된 쿼리 실행, 영향 받은 row 수 만큼 반환
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
            //항상 finally로 반환 해야 한다.
        } finally {
            //외부에서 Connection을 끌어쓰는 것이기 때문에 닫아줘야 한다.
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?"; //회원 하나를 조회

        Connection con = null;
        PreparedStatement pstmt = null;

        //DB에 순서대로 값 들어가는 것을 나타낸다.
        //최초 한번 찾은 것을 기억 안하려고 함
        ResultSet rst = null;
        //내부에 있는 커서를 이동해서 다음 데이터 조회 가능
        //select 쿼리의 결과가 순서대로 들어간다.

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId); //파라미터 바인딩

            rst = pstmt.executeQuery();
            //select의 경우 executeQuery (결과를 담고 있는 것)

            //실제 데이터가 있는 부분 부터 조회
            if (rst.next()) {
                Member member = new Member();
                member.setMemberId(rst.getString("member_id"));
                member.setMoney(rst.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId =" + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rst);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money =? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money); // 첫번째 money
            pstmt.setString(2, memberId); // 두번째 member_id
            int rstSize = pstmt.executeUpdate(); // 결과값
            log.info("resultSize = {}", rstSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    //닫을때 확 꺼버리면 안된다.
    //동기화 매니저를 사용하면 JdbcUtils.closeConnection(con);을 사용하면 안된다.
    private void close(Connection con, Statement stmt, ResultSet rst) {
        JdbcUtils.closeResultSet(rst);
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, dataSource); //dataSource도 같이 넘겨 주어야 한다.
    }

    //DB Connection 획득
    private Connection getConnection() {
        //트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        //동기화된 transactionManager를 꺼내는 것
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get Connection ={} class ={}", con, con.getClass());
        return con;
    }
}
