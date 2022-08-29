# JDBC 이해

## JDBC 등장 이유

애플리케이션을 개발할 때 중요한 데이터는 대부분 데이터베이스에 보관한다.

**클라이언트, 애플리케이션 서버, DB**
![image](https://user-images.githubusercontent.com/83503188/187165264-16156830-001f-4ced-a4ac-db55196979f4.png)

클라이언트가 애플리케이션 서버를 통해 데이터를 저장하거나 조회하면, 애플리케이션 서버는 다음 과정을 통해서 데이터베이스를 사용한다.

**애플리케이션 서버와 DB - 일반적인 사용법**
![image](https://user-images.githubusercontent.com/83503188/187165342-e650f32e-8fbd-4a54-bd3a-33eccf7ce607.png)
1. 커넥션 연결: 주로 TCP/IP를 사용해서 커넥션을 연결한다.
2. SQL 전달: 애플리케이션 서버는 DB가 이해할 수 있는 SQL을 연결된 커넥션을 통해 DB에 전달한다.
3. 결과 응답: DB는 전달된 SQL을 수행하고 그 결과를 응답한다. 애플리케이션 서버는 응답 결과를 활용한다.

**애플리케이션 서버와 DB - DB 변경** -> 과거
![image](https://user-images.githubusercontent.com/83503188/187165408-9e9ab540-5716-4bb0-a7e4-ec0f62d70520.png)

문제는 각각의 데이터베이스마다 커넥션을 연결하는 방법, SQL을 전달하는 방법, 그리고 결과를 응답 받는 방법이 모두 다르다는 점이다.

참고로 관계형 데이터베이스는 수십개가 있다.

여기에는 2가지 큰 문제가 있다.
1. 데이터베이스를 다른 종류의 데이터베이스로 변경하면 애플리케이션 서버에 개발된 데이터베이스 사용 코드도 함께 변경해야 한다.
2. 개발자가 각각의 데이터베이스마다 커넥션 연결, SQL 전달, 그리고 그 결과를 응답 받는 방법을 새로 학습해야 한다.

**이런 문제를 해결하기 위해 JDBC라는 자바 표준이 등장한다.**


> JDBC 표준 인터페이스 ?
>
> JDBC(Java Database Connectivity)는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API다.
> 
> JDBC는 데이터베이스에서 자료를 쿼리하거나 업데이트하는 방법을 제공한다. - 위키백과

**JDBC 표준 인터페이스**
![image](https://user-images.githubusercontent.com/83503188/187165730-b70a894d-2a6e-41df-8e7c-e2134fb1f1ee.png)

대표적으로 다음 3가지 기능을 표준 인터페이스로 정의해서 제공한다.
- `java.sql.Connection` - 연결
- `java.sql.Statement` - SQL을 담은 내용
- `java.sql.ResultSet` - SQL 요청 응답

자바는 이렇게 표준 인터페이스를 정의해두었다. 이제부터 개발자는 이 표준 인터페이스만 사용해서 개발하면 된다.

그런데 인터페이스만 있다고해서 기능이 동작하지는 않는다. 이 JDBC 인터페이스를 각각의 DB 벤더(회사)에서 자신의 DB에 맞도록 구현해서 라이브러리로 제공하는데, 이것을 JDBC 드라이버라 한다. 예를 들어서 MySQL DB에 접근할 수 있는 것은 MySQL JDBC 드라이버라 하고, Oracle DB에 접근할 수 있는 것은 Oracle JDBC 드라이버라 한다.

과거에는 MySQL, Oracle, ... 이 제공하는 인터페이스를 직접사용했다면 현재는 JDBC가 Connection(연결), Statement(SQL을 담은 내용), ResultSet(SQL 요청 응답)을 표준으로 정해뒀다.

따라서 과거에는 DB마다 사용법이 달라서 DB를 변경하게 된다면 코드를 교체해야했지만 현재는 JDBC 드라이버만 교체해주면된다.


**MySQL 드라이버 사용**
![image](https://user-images.githubusercontent.com/83503188/187166161-434464a1-9140-4160-9595-08905d666e32.png)


**Oracle 드라이버 사용**
![image](https://user-images.githubusercontent.com/83503188/187166176-a0aef28b-1346-4d87-acfe-801229482dc7.png)

### 정리

JDBC의 등장으로 다음 2가지 문제가 해결되었다.

1. 데이터베이스를 다른 종류의 데이터베이스로 변경하면 애플리케이션 서버의 데이터베이스 사용 코드도 함께 변경해야하는 문제
   - 애플리케이션 로직은 이제 JDBC 표준 인터페이스에만 의존한다. 따라서 데이터베이스를 다른 종류의 데이터베이스로 변경하고 싶으면 JDBC 구현 라이브러리만 변경하면 된다. 따라서 다른 종류의 데이터베이스로 변경해도 애플리케이션 서버의 사용 코드를 그대로 유지할 수 있다.
2. 개발자가 각각의 데이터베이스마다 커넥션 연결, SQL 전달, 그리고 그 결과를 응답 받는 방법을 새로 학습해야하는 문제
   - 개발자는 JDBC 표준 인터페이스 사용법만 학습하면 된다. 한번 배워두면 수십개의 데이터베이스에 모두 동일하게 적용할 수 있다.



> 참고 - 표준화의 한계
>
> JDBC의 등장으로 많은 것이 편리해졌지만, 각각의 데이터베이스마다 SQL, 데이터타입 등의 일부 사용법 다르다. ANSI SQL이라는 표준이 있기는 하지만 일반적인 부분만 공통화했기 때문에 한계가 있다. 대표적으로 실무에서 기본으로 사용하는 페이징 SQL은 각각의 데이터베이스마다 사용법이 다르다.
> 
> 결국 데이터베이스를 변경하면 JDBC 코드는 변경하지 않아도 되지만 SQL은 해당 데이터베이스에 맞도록 변경해야한다.
> 
> 참고로 JPA(Java Persistence API)를 사용하면 이렇게 각각의 데이터베이스마다 다른 SQL을 정의해야 하는 문제도 많은 부분 해결할 수 있다.

## JDBC와 최신 데이터 접근 기술

JDBC는 1997년에 출시될 정도로 오래된 기술이고, 사용하는 방법도 복잡하다. 그래서 최근에는 JDBC를 직접 사용하기 보다는 JDBC를 편리하게 사용하는 다양한 기술이 존재한다. 대표적으로 SQL Mapper와 ORM 기술로 나눌 수 있다.

**JDBC 직접 사용**

![image](https://user-images.githubusercontent.com/83503188/187166909-55205a30-eb2a-4be6-b548-93d9e894870e.png)

### SQL Mapper

![image](https://user-images.githubusercontent.com/83503188/187166977-a43f4d78-6e0a-45bf-be4e-1ebc28b83c0f.png)



- 장점: JDBC를 편리하게 사용하도록 도와준다.
  - SQL 응답 결과를 객체로 편리하게 변환해준다.
  - JDBC의 반복 코드를 제거해준다.
- 단점: 개발자가 SQL을 직접 작성해야한다.
- 대표 기술: 스프링 JdbcTemplate, MyBatis

### ORM 기술

![image](https://user-images.githubusercontent.com/83503188/187167226-dcaa9033-3466-41b3-91b2-110b22b98013.png)
- ORM 기술 

ORM은 객체를 관계형 데이터베이스 테이블과 매핑해주는 기술이다. 이 기술 덕분에 개발자는 반복적인 SQL을 직접 작성하지 않고, ORM 기술이 개발자 대신에 SQL을 동적으로 만들어 실행해준다. 추가로 각각의 데이터베이스마다 다른 SQL을 사용하는 문제도 중간에서 해결해준다.

- 대표 기술: JPA, 하이버네이트, 이클립스링크

JPA는 자바 진영의 ORM 표준 인터페이스이고, 이것을 구현한 것으로 하이버네이트와 이클립스 링크 등의 구현 기술이 있다.

### SQL Mapper vs. ORM 기술
SQL Mapper와 ORM 기술 둘다 각각 장단점이 있다.

쉽게 설명하자면 SQL Mapper는 SQL만 직접 작성하면 나머지 번거로운 일은 SQL Mapper가 대신 해결해준다. SQL Mapper는 SQL만 작성할 줄 알면 금방 배워서 사용할 수 있다.

ORM기술은 SQL 자체를 작성하지 않아도 되어서 개발 생산성이 매우 높아진다. 편리한 반면에 쉬운 기술은 아니므로 실무에서 사용하려면 깊이있게 학습해야 한다.

### 중요
> 이런 기술들도 내부에서는 모두 JDBC를 사용한다. 따라서 JDBC를 직접 사용하지는 않더라도, JDBC가 어떻게 동작하는지 기본 원리를 알아두어야 한다. 그래야 해당 기술들을 더 깊이있게 이해할 수 있고, 무엇보다 문제가 발생했을 때 근본적인 문제를 찾아서 해결할 수 있다 JDBC는 자바 개발자라면 꼭 알아두어야 하는 필수 기본 기술이다.


## 데이터베이스 연결

```java
package hello.jdbc.connection;

public abstract class ConnectionConst {
    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
```
```java
@Slf4j
public class DBConnectionUtil {

   
    public static Connection getConnection() {
        try {
           
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

}
```
- 데이터베이스에 연결하려면 JDBC가 제공하는 DriverManager.getConnection(..) 를 사용하면 된다.
- 이렇게 하면 라이브러리에 있는 데이터베이스 드라이버를 찾아서 해당 드라이버가 제공하는 커넥션을 반환해준다. 여기서는 H2 데이터베이스 드라이버가 작동해서 실제 데이터베이스와 커넥션을 맺고 그 결과를 반환해준다.

- DriverManager를 통해 Connection 구현체를 가져온다.
  - MySQL 드라이버라면 MySQL Connection, ...
  - 현재 H2 드라이버를 사용하므로 -> `org.h2.jdbc.JdbcConnection`을 반환한다.


![image](https://user-images.githubusercontent.com/83503188/187029921-c08b3426-be01-4c58-968c-b64204539b67.png)

### Connection Test
```java
@Slf4j
class DBConnectionUtilTest {
    @Test
    void connection() {
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}
```

### 실행 결과
```text
DBConnectionUtil - get connection=conn0: url=jdbc:h2:tcp://localhost/~/test
user=SA, class=class org.h2.jdbc.JdbcConnection
```

실행 결과를 보면 `class=class org.h2.jdbc.JdbcConnection` 부분을 확인할 수 있다. 이것이 바로 H2 데이터베이스 드라이버가 제공하는 H2 전용 커넥션이다. 물론 이 커넥션은 JDBC 표준 커넥션 인터페이스인 `java.sql.Connection` 인터페이스를 구현하고 있다.


### JDBC DriverManager 연결 이해

**JDBC 커넥션 인터페이스와 구현**
![image](https://user-images.githubusercontent.com/83503188/187168950-5ce566ff-a75b-4513-a335-0dfd7d63438e.png)
- JDBC는 `java.sql.Connection` 표준 커넥션 인터페이스를 정의한다.
- H2 데이터베이스 드라이버는 JDBC Connection 인터페이스를 구현한 `org.h2.jdbc.JdbcConnection` 구현체를 제공한다.

**DriverManager 커넥션 요청 흐름**

![image](https://user-images.githubusercontent.com/83503188/187169114-d99976bd-b8da-49b1-ad14-29273e6e4033.png)

JDBC가 제공하는 DriverManager 는 라이브러리에 등록된 DB 드라이버들을 관리하고, 커넥션을 획득하는 기능을 제공한다.

1. 애플리케이션 로직에서 커넥션이 필요하면 `DriverManager.getConnection()` 을 호출한다.
2. DriverManager 는 라이브러리에 등록된 드라이버 목록을 자동으로 인식한다. 이 드라이버들에게 순서대로 다음 정보를 넘겨서 커넥션을 획득할 수 있는지 확인한다.
   - URL: 예) jdbc:h2:tcp://localhost/~/test
   - 이름, 비밀번호 등 접속에 필요한 추가 정보
   - 여기서 각각의 드라이버는 URL 정보를 체크해서 본인이 처리할 수 있는 요청인지 확인한다. 예를 들어서 URL이 jdbc:h2 로 시작하면 이것은 h2 데이터베이스에 접근하기 위한 규칙이다. 따라서 H2 드라이버는 본인이 처리할 수 있으므로 실제 데이터베이스에 연결해서 커넥션을 획득하고 이 커넥션을 클라이언트에 반환한다. 반면에 URL이 jdbc:h2 로 시작했는데 MySQL 드라이버가 먼저 실행되면 이 경우 본인이 처리할 수 없다는 결과를 반환하게 되고, 다음 드라이버에게 순서가 넘어간다.
3. 이렇게 찾은 커넥션 구현체가 클라이언트에 반환된다.

**H2 데이터베이스 드라이버 라이브러리 등록**

```yaml
dependencies {
    ...
    runtimeOnly 'com.h2database:h2'
    ...
}
```

## JDBC 개발 - 등록

여기서는 JDBC를 사용해서 회원(Member) 데이터를 데이터베이스에 관리하는 기능을 개발해보자.

**Member**

```java
@Data
public class Member {

    private String memberId;
    private int money;

    public Member() {
    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}

```
```java
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null; // PreparedStatement 객체를 가지고 실제 DB에 쿼리를 실행한다.

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            // 파라미터 바인딩
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());

            pstmt.executeUpdate(); // 쿼리 실행, 반환값은 영향을 받은 row 수 
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);

        }
    }

    private void close(Connection con, Statement statement, ResultSet rs) {
        // 시작과 역순으로 close()
        // pstmt.close(); // Exception 이 발생하게 된다면 connection close가 호출안될 수 있다.
        // con.close();

        // 안전성을 위해 try catch로 전부 잡아서 다른 close에 영향을 끼치지 않아야한다.

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }

        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}

```

**커넥션 획득**
- `getConnection()` : 이전에 만들어둔 DBConnectionUtil 를 통해서 데이터베이스 커넥션을 획득한다.

**save() - SQL 전달**

- sql : 데이터베이스에 전달할 SQL을 정의한다. 여기서는 데이터를 등록해야 하므로 insert sql 을 준비했다.
- `con.prepareStatement(sql)` : 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비한다.
  - sql : insert into member(member_id, money) values(?, ?)"
  - `pstmt.setString(1, member.getMemberId())` : SQL의 첫번째 ? 에 값을 지정한다. 문자이므로 setString 을 사용한다.
  - `pstmt.setInt(2, member.getMoney())` : SQL의 두번째 ? 에 값을 지정한다. Int 형 숫자이므로 setInt 를 지정한다.
- `pstmt.executeUpdate()` : Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다. 참고로 executeUpdate() 은 int 를 반환하는데 영향받은 DB row 수를 반환한다. 여기서는 하나의 row를 등록했으므로 1을 반환한다.


**리소스 정리 - `close()`**
쿼리를 실행하고 나면 리소스를 정리해야 한다. 여기서는 Connection , PreparedStatement 를 사용했다. 리소스를 정리할 때는 항상 역순으로 해야한다.
Connection 을 먼저 획득하고 Connection 을 통해 PreparedStatement 를 만들었기 때문에 리소스를 반환할 때는 PreparedStatement 를 먼저 종료하고,
그 다음에 Connection 을 종료하면 된다. 참고로 여기서 사용하지 않은 ResultSet 은 결과를 조회할 때 사용한다.

> 참고
> 
> PreparedStatement 는 Statement 의 자식 타입인데, ? 를 통한 파라미터 바인딩을 가능하게 해준다.
> 
> 참고로 SQL Injection 공격을 예방하려면 PreparedStatement 를 통한 파라미터 바인딩 방식을 사용해야 한다.

## JDBC 개발 - 조회

**MemberRepositoryV0 - 회원 조회 추가**

```java
public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null; // PreparedStatement 객체를 가지고 실제 DB에 쿼리를 실행한다.
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            // 파라미터 바인딩
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery(); // 데이터를 변경할 때는 executeUpdate() 를 사용하지만, 데이터를 조회할 때는 executeQuery() 를 사용한다. executeQuery() 는 결과를 ResultSet 에 담아서 반환한다.

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);

        }
    }
```
**findById() - 쿼리 실행**

- sql : 데이터 조회를 위한 select SQL을 준비한다.
- `rs = pstmt.executeQuery()` 데이터를 변경할 때는 executeUpdate() 를 사용하지만, 데이터를 조회할 때는 executeQuery() 를 사용한다. executeQuery() 는 결과를 ResultSet 에 담아서 반환한다.

**ResultSet**
- ResultSet 은 다음과 같이 생긴 데이터 구조이다. 보통 select 쿼리의 결과가 순서대로 들어간다.
  - 예를 들어서 select member_id, money 라고 지정하면 member_id , money 라는 이름으로 데이터가 저장된다.
  - 참고로 select * 을 사용하면 테이블의 모든 컬럼을 다 지정한다.
- ResultSet 내부에 있는 커서( cursor )를 이동해서 다음 데이터를 조회할 수 있다.
- `rs.next()` : 이것을 호출하면 커서가 다음으로 이동한다. 참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에 `rs.next()` 를 최초 한번은 호출해야 데이터를 조회할 수 있다.
  - `rs.next()` 의 결과가 true 면 커서의 이동 결과 데이터가 있다는 뜻이다.
  - `rs.next()` 의 결과가 false 면 더이상 커서가 가리키는 데이터가 없다는 뜻이다.
- `rs.getString("member_id")` : 현재 커서가 가리키고 있는 위치의 member_id 데이터를 String 타입으로 반환한다.
- `rs.getInt("money")` : 현재 커서가 가리키고 있는 위치의 money 데이터를 int 타입으로 반환한다.

**ResultSet 결과 예시**

![image](https://user-images.githubusercontent.com/83503188/187171241-02cf9139-e83a-4666-9f58-d82fc03df7e0.png)

findById() 에서는 회원 하나를 조회하는 것이 목적이다. 따라서 조회 결과가 항상 1건이므로 while 대신에 if 를 사용한다. 다음 SQL을 보면 PK인 member_id 를 항상 지정하는 것을 확인할 수 있다.

## JDBC 개발 - 수정, 삭제

```java
public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null; // PreparedStatement 객체를 가지고 실제 DB에 쿼리를 실행한다.

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            // 파라미터 바인딩
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);

        }
    }
```
```java
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null; // PreparedStatement 객체를 가지고 실제 DB에 쿼리를 실행한다.

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            // 파라미터 바인딩
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);

        }
    }
```

```java
class MemberRepositoryV0Test {
    Logger log = LoggerFactory.getLogger(MemberRepositoryV0Test.class);

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV1", 10000);
        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);

        // why ? 성공 -> isEqualTo()는 `==`가 아닌 .eqauls() 메서드를 통해 비교하는데
        // 현재 Lombok의 @Data 어노테이션을 사용하여 eqauls가 재정의된 상태이다.
        // 따라서 현재는 값만 같으면 같은 객체로 인식하는 것
        log.info("member != findMember ", member == findMember); // false
        log.info("member.equals(findMember) ", member.equals(findMember)); // true
        assertThat(findMember).isEqualTo(member);

        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

    }
}
```

회원을 삭제한 다음 findById() 를 통해서 조회한다. 회원이 없기 때문에 NoSuchElementException 이 발생한다. assertThatThrownBy 는 해당 예외가 발생해야 검증에 성공한다.




