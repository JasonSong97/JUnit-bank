package shop.mtcoding.bank.web;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
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
     private TransactionRepository transactionRepository;
     @Autowired
     private AccountRepository accountRepository;
     @Autowired
     private UserRepository userRepository;

     @BeforeEach
     public void setUp() {
          dataSetting();
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

     @Test
     public void depositAccount_test() throws Exception {
          // given
          AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
          accountDepositRequestDto.setNumber(1111L);
          accountDepositRequestDto.setAmount(100L);
          accountDepositRequestDto.setGubun("DEPOSIT");
          accountDepositRequestDto.setTel("01088887777");

          String requestBody = om.writeValueAsString(accountDepositRequestDto);
          System.out.println("테스트 : " + requestBody);

          // when
          ResultActions resultActions = mvc
                    .perform(post("/api/account/deposit").content(requestBody).contentType(MediaType.APPLICATION_JSON));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + responseBody);

          // then
          resultActions.andExpect(status().isCreated()); // @JsonIgnore 조절
     }

     @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
     @Test
     public void withdrawAccount_test() throws Exception {
          // given
          AccountWithdrawRequestDto accountWithdrawRequestDto = new AccountWithdrawRequestDto();
          accountWithdrawRequestDto.setNumber(1111L);
          accountWithdrawRequestDto.setPassword(1234L);
          accountWithdrawRequestDto.setAmount(100L);
          accountWithdrawRequestDto.setGubun("WITHDRAW");

          String requestBody = om.writeValueAsString(accountWithdrawRequestDto);
          System.out.println("테스트 : " + requestBody);

          // when
          ResultActions resultActions = mvc
                    .perform(post("/api/s/account/withdraw").content(requestBody)
                              .contentType(MediaType.APPLICATION_JSON));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + responseBody);

          // then
          resultActions.andExpect(status().isCreated());
     }

     @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
     @Test
     public void transferAccount_test() throws Exception {
          // given
          AccountTransferRequestDto accountTransferRequestDto = new AccountTransferRequestDto();
          accountTransferRequestDto.setWithdrawNumber(1111L);
          accountTransferRequestDto.setDepositNumber(2222L);
          accountTransferRequestDto.setWithdrawPassword(1234L);
          accountTransferRequestDto.setAmount(100L);
          accountTransferRequestDto.setGubun("TRANSFER");

          String requestBody = om.writeValueAsString(accountTransferRequestDto);
          System.out.println("테스트 : " + requestBody);

          // when
          ResultActions resultActions = mvc
                    .perform(post("/api/s/account/transfer").content(requestBody)
                              .contentType(MediaType.APPLICATION_JSON));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + responseBody);

          // then
          resultActions.andExpect(status().isCreated());
     }

     @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
     @Test
     public void findDetailAccount_test() throws Exception {
          // given
          Long number = 1111L;
          String page = "0";

          // when
          ResultActions resultActions = mvc
                    .perform(get("/api/s/account/" + number)
                              .param("page", page));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + responseBody);

          // then
          // resultActions.andExpect(jsonPath("$.data.transactions[0].balance").value(900L));
          // resultActions.andExpect(jsonPath("$.data.transactions[1].balance").value(800L));
          // resultActions.andExpect(jsonPath("$.data.transactions[2].balance").value(700L));
          // resultActions.andExpect(jsonPath("$.data.transactions[3].balance").value(800L));
     }

     private void dataSetting() {
          User ssar = userRepository.save(newUser("ssar", "쌀"));
          User cos = userRepository.save(newUser("cos", "코스,"));
          User love = userRepository.save(newUser("love", "러브"));
          User admin = userRepository.save(newUser("admin", "관리자"));

          Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
          Account cosAccount = accountRepository.save(newAccount(2222L, cos));
          Account loveAccount = accountRepository.save(newAccount(3333L, love));
          Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

          Transaction withdrawTransaction1 = transactionRepository
                    .save(newWithdrawTransaction(ssarAccount1, accountRepository));
          Transaction depositTransaction1 = transactionRepository
                    .save(newDepositTransaction(cosAccount, accountRepository));
          Transaction transferTransaction1 = transactionRepository
                    .save(newTransferTransaction(ssarAccount1, cosAccount, accountRepository));
          Transaction transferTransaction2 = transactionRepository
                    .save(newTransferTransaction(ssarAccount1, loveAccount, accountRepository));
          Transaction transferTransaction3 = transactionRepository
                    .save(newTransferTransaction(cosAccount, ssarAccount1, accountRepository));
     }
}
