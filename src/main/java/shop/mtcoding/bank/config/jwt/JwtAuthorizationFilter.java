package shop.mtcoding.bank.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import shop.mtcoding.bank.config.auth.LoginUser;

/**
 * 모든 주소에서 동작함 (토큰 검증)
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter { // 검증필터
     private final Logger log = LoggerFactory.getLogger(getClass());

     public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
          super(authenticationManager);
     }

     @Override
     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
               throws IOException, ServletException {

          if (isHeaderVerify(request, response)) {
               log.debug("디버그 : 토큰이 존재함");
               // 토큰이 존재함
               String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX, ""); // 공백으로 치환
               LoginUser loginUser = JwtProcess.verify(token);
               log.debug("디버그 : 토큰 검증이 완료됨");

               // 임시 세션 (첫번쨰 파라미터: UserDetails 타입 or username)
               Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null,
                         loginUser.getAuthorities()); // role 들어가있음
               SecurityContextHolder.getContext().setAuthentication(authentication); // 강제 로그인
               log.debug("디버그 : 임시 세션이 생성됨");
          }
          chain.doFilter(request, response);
     }

     private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response) {
          String header = request.getHeader(JwtVO.HEADER);
          if (header == null || !header.startsWith(JwtVO.TOKEN_PREFIX)) {
               return false;
          } else {
               return true;
          }
     }
}
