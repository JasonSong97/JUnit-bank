package shop.mtcoding.bank.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.config.jwt.JwtAuthenticationFilter;
import shop.mtcoding.bank.config.jwt.JwtAuthorizationFilter;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.util.CustomResponseUtil;

@Configuration // IoC
public class SecurityConfig {
     private final Logger log = LoggerFactory.getLogger(getClass());

     @Bean // IoC
     public BCryptPasswordEncoder passwordEncoder() {
          log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨.");
          return new BCryptPasswordEncoder();
     }

     // Jwt 필터 등록 필요(내부 클래스 필요) -> SecurityFilterChain에 필터 적용 필요
     public class CustomSecurityFilterManager
               extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {

          @Override
          public void configure(HttpSecurity builder) throws Exception {
               AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
               builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
               builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
               super.configure(builder);
          }

     }

     @Bean // Jwt 서버만듬: 세션 사용 X
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          log.debug("디버그 : filterChain 빈 등록됨");
          http.headers().frameOptions().disable(); // iframe 허용 X
          http.csrf().disable(); // postman 작동 안함
          http.cors().configurationSource(configurationSource()); // 자바스크립트 공격 막기

          http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // JessionId를 서버쪽에서 관리 안한다는
                                                                                           // 의미
          // 리엑트, 엡으로 요청할 예정
          http.formLogin().disable(); // 화면 로그인 사용 X
          http.httpBasic().disable(); // httpBasic: 브라우저가 팝업창을 이용해서 인증을 진행한다.

          // Jwt filter 적용
          http.apply(new CustomSecurityFilterManager());

          // 인증 실패
          http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
               CustomResponseUtil.fail(response, "로그인을 진행해 주세요", HttpStatus.UNAUTHORIZED);
          });

          // 권한 실패
          http.exceptionHandling().accessDeniedHandler((request, response, e) -> {
               CustomResponseUtil.fail(response, "권한이 없습니다.", HttpStatus.FORBIDDEN); // 403
          });

          http.authorizeRequests()
                    .antMatchers("/api/s/**").authenticated()
                    .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN)
                    .anyRequest().permitAll();

          return http.build();
     }

     public CorsConfigurationSource configurationSource() { // 자바스크립트로 들어오는 요청
          log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain 등록됨");
          CorsConfiguration configuration = new CorsConfiguration();
          configuration.addAllowedHeader("*");
          configuration.addAllowedMethod("*"); // GET POST PUT DELETE 모든 요청 허용
          configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용(프론트 엔드 IP만 허용 react)
          configuration.setAllowCredentials(true); // client 쪽에서 쿠키 요청 허용
          configuration.addExposedHeader("Authorization"); // 디폴트(예전), 지금은 아님
          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          source.registerCorsConfiguration("/**", configuration); // 모든 요청에 해당 과정을 넣겠다.
          return source;
     }
}
