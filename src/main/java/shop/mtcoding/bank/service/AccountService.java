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
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountDepositResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountTransferResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountWithdrawResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

     private final UserRepository userRepository;
     private final AccountRepository accountRepository;
     private final TransactionRepository transactionRepository;

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

     @Transactional
     public void 계좌삭제(Long number, Long userId) {
          // 1. 계좌 DB 검증
          Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                    () -> new CustomApiException("계좌를 찾을 수 없습니다. "));

          // 2. 계좌 소유자 DB 검증
          accountPS.checkOwner(userId);

          // 3. 계좌 삭제
          accountRepository.deleteById(accountPS.getId());
     }

     @Transactional // 인증 필요 X
     public AccountDepositResponseDto 계좌입금(AccountDepositRequestDto accountDepositRequestDto) { // ATM -> A계좌
          // 1. 0원 체크
          if (accountDepositRequestDto.getAmount() <= 0L) {
               throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다. ");
          }

          // 2. 입금계좌 확인
          Account depositAccountPS = accountRepository.findByNumber(accountDepositRequestDto.getNumber()).orElseThrow(
                    () -> new CustomApiException("계좌를 찾을 수 없습니다. "));

          // 3. 입금(해당 계좌 balance 조정 - update문 - 더티채킹)
          depositAccountPS.deposit(accountDepositRequestDto.getAmount());

          // 4. 거래내역 남기기
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

          // 5. DTO
          return new AccountDepositResponseDto(depositAccountPS, transactionPS);
     }

     @Transactional
     public AccountWithdrawResponseDto 계좌출금(AccountWithdrawRequestDto accountWithdrawRequestDto, Long userId) {
          // 1. 0원 체크
          if (accountWithdrawRequestDto.getAmount() <= 0L) {
               throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다. ");
          }

          // 2. 출금계좌 확인
          Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawRequestDto.getNumber()).orElseThrow(
                    () -> new CustomApiException("계좌를 찾을 수 없습니다. "));

          // 3. 출금 소유자 확인(로그인한 사람과 비교)
          withdrawAccountPS.checkOwner(userId);

          // 4. 출금 비밀번호 확인
          withdrawAccountPS.checkSamePassword(accountWithdrawRequestDto.getPassword());

          // 5. 출금계좌 잔액 확인
          withdrawAccountPS.checkBalance(accountWithdrawRequestDto.getAmount());

          // 6. 출금하기
          withdrawAccountPS.withdraw(accountWithdrawRequestDto.getAmount());

          // 7. 거래내역 남기기
          Transaction transaction = Transaction.builder()
                    .withdrawAccount(withdrawAccountPS)
                    .depositAccount(null)
                    .withdrawAccountBalance(withdrawAccountPS.getBalance())
                    .depositAccountBalance(null)
                    .amount(accountWithdrawRequestDto.getAmount())
                    .gubun(TransactionEnum.WITHDRAW)
                    .sender(accountWithdrawRequestDto.getNumber() + "")
                    .receiver("ATM")
                    .build();
          Transaction transactionPS = transactionRepository.save(transaction);

          // 8. DTO
          return new AccountWithdrawResponseDto(withdrawAccountPS, transactionPS);
     }

     @Transactional
     public AccountTransferResponseDto 계좌이체(AccountTransferRequestDto accountTransferRequestDto, Long userId) {
          // 1. 출금계좌 != 입금계좌
          if (accountTransferRequestDto.getWithdrawNumber().longValue() == accountTransferRequestDto.getDepositNumber()
                    .longValue()) {
               throw new CustomApiException("입출금계좌가 동일할 수 없습니다. ");
          }

          // 2. 0원 체크
          if (accountTransferRequestDto.getAmount() <= 0L) {
               throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다. ");
          }

          // 3. 출금계좌 확인
          Account withdrawAccountPS = accountRepository.findByNumber(accountTransferRequestDto.getWithdrawNumber())
                    .orElseThrow(
                              () -> new CustomApiException("출금계좌를 찾을 수 없습니다. "));

          // 4. 입금계좌 확인
          Account depositAccountPS = accountRepository.findByNumber(accountTransferRequestDto.getDepositNumber())
                    .orElseThrow(
                              () -> new CustomApiException("입금계좌를 찾을 수 없습니다. "));

          // 5. 출금 소유자 확인(로그인한 사람과 비교)
          withdrawAccountPS.checkOwner(userId);

          // 6. 출금 비밀번호 확인
          withdrawAccountPS.checkSamePassword(accountTransferRequestDto.getWithdrawPassword());

          // 7. 출금계좌 잔액 확인
          withdrawAccountPS.checkBalance(accountTransferRequestDto.getAmount());

          // 8. 이체하기
          withdrawAccountPS.withdraw(accountTransferRequestDto.getAmount());
          depositAccountPS.deposit(accountTransferRequestDto.getAmount());

          // 7. 거래내역 남기기
          Transaction transaction = Transaction.builder()
                    .withdrawAccount(withdrawAccountPS)
                    .depositAccount(depositAccountPS)
                    .withdrawAccountBalance(withdrawAccountPS.getBalance())
                    .depositAccountBalance(depositAccountPS.getBalance())
                    .amount(accountTransferRequestDto.getAmount())
                    .gubun(TransactionEnum.TRANSFER)
                    .sender(accountTransferRequestDto.getWithdrawNumber() + "")
                    .receiver(accountTransferRequestDto.getDepositNumber() + "")
                    .build();
          Transaction transactionPS = transactionRepository.save(transaction);

          // 8. DTO
          return new AccountTransferResponseDto(withdrawAccountPS, transactionPS);
     }
}
