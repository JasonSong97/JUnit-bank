package shop.mtcoding.bank.dto.account;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.user.User;

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

     @Getter
     @Setter
     public static class AccountListResponseDto {
          private String fullname;
          private List<AccountDto> accounts = new ArrayList<>();

          public AccountListResponseDto(User user, List<Account> accounts) {
               this.fullname = user.getFullname();
               this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
               // [account, account]
          }

          @Getter
          @Setter
          private class AccountDto {
               private Long id;
               private Long number;
               private Long balance;

               public AccountDto(Account account) {
                    this.id = account.getId();
                    this.number = account.getNumber();
                    this.balance = account.getBalance();
               }
          }
     }
}
