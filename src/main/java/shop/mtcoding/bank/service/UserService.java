package shop.mtcoding.bank.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Service
public class UserService { // 서비스는 DTO요청을 받고 DTO로 응답을 한다.

     private final Logger log = LoggerFactory.getLogger(getClass());
     private final UserRepository userRepository;
     private final BCryptPasswordEncoder passwordEncoder;

     @Transactional
     public JoinResponseDto 회원가입(JoinRequestDto joinRequestDto) {
          // 1. 동일유저 확인
          Optional<User> userOP = userRepository.findByUsername(joinRequestDto.getUsername());
          if (userOP.isPresent()) { // 중복된 것
               throw new CustomApiException("동일한 username이 존재합니다.");
          }

          // 2. 패스워드 인코딩 + 회원가입
          User userPS = userRepository.save(joinRequestDto.toEntity(passwordEncoder));

          // 3. dto 응답
          return new JoinResponseDto(userPS);
     }

     @ToString
     @Getter
     @Setter
     public static class JoinRequestDto { // validation check
          private String username;
          private String password;
          private String email;
          private String fullname;

          public User toEntity(BCryptPasswordEncoder passwordEncoder) {
               return User.builder()
                         .username(username)
                         .password(passwordEncoder.encode(password))
                         .email(email)
                         .fullname(fullname)
                         .role(UserEnum.CUSTOMER)
                         .build();
          }

     }

     @Getter
     @Setter
     public static class JoinResponseDto {
          private Long id;
          private String username;
          private String fullname;

          public JoinResponseDto(User user) {
               this.id = user.getId();
               this.username = user.getUsername();
               this.fullname = user.getFullname();
          }

     }
}
