package shop.mtcoding.bank.dto.user;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class UserRequestDto {

     @Getter
     @Setter
     public static class AccountDepositRequestDto {
          @NotNull
          @Digits(integer = 4, fraction = 4)
          private Long number;
          @NotNull
          private Long amount; // 0원 유효성 검사
          @NotEmpty
          @Pattern(regexp = "DEPOSIT")
          private String gubun; // DEPOSIT
          @NotEmpty
          @Pattern(regexp = "^[0-9]{11}")
          private String tel;
     }

     @Getter
     @Setter
     public static class LoginRequestDto {
          private String username;
          private String password;
     }

     @Getter
     @Setter
     public static class JoinRequestDto { // validation check
          @NotEmpty // null 이거나, 공백일 수 없다.
          @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "영문/숫자 2~20자 이내로 작성해주세요.")
          private String username;

          @Size(min = 4, max = 20)
          @NotEmpty
          private String password;

          @Pattern(regexp = "^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 작성해주세요.")
          @NotEmpty
          private String email;

          @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요.")
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
