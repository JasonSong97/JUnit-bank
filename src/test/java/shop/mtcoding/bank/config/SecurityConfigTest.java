package shop.mtcoding.bank.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc // Mock(가짜) 환경에 MockMvc가 등록
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) // 가짜 환경에서 test
public class SecurityConfigTest {

      @Autowired // 가짜 환경에 등록된 MockMvc를 DI 함
      private MockMvc mvc;

      @Test
      public void authentication_test() throws Exception {
            // given

            // when
            ResultActions resultActions = mvc.perform(get("/api/s/hello"));
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
            System.out.println("테스트 : " + responseBody);
            System.out.println("테스트 : " + httpStatusCode);
            // then

      }

      @Test
      public void authorizetion_test() throws Exception {
            // given

            // when

            // then

      }
}
