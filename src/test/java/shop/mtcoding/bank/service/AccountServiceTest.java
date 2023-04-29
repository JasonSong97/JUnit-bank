package shop.mtcoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveReqeustDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountDepositResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.mtcoding.bank.dto.user.UserRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends DummyObject {

     // 가짜 UserRepository가 AccountService의 주인이 된다.
     @InjectMocks // 모든 Mock들이 InjectMocks 로 주입됨
     private AccountService accountService;
     @Mock
     private UserRepository userRepository;
     @Mock
     private AccountRepository accountRepository;
     @Mock
     private TransactionRepository transactionRepository;

     @Spy // 진짜 객체를 InjectMocks에 주입한다.
     private ObjectMapper om;

     @Test
     public void 계좌등록_test() throws Exception {
          // given
          Long userId = 1L;

          AccountSaveReqeustDto accountSaveReqeustDto = new AccountSaveReqeustDto();
          accountSaveReqeustDto.setNumber(1111L);
          accountSaveReqeustDto.setPassword(1234L);

          // stub 1
          User ssar = newMockUser(userId, "ssar", "쌀");
          when(userRepository.findById(any())).thenReturn(Optional.of(ssar));

          // stub 2
          when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

          // stub 3
          Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
          when(accountRepository.save(any())).thenReturn(ssarAccount);

          // when
          AccountSaveResponseDto accountSaveResponseDto = accountService.계좌등록(accountSaveReqeustDto, userId);
          String responseBody = om.writeValueAsString(accountSaveResponseDto);
          System.out.println("테스트 : " + responseBody);

          // then
          assertThat(accountSaveReqeustDto.getNumber()).isEqualTo(1111L);
     }

     @Test
     public void 계좌삭제_test() throws Exception {
          // given
          Long number = 1111L;
          Long userId = 2L;

          // stub 1
          User ssar = newMockUser(1L, "ssar", "쌀");
          Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
          when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

          // when
          assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, userId));

     }

     // 목적: Account -> balance 변경됬는지
     // Transaction -> transaction balance 변경됬는지
     @Test
     public void 계좌입금_test() throws Exception {
          // given
          AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
          accountDepositRequestDto.setNumber(1111L);
          accountDepositRequestDto.setAmount(100L);
          accountDepositRequestDto.setGubun("DEPOSIT");
          accountDepositRequestDto.setTel("01088887777");

          // stub 1
          User ssar = newMockUser(1L, "ssar", "쌀"); // 실행됨
          Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar); // 실행됨 - ssarAccount1 -> 1000원
          when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1)); // 실행 안됨 -> service 호출 후
                                                                                             // 실행됨 -> 1100원

          // stub 2 (스텁이 진행될 때마다 연관된 객체는 새로 만들어서 주입하기 - 타이밍 떄문에 꼬인다.)
          Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar);
          Transaction transaction = newMockDepositTransaction(1L, ssarAccount2); // 실행됨 - (ssarAccount1 -> 1100원)
          when(transactionRepository.save(any())).thenReturn(transaction); // 실행 안됨

          // when
          AccountDepositResponseDto accountDepositResponseDto = accountService.계좌입금(accountDepositRequestDto);
          System.out.println("테스트 : 잔액 : " + accountDepositResponseDto.getTransaction().getDepositAccountBalance());
          System.out.println("테스트 : 계좌쪽 잔액 : " + ssarAccount1.getBalance());

          // then
          assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
          assertThat(accountDepositResponseDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
     }

     @Test
     public void 계좌입금2_test() throws Exception {
          // given
          AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
          accountDepositRequestDto.setNumber(1111L);
          accountDepositRequestDto.setAmount(100L);
          accountDepositRequestDto.setGubun("DEPOSIT");
          accountDepositRequestDto.setTel("01088887777");

          // stub 1
          User ssar = newMockUser(1L, "ssar", "쌀"); // 실행됨
          Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar); // 실행됨 - ssarAccount1 -> 1000원
          when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1));

          // stub 2
          User ssar2 = newMockUser(1L, "ssar", "쌀");
          Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar2);
          Transaction transaction = newMockDepositTransaction(1L, ssarAccount2); // 실행됨 - (ssarAccount1 -> 1100원)
          when(transactionRepository.save(any())).thenReturn(transaction);

          // when
          AccountDepositResponseDto accountDepositResponseDto = accountService.계좌입금(accountDepositRequestDto);
          String responseBody = om.writeValueAsString(accountDepositResponseDto);
          System.out.println("테스트 : " + responseBody);
          // then
          assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
     }

     @Test
     public void 계좌입금_test3() throws Exception {
          // given
          Account account = newMockAccount(1L, 1111L, 1000L, null);
          Long amount = 0L;
          // when
          if (amount <= 0L) {
               throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
          }
          account.deposit(100L);

          // then
          assertThat(account.getBalance()).isEqualTo(1100L);
     }

     @Test
     public void 계좌출금_test() throws Exception {
          // given
          Long amount = 100L;
          Long password = 1234L;
          Long userId = 1L;

          User ssar = newMockUser(1L, "ssar", "쌀");
          Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);

          // when
          // 0원 체크
          if (amount <= 0L) {
               throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
          }
          ssarAccount.checkOnwer(userId);
          ssarAccount.checkSamePassword(password);
          // ssarAccount.checkBalance(amount);
          ssarAccount.withdraw(amount);

          // then
          assertThat(ssarAccount.getBalance()).isEqualTo(900L);
     }
}

// Git check
// 계좌 출금_테스트 (서비스)
// 계좌 이체_테스트 (서비스)
// 계좌목록보기_유저별_테스트 (서비스)
// 계좌상세보기_테스트 (서비스)
