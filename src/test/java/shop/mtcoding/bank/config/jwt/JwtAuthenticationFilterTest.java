package shop.mtcoding.bank.config.jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto.LoginRequestDto;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class JwtAuthenticationFilterTest extends DummyObject {

     @Autowired
     private ObjectMapper om; // <-- @SpringBootTest(webEnvironment = WebEnvironment.MOCK)
     @Autowired
     private MockMvc mvc; // @AutoConfigureMockMvc이 있어야 WebEnvironment.MOCK 환경에 MockMvc 가 들어간다.(DI)
     @Autowired
     private UserRepository userRepository;

     @BeforeEach
     public void setUp() throws Exception {
          User user = userRepository.save(newUser("ssar", "쌀"));
     }

     @Test
     public void successfulAuthentication_test() throws Exception {
          // given
          LoginRequestDto loginRequestDto = new LoginRequestDto();
          loginRequestDto.setUsername("ssar");
          loginRequestDto.setPassword("1234");
          String requestBody = om.writeValueAsString(loginRequestDto); // json 파싱
          System.out.println("테스트 : " + requestBody);

          // when
          ResultActions resultActions = mvc
                    .perform(post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
          System.out.println("테스트 : " + responseBody);
          System.out.println("테스트 : " + jwtToken);

          // then
          resultActions.andExpect(status().isOk());
          assertNotNull(jwtToken); // jwtToken null 아니길 기대한다.
          assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
          resultActions.andExpect(jsonPath("$.data.username").value("ssar"));
     } // 롤백

     @Test
     public void unsuccessfulAuthentication_test() throws Exception {
          // given
          LoginRequestDto loginRequestDto = new LoginRequestDto();
          loginRequestDto.setUsername("ssar");
          loginRequestDto.setPassword("12345");
          String requestBody = om.writeValueAsString(loginRequestDto); // json 파싱
          System.out.println("테스트 : " + requestBody);

          // when
          ResultActions resultActions = mvc
                    .perform(post("/api/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
          String responseBody = resultActions.andReturn().getResponse().getContentAsString();
          String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
          System.out.println("테스트 : " + responseBody);
          System.out.println("테스트 : " + jwtToken);

          // then
          resultActions.andExpect(status().isUnauthorized());
     }
}
