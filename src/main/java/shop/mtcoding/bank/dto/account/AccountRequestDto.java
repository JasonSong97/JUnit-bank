package shop.mtcoding.bank.dto.account;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

public class AccountRequestDto {

     @Getter
     @Setter
     public static class AccountSaveRequestDto {

          @NotNull
          @Digits(integer = 4, fraction = 4)
          private Long number;
          @NotNull
          @Digits(integer = 4, fraction = 4)
          private Long password;

          public Account toEntity(User user) {
               return Account.builder()
                         .number(number)
                         .password(password)
                         .balance(1000L)
                         .user(user)
                         .build();
          }
     }

     @Getter
     @Setter
     public static class AccountDepositRequestDto {

          @NotNull
          @Digits(integer = 4, fraction = 4)
          private Long number;
          @NotNull
          private Long amount;
          @NotEmpty
          @Pattern(regexp = "DEPOSIT")
          private String gubun; // DEPOSIT
          @NotEmpty
          @Pattern(regexp = "^[0-9]{11}")
          private String tel;
     }

     @Getter
     @Setter
     public static class AccountWithdrawRequestDto {

          @NotNull
          @Digits(integer = 4, fraction = 4)
          private Long number;
          @NotNull
          @Digits(integer = 4, fraction = 4)
          private Long password;
          @NotNull
          private Long amount;
          @NotEmpty
          @Pattern(regexp = "WITHDRAW")
          private String gubun;
     }
}
