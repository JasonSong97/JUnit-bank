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
          setFilterProcessesUrl("/api/login"); // /login -> /api/login
          this.authenticationManager = authenticationManager;
     }

     // 동작 시점 : POST, /login
     @Override
     public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
               throws AuthenticationException {
          try {
               ObjectMapper om = new ObjectMapper();
               LoginRequestDto loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);

               UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                         loginRequestDto.getUsername(), loginRequestDto.getPassword());

               Authentication authentication = authenticationManager.authenticate(authenticationToken);
               return authentication;
          } catch (Exception e) {
               // SecurityConfig, authenticationEntryPoint에서 걸림
               throw new InternalAuthenticationServiceException(e.getMessage());
          }
     }

     // return authentication 작동 시
     @Override
     protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
               FilterChain chain, Authentication authResult) throws IOException, ServletException {
          LoginUser loginUser = (LoginUser) authResult.getPrincipal();
          String jwtToken = JwtProcess.create(loginUser);
          response.addHeader(JwtValueObject.HEADER, jwtToken);

          LoginResponseDto loginResponseDto = new LoginResponseDto(loginUser.getUser());
          CustomResponseUtil.success(response, loginResponseDto);
     }
}
