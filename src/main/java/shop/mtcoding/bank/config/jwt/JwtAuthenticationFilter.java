package shop.mtcoding.bank.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

     private final Logger log = LoggerFactory.getLogger(getClass());
     private AuthenticationManager authenticationManager;

     public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
          super(authenticationManager);
          setFilterProcessesUrl("/api/login"); // /login -> /api/login
          this.authenticationManager = authenticationManager;
     }

     @Override // 동작 시점 : POST, /login
     public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
               throws AuthenticationException {
          log.debug("디버그 : attemptAuthentication 호출됨");
          try {
               ObjectMapper om = new ObjectMapper();
               LoginRequestDto loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
               UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                         loginRequestDto.getUsername(), loginRequestDto.getPassword());
               Authentication authentication = authenticationManager.authenticate(authenticationToken);
               return authentication; // successfulAuthentication 호출
          } catch (Exception e) {
               // unsuccessfulAuthentication 호출
               throw new InternalAuthenticationServiceException(e.getMessage());
          }
     }

     @Override
     protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
               AuthenticationException failed) throws IOException, ServletException {
          CustomResponseUtil.fail(response, "로그인 실패", HttpStatus.UNAUTHORIZED);
     }

     @Override
     protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
               FilterChain chain, Authentication authResult) throws IOException, ServletException {
          log.debug("디버그 : successfulAuthentication 호출됨"); // 로그인 O, 세션이 만들어진 것
          LoginUser loginUser = (LoginUser) authResult.getPrincipal();
          String jwtToken = JwtProcess.create(loginUser);
          response.addHeader(JwtValueObject.HEADER, jwtToken);
          LoginResponseDto loginResponseDto = new LoginResponseDto(loginUser.getUser());
          CustomResponseUtil.success(response, loginResponseDto);
     }
}
