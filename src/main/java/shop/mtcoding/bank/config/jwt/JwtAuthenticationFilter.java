package shop.mtcoding.bank.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.user.UserRequestDto.LoginRequestDto;
import shop.mtcoding.bank.dto.user.UserResponseDto.LoginResponseDto;
import shop.mtcoding.bank.util.CustomResponseUtil;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

     private AuthenticationManager authenticationManager;

     public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
          super(authenticationManager);
          setFilterProcessesUrl("/api/login");// /login -> /api/login
          this.authenticationManager = authenticationManager;
     }

     // POST: /login -> /api/login -> 동작
     @Override
     public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
               throws AuthenticationException {

          try {
               ObjectMapper om = new ObjectMapper();
               LoginRequestDto loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);

               // 강제 로그인
               UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                         loginRequestDto.getUsername(), loginRequestDto.getPassword());

               // UserDetailsService의 loadUserByUsername 호출
               // JWT를 쓴다고 하더라도, 컨트롤러 진입을 하면 시큐리티에 권한체크, 인증체크의 도움을 받을 수 있게 세션을 만든다.
               // 이 세션의 유효기간은 request하고, response하면 끝!
               Authentication authentication = authenticationManager.authenticate(authenticationToken);
               return authentication;
          } catch (Exception e) {
               // authenticationEntryPoint에 걸린다.
               throw new InternalAuthenticationServiceException(e.getLocalizedMessage());
          }
     }

     // return authentication 잘 작동하면 해당 메소드 호출
     @Override
     protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
               FilterChain chain, Authentication authResult) throws IOException, ServletException {

          LoginUser loginUser = (LoginUser) authResult.getPrincipal();
          String jwtToken = JwtProcess.create(loginUser);
          response.addHeader(JwtVO.HEADER, jwtToken);

          LoginResponseDto loginResponseDto = new LoginResponseDto(loginUser.getUser());
          CustomResponseUtil.success(response, loginResponseDto); // 응답
     }

}
