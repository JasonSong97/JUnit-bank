package shop.mtcoding.bank.service;

import java.util.Optional;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

     private final UserRepository userRepository;
     private final AccountRepository accountRepository;

     @Transactional
     public AccountSaveResponseDto 계좌등록(AccountSaveRequestDto accountSaveRequestDto, Long userId) {
          // 1. User DB 검증
          User userPS = userRepository.findById(userId).orElseThrow(
                    () -> new CustomApiException("유저를 찾을 수 없습니다. "));

          // 2. 계좌 DB 중복 여부 검증
          Optional<Account> accountOP = accountRepository.findByNumber(accountSaveRequestDto.getNumber());
          if (accountOP.isPresent()) {
               throw new CustomApiException("해당 계좌가 이미 존재합니다. ");
          }

          // 3. 계좌 등록
          Account accountPS = accountRepository.save(accountSaveRequestDto.toEntity(userPS));

          // 4. DTO
          return new AccountSaveResponseDto(accountPS);
     }

     @Getter
     @Setter
     public static class AccountSaveResponseDto {

          private Long id;
          private Long number;
          private Long balance;

          public AccountSaveResponseDto(Account account) {
               this.id = account.getId();
               this.number = account.getNumber();
               this.balance = account.getBalance();
          }
     }

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
}
