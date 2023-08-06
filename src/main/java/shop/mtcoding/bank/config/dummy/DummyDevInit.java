package shop.mtcoding.bank.config.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Configuration
public class DummyDevInit extends DummyObject {

     @Profile("dev") // dev만 동작, prod는 실행 안되야함
     @Bean
     CommandLineRunner init(UserRepository userRepository) {
          return (args) -> {
               // 서버 실행 시, 무조건 실행
               User ssar = userRepository.save(newUser("ssar", "쌀"));
          };
     }
}
