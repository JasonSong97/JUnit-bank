package shop.mtcoding.bank.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class JwtProcessTest {

     @Test
     public void create_test() throws Exception {
          // given
          User user = User.builder().id(1L).role(UserEnum.ADMIN).build();
          LoginUser loginUser = new LoginUser(user); // id + role

          // when
          String jwtToken = JwtProcess.create(loginUser);
          System.out.println("테스트 : " + jwtToken);

          // then
          assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
     }

     @Test
     public void verify_test() throws Exception {
          // given
          String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5rIiwicm9sZSI6IkFETUlOIiwiaWQiOjEsImV4cCI6MTY4MzIxODMzOX0.J8sbHOswQLtFApcOGnPmhKS43vC3LQwHnkZ90oQUCFTCa2GBA_kZkrzS61mFXSb9DKqH2bPy3LNcwmzT5Hg10g";

          // when
          LoginUser loginUser = JwtProcess.verify(jwtToken);
          System.out.println("테스트 : " + loginUser.getUser().getId());
          System.out.println("테스트 : " + loginUser.getUser().getRole().name());

          // then
          assertThat(loginUser.getUser().getId()).isEqualTo(1L);
          assertThat(loginUser.getUser().getRole()).isEqualTo(UserEnum.ADMIN);
     }
}
