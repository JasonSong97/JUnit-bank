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
@Service
@Transactional(readOnly = true)
public class AccountService {

     private final UserRepository userRepository;
     private final AccountRepository accountRepository;

     @Transactional
     public AccountSaveResponseDto 계좌등록(AccountSaveReqeustDto accountSaveReqeustDto, Long userId) {
          // User가 DB에 있는지 검증 겸 유저 엔티티 가져오기
          User userPS = userRepository.findById(userId).orElseThrow(
                    () -> new CustomApiException("유저를 찾을 수 없습니다."));

          // 해당 계좌가 DB에 있는 중복여부를 체크
          Optional<Account> accountOP = accountRepository.findByNumber(accountSaveReqeustDto.getNumber());
          if (accountOP.isPresent()) {
               throw new CustomApiException("해당 계좌가 이미 존재합니다.");
          }

          // 계좌 등록
          Account accountPS = accountRepository.save(accountSaveReqeustDto.toEntity(userPS));

          // DTO 응답
          return new AccountSaveResponseDto(accountPS);
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
     public static class AccountSaveReqeustDto {
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
