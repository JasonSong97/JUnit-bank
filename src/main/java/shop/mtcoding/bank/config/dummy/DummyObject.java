package shop.mtcoding.bank.config.dummy;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class DummyObject {

     public Transaction newWithdrawTransaction(Account account, AccountRepository accountRepository) {
          account.withdraw(100L);

          if (accountRepository != null) {
               accountRepository.save(account);
          }
          Transaction transaction = Transaction.builder()
                    .withdrawAccount(account)
                    .depositAccount(null)
                    .withdrawAccountBalance(account.getBalance())
                    .depositAccountBalance(null)
                    .amount(100L)
                    .gubun(TransactionEnum.WITHDRAW)
                    .sender(account.getNumber() + "")
                    .receiver("ATM")
                    .build();
          return transaction;
     }

     public Transaction newDepositTransaction(Account account, AccountRepository accountRepository) {
          account.deposit(100L);

          if (accountRepository != null) {
               accountRepository.save(account);
          }
          Transaction transaction = Transaction.builder()
                    .withdrawAccount(null)
                    .depositAccount(account)
                    .withdrawAccountBalance(null)
                    .depositAccountBalance(account.getBalance())
                    .amount(100L)
                    .gubun(TransactionEnum.DEPOSIT)
                    .sender("ATM")
                    .receiver(account.getNumber() + "")
                    .tel("01022227777")
                    .build();
          return transaction;
     }

     public Transaction newTransferTransaction(Account withdrawAccount, Account depositAccount,
               AccountRepository accountRepository) {
          withdrawAccount.withdraw(100L);
          depositAccount.deposit(100L);

          if (accountRepository != null) {
               accountRepository.save(withdrawAccount);
               accountRepository.save(depositAccount);
          }
          Transaction transaction = Transaction.builder()
                    .withdrawAccount(withdrawAccount)
                    .depositAccount(depositAccount)
                    .withdrawAccountBalance(withdrawAccount.getBalance())
                    .depositAccountBalance(depositAccount.getBalance())
                    .amount(100L)
                    .gubun(TransactionEnum.TRANSFER)
                    .sender(withdrawAccount.getNumber() + "")
                    .receiver(depositAccount.getNumber() + "")
                    .build();
          return transaction;
     }

     public static User newUser(String username, String fullname) {
          BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
          String encPassword = passwordEncoder.encode("1234");
          return User.builder()
                    .username(username)
                    .password(encPassword)
                    .email(username + "@nate.com")
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
     }

     public static User newMockUser(Long id, String username, String fullname) {
          BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
          String encPassword = passwordEncoder.encode("1234");
          return User.builder()
                    .id(id)
                    .username(username)
                    .password(encPassword)
                    .email(username + "@nate.com")
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .createAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .build();
     }

     public static Account newAccount(Long number, User user) {
          return Account.builder()
                    .number(number)
                    .password(1234L)
                    .balance(1000L)
                    .user(user)
                    .build();
     }

     public static Account newMockAccount(Long id, Long number, Long balance, User user) {
          return Account.builder()
                    .id(id)
                    .number(number)
                    .password(1234L)
                    .balance(balance)
                    .user(user)
                    .createAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .build();
     }

     // 입금 트랜젝션 -> 계좌 1100원 변경 -> 입금 트랜젝션 히스토리가 생성되어야 함.
     public static Transaction newMockDepositTransaction(Long id, Account account) {
          account.deposit(100L);
          return Transaction.builder()
                    .id(id)
                    .depositAccount(account)
                    .withdrawAccount(null)
                    .depositAccountBalance(account.getBalance())
                    .withdrawAccountBalance(null)
                    .amount(100L)
                    .gubun(TransactionEnum.DEPOSIT)
                    .sender("ATM")
                    .receiver(account.getNumber() + "")
                    .tel("01088887777")
                    .createAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .build();
     }
}
