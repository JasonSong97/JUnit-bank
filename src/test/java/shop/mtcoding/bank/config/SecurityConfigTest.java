package shop.mtcoding.bank.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.assertj.core.api.Assertions;
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

      // 서버는 일관성있게 에러가 리턴되어야 한다.
      // 백엔드가 모르는 에러가 프론트한테 날라가지 않게, 내가 직접 다 제어하자.
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
            Assertions.assertThat(httpStatusCode).isEqualTo(401);
      }

      @Test
      public void authorizetion_test() throws Exception {
            // given

            // when
            ResultActions resultActions = mvc.perform(get("/api/s/hello"));
            String responseBody = resultActions.andReturn().getResponse().getContentAsString();
            int httpStatusCode = resultActions.andReturn().getResponse().getStatus();
            System.out.println("테스트 : " + responseBody);
            System.out.println("테스트 : " + httpStatusCode);

            // then
            Assertions.assertThat(httpStatusCode).isEqualTo(401);
      }
}
