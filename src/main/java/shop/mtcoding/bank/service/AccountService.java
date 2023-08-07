package shop.mtcoding.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

     private final UserRepository userRepository;
     private final AccountRepository accountRepository;

     public AccountListResponseDto 계좌목록보기_유저별(Long userId) {
          User userPS = userRepository.findById(userId).orElseThrow(
                    () -> new CustomApiException("유저를 찾을 수 없습니다. "));

          // 유저의 모든 계좌목록
          List<Account> accountListPS = accountRepository.findByUser_id(userId);
          return new AccountListResponseDto(userPS, accountListPS);
     }

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
}
