package shop.mtcoding.bank.config.dummy;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class DummyObject {

     protected static User newUser(String username, String fullname) {
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

     protected static User newMockUser(Long id, String username, String fullname) {
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

     protected static Account newAccount(Long number, User user) {
          return Account.builder()
                    .number(number)
                    .password(1234L)
                    .balance(1000L)
                    .user(user)
                    .build();
     }

     protected static Account newMockAccount(Long id, Long number, Long balance, User user) {
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
     protected static Transaction newMockDepositTransaction(Long id, Account account) {
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
