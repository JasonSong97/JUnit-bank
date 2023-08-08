package shop.mtcoding.bank.web;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@ActiveProfiles("test") // 테스트 모드
@Sql("classpath:db/teardown.sql") // BeforeEach 직전 실행
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class AccountControllerTest extends DummyObject {

     @Autowired
     private MockMvc mvc;
     @Autowired
     private ObjectMapper om;
     @Autowired
     private EntityManager em;
     @Autowired
     private UserRepository userRepository;
     @Autowired
     private AccountRepository accountRepository;

     @BeforeEach
     public void setUp() {
          User ssar = userRepository.save(newUser("ssar", "쌀"));
          User cos = userRepository.save(newUser("cos", "코스"));

          Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
          Account cosAccount1 = accountRepository.save(newAccount(2222L, cos));

          em.clear();
     }

     @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
     @Test
     public void saveAccount_test() throws Exception {
          // given
          AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto();
          accountSaveRequestDto.setNumber(9999L);
          accountSaveRequestDto.setPassword(1234L);
          String requestBody = om.writeValueAsString(accountSaveRequestDto);
          System.out.println("테스트 : " + requestBody);

          // when
          ResultActions resultActions = mvc.perform(post("/api/s/account")
                    .content(requestBody).contentType(MediaType.APPLICATION_JSON));
          String resopnseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + resopnseBody);

          // then
          resultActions.andExpect(status().isCreated());
     }

     @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
     @Test
     public void findUserAccount_test() throws Exception {
          // given

          // when
          ResultActions resultActions = mvc.perform(get("/api/s/account/login-user"));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + responseBody);

          // then
          resultActions.andExpect(status().isOk());
     }

     @WithUserDetails(value = "cos", setupBefore = TestExecutionEvent.TEST_EXECUTION)
     @Test
     public void deleteAccount_test() throws Exception {
          // given
          Long number = 2222L;

          // when
          ResultActions resultActions = mvc.perform(delete("/api/s/account/" + number));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + responseBody);

          // then, JUnit 테스트에서 delete 쿼리는 DB관련으로 가장 마지막에 실행되면 발동안함.
          assertThrows(CustomApiException.class, () -> accountRepository.findByNumber(number).orElseThrow(
                    () -> new CustomApiException("계좌를 찾을 수 없습니다. ")));
     }
}
