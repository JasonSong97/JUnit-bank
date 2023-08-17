package shop.mtcoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.core.RestDoc;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@DisplayName("트랜젝션 API")
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8081)
@ActiveProfiles("test") // 테스트 모드
@Sql("classpath:db/teardown.sql") // BeforeEach 직전 실행
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class TransactionContollrerTest extends RestDoc {

     private DummyObject dummy = new DummyObject();

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

     @DisplayName("입출금목록보기 성공")
     @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
     @Test
     public void find_transaction_list_success_test() throws Exception {
          // given
          Long number = 1111L;
          String gubun = "ALL";
          String page = "0";

          // when
          ResultActions resultActions = mvc
                    .perform(get("/api/s/account/" + number + "/transaction").param("gubun", gubun)
                              .param("page", page));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          System.out.println("테스트 : " + responseBody);

          // then
          resultActions.andExpect(jsonPath("$.data.transactions[0].balance").value(900L));
          resultActions.andExpect(jsonPath("$.data.transactions[1].balance").value(800L));
          resultActions.andExpect(jsonPath("$.data.transactions[2].balance").value(700L));
          resultActions.andExpect(jsonPath("$.data.transactions[3].balance").value(800L));
          resultActions.andDo(document.document(
                    requestHeaders(headerWithName("Authorization").optional().description("인증헤더 Bearer token 필수"))));
          resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);
     }

     private void dataSetting() {
          User ssar = userRepository.save(dummy.newUser("ssar", "쌀"));
          User cos = userRepository.save(dummy.newUser("cos", "코스,"));
          User love = userRepository.save(dummy.newUser("love", "러브"));
          User admin = userRepository.save(dummy.newUser("admin", "관리자"));

          Account ssarAccount1 = accountRepository.save(dummy.newAccount(1111L, ssar));
          Account cosAccount = accountRepository.save(dummy.newAccount(2222L, cos));
          Account loveAccount = accountRepository.save(dummy.newAccount(3333L, love));
          Account ssarAccount2 = accountRepository.save(dummy.newAccount(4444L, ssar));

          Transaction withdrawTransaction1 = transactionRepository
                    .save(dummy.newWithdrawTransaction(ssarAccount1, accountRepository));
          Transaction depositTransaction1 = transactionRepository
                    .save(dummy.newDepositTransaction(cosAccount, accountRepository));
          Transaction transferTransaction1 = transactionRepository
                    .save(dummy.newTransferTransaction(ssarAccount1, cosAccount, accountRepository));
          Transaction transferTransaction2 = transactionRepository
                    .save(dummy.newTransferTransaction(ssarAccount1, loveAccount, accountRepository));
          Transaction transferTransaction3 = transactionRepository
                    .save(dummy.newTransferTransaction(cosAccount, ssarAccount1, accountRepository));

     }
}