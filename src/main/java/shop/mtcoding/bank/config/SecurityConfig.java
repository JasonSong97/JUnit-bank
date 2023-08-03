package shop.mtcoding.bank.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import shop.mtcoding.bank.domain.user.UserEnum;

@Configuration
public class SecurityConfig {

     private final Logger log = LoggerFactory.getLogger(getClass()); // @slf4j -> JUnit 테스트 문제 발생

     @Bean // IOC에 BCryptPasswordEncoder 객체 등록, @Configuration 붙어있는 곳만
     public BCryptPasswordEncoder bCryptPasswordEncoder() {
          log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨");
          return new BCryptPasswordEncoder();
     }

     // JWT 등록이 필요함!!

     // JWT 서버 -> 세션 사용 X(무상태 서버)
     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          log.debug("디버그 : filterChain 빈 등록됨");
          http.headers().frameOptions().disable(); // iframe 허용 X
          http.csrf().disable();
          http.cors().configurationSource(configurationSource());

          // jSessionId 서버쪽에서 관리 안하겠다는 것
          http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
          // react, app 요청 예정
          http.formLogin().disable();
          // 브라우저가 팝업창을 이용해서 사용자 인증을 진행
          http.httpBasic().disable();
          http.authorizeRequests()
                    .antMatchers("/api/s/**").authenticated()
                    .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN) // ROLE_
                    .anyRequest().permitAll();

          return http.build();
     }

     public CorsConfigurationSource configurationSource() {
          log.debug("디버그 : CorsConfigurationSource cors 설정이 SecurityFilterChain에 등록됨");
          CorsConfiguration configuration = new CorsConfiguration();
          configuration.addAllowedHeader("*"); // 모든 헤더 받기
          configuration.addAllowedMethod("*"); // GET, POST, DELETE, PUT 전부 허용
          configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (FE IP만 허용)
          configuration.setAllowCredentials(true); // 클라이언트쪽에서 쿠키 요청 허용(클라이언트쪽에서 보내는게 가능)

          UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
          source.registerCorsConfiguration("/**", configuration); // 모든 주소요청에 위에 설정을 넣기
          return source;
     }
}
