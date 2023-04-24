package shop.mtcoding.bank.config.dummy;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class DummyObject {

     protected User newUser(String username, String fullname) { // entity save용
          BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
          String encPassword = passwordEncoder.encode("1234");
          return User.builder() // id date 나중에 만들어질때 사용 그래서 필요 X
                    .username(username)
                    .password(encPassword)
                    .email(fullname + "@nate.com")
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
     }

     protected User newMockUser(Long id, String username, String fullname) { // 가짜 용
          BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
          String encPassword = passwordEncoder.encode("1234");
          return User.builder()
                    .id(id)
                    .username(username)
                    .password(encPassword)
                    .email(fullname + "@nate.com")
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
     }
}
