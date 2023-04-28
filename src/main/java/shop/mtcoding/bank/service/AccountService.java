package shop.mtcoding.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionEnum;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveReqeustDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountDepositResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.mtcoding.bank.dto.user.UserRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountService {

     private final UserRepository userRepository;
     private final AccountRepository accountRepository;
     private final TransactionRepository transactionRepository;

     public AccountListResponseDto 계좌목록보기_유저별(Long userId) {
          User userPS = userRepository.findById(userId).orElseThrow(
                    () -> new CustomApiException("유저를 찾을 수 없습니다."));
          // 유저의 모든 계좌목록
          List<Account> accountListPS = accountRepository.findByUser_id(userId);
          return new AccountListResponseDto(userPS, accountListPS);
     }

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

     @Transactional
     public void 계좌삭제(Long number, Long userId) {
          // 1. 계좌 확인
          Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                    () -> new CustomApiException("계좌를 찾을 수 없습니다."));

          // 2. 계좌 소유자 확인
          accountPS.checkOnwer(userId);

          // 3. 계좌 삭제
          accountRepository.deleteById(accountPS.getId());
     }

     // 인증 필요 없다
     @Transactional
     public AccountDepositResponseDto 계좌입금(AccountDepositRequestDto accountDepositRequestDto) { // ATM -> 누군가의 계좌
          // 0원 체크
          if (accountDepositRequestDto.getAmount() <= 0) {
               throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
          }

          // 입금계좌 확인
          Account depositAccountPS = accountRepository.findByNumber(accountDepositRequestDto.getNumber())
                    .orElseThrow(
                              () -> new CustomApiException("계좌를 찾을 수 없습니다."));

          // 입금 (해당 계좌 balance 조정 - update문 - 더티체킹)
          depositAccountPS.deposit(accountDepositRequestDto.getAmount());
          System.out.println("테스트 : ssarAccount1 : 잔액 : " + depositAccountPS.getBalance());

          // 거래내역 남기기
          Transaction transaction = Transaction.builder()
                    .depositAccount(depositAccountPS)
                    .withdrawAccount(null)
                    .depositAccountBalance(depositAccountPS.getBalance())
                    .withdrawAccountBalance(null)
                    .amount(accountDepositRequestDto.getAmount())
                    .gubun(TransactionEnum.DEPOSIT)
                    .sender("ATM")
                    .receiver(accountDepositRequestDto.getNumber() + "")
                    .tel(accountDepositRequestDto.getTel())
                    .build();

          Transaction transactionPS = transactionRepository.save(transaction);
          return new AccountDepositResponseDto(depositAccountPS, transactionPS);
     }
}
