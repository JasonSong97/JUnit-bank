package shop.mtcoding.bank.dto.user;

import javax.validation.constraints.NotEmpty;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class UserRequestDto {

     @ToString
     @Getter
     @Setter
     public static class JoinRequestDto { // validation check
          @NotEmpty // null 이거나, 공백일 수 없다.
          private String username;
          @NotEmpty
          private String password;
          @NotEmpty
          private String email;
          @NotEmpty
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
}
