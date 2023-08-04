package shop.mtcoding.bank.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import shop.mtcoding.bank.config.auth.LoginUser;

// 모든 주소에서 동작(검증)
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

     public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
          super(authenticationManager);
     }

     @Override
     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
               throws IOException, ServletException {
          if (isHeaderVerify(request, response)) {
               // 토큰 존재
               String token = request.getHeader(JwtValueObject.HEADER).replace(JwtValueObject.TOKEN_PREFIX, "");
               LoginUser loginUser = JwtProcess.verify(token); // id, role만 존재

               // 임시 세션, 인증처럼 UserDetailsService 호출 X, UserDetails or username
               Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser, null,
                         loginUser.getAuthorities());
               SecurityContextHolder.getContext().setAuthentication(authentication);

          }
          chain.doFilter(request, response);
     }

     private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response) {
          String header = request.getHeader(JwtValueObject.HEADER);
          if (header == null || !header.startsWith(JwtValueObject.TOKEN_PREFIX)) {
               return false;
          } else {
               return true;
          }
     }
}
