package shop.mtcoding.bank.dto.account;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.util.CustomDateUtil;

public class AccountResponseDto {

     // DTO가 똑같아도 재사요하지 않기 (나중에 만약에 출금할때 먼가 조금 DTO 달라져야하면 DTO를 공유하면 수정잘못하면 망함 ->
     // 독립적으로 만들자.)
     @Getter
     @Setter
     public static class AccountWithdrawResponseDto { // 입금
          private Long id; // 계좌ID
          private Long number; // 계좌번호
          private Long balance; // 잔액
          private TransactionDto transaction; // 로그

          public AccountWithdrawResponseDto(Account account, Transaction transaction) {
               this.id = account.getId();
               this.number = account.getNumber();
               this.balance = account.getBalance();
               this.transaction = new TransactionDto(transaction);
          }

          @Getter
          @Setter
          public class TransactionDto {
               private Long id;
               private String gubun;
               private String sender;
               private String reciver;
               private Long amount;
               private String tel;
               private String createdAt;

               public TransactionDto(Transaction transaction) {
                    this.id = transaction.getId();
                    this.gubun = transaction.getGubun().getValue();
                    this.sender = transaction.getSender();
                    this.reciver = transaction.getReceiver();
                    this.amount = transaction.getAmount();
                    this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
               }

          }
     }

     @Getter
     @Setter
     public static class AccountDepositResponseDto { // 입금
          private Long id; // 계좌ID
          private Long number; // 계좌번호
          private TransactionDto transaction; // 로그

          public AccountDepositResponseDto(Account account, Transaction transaction) {
               this.id = account.getId();
               this.number = account.getNumber();
               this.transaction = new TransactionDto(transaction);
          }

          @Getter
          @Setter
          public class TransactionDto {
               private Long id;
               private String gubun;
               private String sender;
               private String reciver;
               private Long amount;
               private String tel;
               private String createdAt;

               @JsonIgnore
               private Long depositAccountBalance; // client 전달 X, 서비스단 테스트용

               public TransactionDto(Transaction transaction) {
                    this.id = transaction.getId();
                    this.gubun = transaction.getGubun().getValue();
                    this.sender = transaction.getSender();
                    this.reciver = transaction.getReceiver();
                    this.amount = transaction.getAmount();
                    this.depositAccountBalance = transaction.getDepositAccountBalance();
                    this.tel = transaction.getTel();
                    this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
               }

          }
     }

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
