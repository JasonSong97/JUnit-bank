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

          List<TransactionDto> transactions = new ArrayList<>();

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
               private String receiver;
               private String tel;
               private String createdAt;
               private Long balance;

               public TransactionDto(Transaction transaction, Long accountNumber) {
                    this.id = transaction.getId();
                    this.gubun = transaction.getGubun().getValue();
                    this.amount = transaction.getAmount();
                    this.sender = transaction.getSender();
                    this.receiver = transaction.getReceiver();
                    this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreateAt());
                    this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

                    // 출금(null) 입금(값), 출금(값) 입금(null)
                    if (transaction.getDepositAccount() == null) {
                         this.balance = transaction.getDepositAccountBalance();
                    } else if (transaction.getWithdrawAccount() == null) {
                         this.balance = transaction.getWithdrawAccountBalance();
                    } else { // 출금(값) 입금(값)
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
