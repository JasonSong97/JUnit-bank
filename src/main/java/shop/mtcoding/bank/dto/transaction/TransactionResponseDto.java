package shop.mtcoding.bank.dto.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.util.CustomDateUtil;

public class TransactionResponseDto {

     @Getter
     @Setter
     public static class TransactionListResponseDto {
          private List<TransactionDto> transactions = new ArrayList<>();

          public TransactionListResponseDto(List<Transaction> transactions, Account account) {
               this.transactions = transactions.stream()
                         .map((transaction) -> new TransactionDto(transaction, account.getNumber()))
                         .collect(Collectors.toList());
               ;
          }

          @Getter
          @Setter
          public class TransactionDto {
               private Long id;
               private String gubun;
               private Long amount;
               private String sender;
               private String reciver;
               private String tel;
               private String createdAt;
               private Long balance;

               public TransactionDto(Transaction transaction, Long accountNumber) {
                    this.id = transaction.getId();
                    this.gubun = transaction.getGubun().getValue();
                    this.amount = transaction.getAmount();
                    this.sender = transaction.getSender();
                    this.reciver = transaction.getReceiver();
                    this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                    this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

                    if (transaction.getDepositAccount() == null) { // withdraw만 들어오는 경우
                         this.balance = transaction.getWithdrawAccountBalance(); // withdraw만 볼것
                    } else if (transaction.getWithdrawAccount() == null) {
                         this.balance = transaction.getDepositAccountBalance();
                    } else {
                         // 1111 계좌의 입출금 내역 조회 (출금계좌 = 값, 입금계좌 = 값)
                         if (accountNumber.longValue() == transaction.getDepositAccount().getNumber()) {
                              this.balance = transaction.getDepositAccountBalance();
                         } else {
                              this.balance = transaction.getWithdrawAccountBalance();
                         }
                    }
               }
          }
     }
}
