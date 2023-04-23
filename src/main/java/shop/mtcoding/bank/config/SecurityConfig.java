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

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.util.CustomResponseUtil;

@Configuration
public class SecurityConfig {

      private final Logger log = LoggerFactory.getLogger(getClass());

      @Bean
      public BCryptPasswordEncoder passwordEncoder() {
            log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨.");
            return new BCryptPasswordEncoder();
      }

      // JWT 필터 등록이 필요함

      // JWT 서버 (세션 사용 X)
      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            log.debug("디버그 : SecurityFilterChain 빈 등록됨 ");
            http.headers().frameOptions().disable(); // iframe 허용 안함
            http.csrf().disable(); // postman 이면 작동안함
            http.cors().configurationSource(configurationSource());

            // JsessionId를 서버쪽에서 관리안하겠다는 뜻!!
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            // react, app으로 요청할 예정
            http.formLogin().disable(); // 화면 로그인 방식 X
            http.httpBasic().disable(); // httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.

            // Exception 가로채기: 인증과 권한 실패시 디폴트 값 바꾸기 위해 (통일성)
            http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
                  CustomResponseUtil.unAuthentication(response, "로그인을 진행해 주세요");
            });

            http.authorizeRequests()
                        .antMatchers("/api/s/**").authenticated()
                        .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN)
                        .anyRequest().permitAll();
            return http.build();
      }

      public CorsConfigurationSource configurationSource() {
            log.debug("디버그 : CorsConfigurationSource cors 설정이 SecurityFilterChain에 등록됨 ");
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedHeader("*"); // 모든 헤더 다 받기
            configuration.addAllowedMethod("*"); // GET POST DELETE PUT (JavaScript 요청 헤더)
            configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트 엔드 IP 허용 react)
            configuration.setAllowCredentials(true); // 클라이언트 쪽에서 쿠키 요청 허용

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration); // 어떤 요청이 와도 위의 요청을 적용할 것이다.
            return source;
      }
}
