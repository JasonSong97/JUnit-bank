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

// UsernamePasswordAuthenticationFilter 들어가보기
// filter: 컨트롤러 가기전에 발동 + filter 등록!
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter { // 인증 필터
     private final Logger log = LoggerFactory.getLogger(getClass());

     private AuthenticationManager authenticationManager;

     public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
          super(authenticationManager);
          setFilterProcessesUrl("/api/login");
          this.authenticationManager = authenticationManager;
     }

     // 동작시점: (POST) /api/login
     @Override
     public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
               throws AuthenticationException {
          log.debug("디버그 : attemptAuthentication 호출됨");
          try {
               ObjectMapper om = new ObjectMapper();
               LoginRequestDto loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);

               // 강제 로그인
               UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                         loginRequestDto.getUsername(), loginRequestDto.getPassword());

               // UserDetailsService의 loadUserByUsername 호출(강제 로그인)
               // 강제 로그인 이유: Jwt를 사용해도, 컨트롤러 진입하면 시큐리티의 권한체크, 인증체크 도움을 받을 수 있게 세션을 만든다.
               // "/api/s/**", "/api/admin/**", ...
               // 이 세션의 유효기간: request, response하면 끝 -> successfulAuthentication ->
               // CustomResponseUtil.success(response, loginResponseDto);
               Authentication authentication = authenticationManager.authenticate(authenticationToken);
               return authentication;
          } catch (Exception e) {
               // unsuccessfulAuthentication 호출
               throw new InternalAuthenticationServiceException(e.getMessage());
          }
     }

     // 작동시점: 로그인 실패
     @Override
     protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
               AuthenticationException failed) throws IOException, ServletException {
          CustomResponseUtil.fail(response, "로그인 실패", HttpStatus.UNAUTHORIZED);

     }

     // 작동시점: return authentication;
     @Override
     protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
               FilterChain chain, Authentication authResult) throws IOException, ServletException {
          log.debug("디버그 : successfulAuthentication 호출됨");
          LoginUser loginUser = (LoginUser) authResult.getPrincipal();
          String jwtToken = JwtProcess.create(loginUser);
          response.addHeader(JwtVO.HEADER, jwtToken);

          LoginResponseDto loginResponseDto = new LoginResponseDto(loginUser.getUser());
          CustomResponseUtil.success(response, loginResponseDto); // 세션종료
     }

}
