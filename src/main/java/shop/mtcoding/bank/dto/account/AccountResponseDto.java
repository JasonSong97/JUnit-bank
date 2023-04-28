package shop.mtcoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;

public class AccountResponseDto {
     @Getter
     @Setter
     public static class AccountSaveResponseDto {
          private Long id;
          private Long number; // 등록된 계좌 번호
          private Long balance; // 등록된 계좌 잔액

          public AccountSaveResponseDto(Account account) {
               this.id = account.getId();
               this.number = account.getNumber();
               this.balance = account.getBalance();
          }

     }
}
