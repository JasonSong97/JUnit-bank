# Junit Bank App

## Jpa LocalDateTime 자동으로 생성하는 법
- @EnableJpaAuditing (Main class)
- @EntityListeners(AuditingEntityListener.class) (Entity class)
```java
      @CreatedDate // insert
      @Column(nullable = false)
      private LocalDateTime createdAt;

      @LastModifiedBy // insert, update
      @Column(nullable = false)
      private LocalDateTime updatedAt;
```
## @Builder와 @NoArgsConstructor
- @NoArgsConstructor (User.class)
  - 스프링이 User 객체 생성할 때 빈생성자로 new를 하기 때문
```java
@Builder
     public User(Long id, String username, String password, String email, String fullname, UserEnum role,
               LocalDateTime createdAt, LocalDateTime updatedAt) {
          .
          .
          .
     }
```

## iframe

 > iframe(inline frame)은 HTML에서 다른 HTML 문서나 외부 웹페이지를 현재 문서 안에서 삽입하는 태그입니다. 즉, 하나의 HTML 문서 안에 다른 HTML 문서를 포함시켜 화면에 노출시킬 수 있습니다.

```html
<!DOCTYPE html>
<html>
  <head>
    <title>Example</title>
  </head>
  <body>
    <h1>Example Page</h1>
    <iframe src="https://www.example.com" width="100%" height="500px" frameborder="0" scrolling="auto"></iframe>
  </body>
</html>
```
태그를 사용하면 웹페이지 안에 다른 웹페이지를 삽입하여 보여줄 수 있습니다. 주로 광고나 외부 컨텐츠 등을 삽입하기 위해 사용됩니다. 예를 들어, 다른 웹사이트에서 제공하는 지도 서비스나 동영상을 현재 웹페이지에 삽입하여 보여줄 수 있습니다.

태그는 다음과 같은 속성을 가집니다.

- src: 삽입할 문서의 URL을 지정합니다.
- width, height: iframe의 너비와 높이를 지정합니다.
- frameborder: iframe 주변에 테두리를 표시할지 여부를 지정합니다.
- scrolling: iframe 안에서 스크롤바를 표시할지 여부를 지정합니다.

하지만,  태그를 남용하면 웹페이지의 성능에 악영향을 끼칠 수 있으며, 보안상의 문제도 발생할 수 있습니다. 따라서,  태그를 사용할 때에는 꼭 필요한 경우에만 사용하고, 보안을 고려하여 사용해야 합니다.

## cors

> CORS(Cross-Origin Resource Sharing)는 웹 브라우저에서 실행되는 스크립트가 다른 도메인의 자원에 접근하는 것을 제한하는 보안 기능입니다. 이는 보안상의 이유로 웹 브라우저에서만 적용되며, 서버 간 통신에서는 적용되지 않습니다.

자바스크립트로 요청되는 API 요청 전부 막는 것.

예를 들어, 도메인 A의 스크립트에서 도메인 B의 자원에 접근하려고 할 때 CORS가 적용되면 도메인 B의 서버는 스크립트가 접근할 수 있는 자원의 목록을 제한할 수 있습니다. 이를 통해 악의적인 스크립트의 도메인 간 공격을 방지할 수 있습니다.

CORS는 다음과 같은 방식으로 동작합니다.

스크립트에서 다른 도메인의 자원에 접근하려고 시도합니다.
브라우저는 요청 헤더에 Origin 필드를 추가하여 요청을 보냅니다.
서버는 요청 헤더의 Origin 필드를 확인하여, 접근이 허용되는 도메인인지 확인합니다.
서버는 접근이 허용되는 도메인일 경우, 응답 헤더에 Access-Control-Allow-Origin 필드를 추가하여 응답을 보냅니다.
즉, CORS를 적용하려면 서버에서 응답 헤더에 Access-Control-Allow-Origin 필드를 추가하여 스크립트가 접근할 수 있는 도메인을 설정해야 합니다. 이외에도, Access-Control-Allow-Headers 필드를 추가하여 접근이 허용되는 요청 헤더를 설정할 수 있습니다.

## Security 기본 설정
```java
@Bean // Jwt 서버만듬: 세션 사용 X
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          http.headers().frameOptions().disable(); // iframe 허용 X
          http.csrf().disable(); // postman 작동 안함
          http.cors().configurationSource(configurationSource()); // 자바스크립트 공격 막기
          // JessionId를 서버쪽에서관리안한다는의미                                                              
          http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
          // 리엑트, 엡으로 요청할 예정
          http.formLogin().disable(); // 화면 로그인 사용 X
          http.httpBasic().disable(); // httpBasic: 브라우저가 팝업창을 이용해서 인증을 진행한다.
          http.authorizeRequests()
                    .antMatchers("/api/s/**").authenticated()
                    .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN)
                    .anyRequest().permitAll();

          return http.build();
     }

     public CorsConfigurationSource configurationSource() { // 자바스크립트로 들어오는 요청
          CorsConfiguration configuration = new CorsConfiguration();
          configuration.addAllowedHeader("*");
          configuration.addAllowedMethod("*"); // GET POST PUT DELETE 모든 요청 허용
          configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용(프론트 엔드 IP만 허용 react)
          configuration.setAllowCredentials(true); // client 쪽에서 쿠키 요청 허용
          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          source.registerCorsConfiguration("/**", configuration); // 모든 요청에 해당 과정을 넣겠다.
          return source;
     }
```
## 정규표현식

https://github.com/codingspecialist/junit-bank-security-jwt/blob/master/class-note/regex/regex.pdf

## 서버에러
> 서버는 일관성있게 에러가 리턴되어야한다. 내가 모르는 에러가 프로트에게 전달되면 안된다. 내가 전부 제어할 수 있어야한다.
```java
@Getter
@RequiredArgsConstructor // RequiredArgsConstructor: 응답의 DTO는 1번 만들면 수정할 일이 없어서
public class ResponseDto<T> {
     private final Integer code; // -1, 1
     private final String msg;
     private final T data;
}
```

## 가짜 환경

```java
@ExtendWith(MockitoExtension.class) // Service 가짜 환경에서 실행
public class UserServiceTest extends DummyObject {

     @InjectMocks // 가짜
     private UserService userService;

     @Mock // 가짜
     private UserRepository userRepository;

     @Spy // 진짜 꺼내서 가짜에 넣는다.
     private BCryptPasswordEncoder passwordEncoder;
}
```

## 인증과 인가
![image](https://user-images.githubusercontent.com/107292103/235059177-20f31415-86cb-4ab1-9ccf-035554daa776.png)
![image](https://user-images.githubusercontent.com/107292103/235059228-c8e38215-22bf-4d50-a539-e97e0040953f.png)

### trasaction -> teardown
- ~ControllerTest (@Sql("classpath:db/teardown.sql") 추가)

```sql
SET REFERENTIAL_INTEGRITY FALSE; -- 비활성화
truncate table transaction_tb; -- truncate: 테이블 내용만 지우기
truncate table account_tb;
truncate table user_tb;
SET REFERENTIAL_INTEGRITY TRUE; -- 활성화
-- 컨트롤러 테스트 -> 다음과 같이
```

## 서비스 테스트
- 진짜 서비스 테스트를 하려면 내가 지금 무엇을 여기서 테스트 해야할지 명확히 구분하자.(책임을 분리 시키자)
- DTO를 만드는 책임 -> 서비스에 있지만!(서비스에서 DTO 검증 안할래! - Controller 테스트해볼 테니까)
- DB 관련된 것도 -> 서비스 것이 아니다
- DB 관련된 것을 조회했을 때, 그 값을 통해서 어떤 비지니스 로직이 흘러가는 것이 있으면 -> stub으로 정의해서 테스트를 해보면 된다.

## left outer join vs inner join
- Driving table, Driven table
- left outer join: null 포함 모든 것들을 가져오는 것
- inner join: null 제외하고 가져옴 -> 제외시킬 떄, 해당 row를 아예 가져오지 않음

![image](https://user-images.githubusercontent.com/107292103/235301316-685f3ee6-eed8-42bd-9c14-cc0ee83c7517.png)

## 최종정리

- Controller Test
  - @Transactional 걸지 말자
- 예시
```java
@Sql("classpath:db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class AccountControllerTest extends DummyObject {
     @Autowired
     private MockMvc mvc;
     @Autowired
     private ObjectMapper om;
     @Autowired
     private EntityManager em;
     // 필요한 것 (+ Repository)
     .
     .
     .
     @BeforeEach
     public void setUp() {
          dataSetting();
          em.clear();
     }

     @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION) // 인증이 필요하면 넣기
     @Test
     public void _test() throws Exception {
          // given
    

          // when
          ResultActions resultActions = mvc
                    .perform(post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + responseBody);

          // then
          resultActions.andExpect(status().isCreated()); // 이렇게 값을 바로 보지말고 직접 -> when에서 눈으로 확인을 하자
          
          // OR 만약 DTO 테스트를 서비스에서 하지 않을 거면, 다음과 같이 값들을 넣고 확인을 하자
          resultActions.andExpect(jsonPath("$.data.transactions[0].balance").value(900L));
          resultActions.andExpect(jsonPath("$.data.transactions[1].balance").value(800L));
          resultActions.andExpect(jsonPath("$.data.transactions[2].balance").value(700L));
          resultActions.andExpect(jsonPath("$.data.transactions[3].balance").value(800L));

     }
}
```
- Repository Test
  - DummyObject가 있으면 편하다
```java
@ActiveProfiles("test")
@DataJpaTest // DB관련 Bean이 올라온다.
public class TransactionRepositoryImplTest extends DummyObject {
     .
     .
     .
     @BeforeEach
     public void setUp() {
          autoincrementReset(); // 필수
          dataSetting();
          em.clear(); // 레포 테스트 필수
     }

     .
     .
     .
     private void autoincrementReset() {
          em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
          em.createNativeQuery("ALTER TABLE account_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
          em.createNativeQuery("ALTER TABLE transaction_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
     }
}
```
- Service Test
  - 테스트는 기능별로 쪼개서 그리고 레이어별로 해야하기 때문에 Service만 띄우고 하자
```java
@ExtendWith(MockitoExtension.class) // Service 가짜 환경에서 실행
public class UserServiceTest extends DummyObject {
     @InjectMocks // 가짜
     private UserService userService;

     @Mock // 가짜
     private UserRepository userRepository;

     @Spy // 진짜 꺼내서 가짜에 넣는다.
     private BCryptPasswordEncoder passwordEncoder;
}
```
## API 화면
- s: 인증이 필요한 것들

### 구성

### 회원가입

### 로그인(토큰)

### 계좌등록

### 계좌목록보기 유저별 성공

### 계좌삭제

### 계좌 입금

### 계좌 출금

### 계좌 이체

### 계좌상세보기

### 입출금목록보기