package shop.mtcoding.bank.dto.user;

import org.springframework.beans.propertyeditors.CustomDateEditor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

public class UserResponseDto {

     @Getter
     @Setter
     public static class LoginResponseDto {
          private Long id;
          private String username;
          private String createAt;

          public LoginResponseDto(User user) {
               this.id = user.getId();
               this.username = user.getUsername();
               this.createAt = CustomDateUtil.toStringFormat(user.getCreatedAt());
          }
     }

     @ToString
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
