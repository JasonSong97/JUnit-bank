package shop.mtcoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
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
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountDepositRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountTransferRequestDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountWithdrawRequestDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountDepositResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends DummyObject {

     @InjectMocks
     private AccountService accountService;
     @Mock
     private UserRepository userRepository;
     @Mock
     private AccountRepository accountRepository;
     @Mock
     private TransactionRepository transactionRepository;
     @Spy
     private ObjectMapper om;

     @Test
     public void 계좌등록_test() throws Exception {
          // given
          Long userId = 1L;

          AccountSaveRequestDto accountSaveRequestDto = new AccountSaveRequestDto();
          accountSaveRequestDto.setNumber(1111L);
          accountSaveRequestDto.setPassword(1234L);

          // stub 1
          User ssar = newMockUser(userId, "ssar", "쌀");
          when(userRepository.findById(any())).thenReturn(Optional.of(ssar));

          // stub 2
          when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

          // stub 3
          Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
          when(accountRepository.save(any())).thenReturn(ssarAccount);

          // when
          AccountSaveResponseDto accountSaveResponseDto = accountService.계좌등록(accountSaveRequestDto, userId);
          String resposneBody = om.writeValueAsString(accountSaveResponseDto);
          System.out.println("테스트 : " + resposneBody);

          // then
          assertThat(accountSaveRequestDto.getNumber()).isEqualTo(1111L);
     }

     @Test
     public void 계좌목록보기_유저별_test() throws Exception {
          // given
          Long userId = 1L;

          // stub 1
          User ssar = newMockUser(userId, "ssar", "쌀");
          when(userRepository.findById(userId)).thenReturn(Optional.of(ssar));

          // stub 2
          Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
          Account ssarAccount2 = newMockAccount(2L, 2222L, 1000L, ssar);
          List<Account> accountList = Arrays.asList(ssarAccount1, ssarAccount2);
          when(accountRepository.findByUser_id(any())).thenReturn(accountList);

          // when
          AccountListResponseDto accountListResponseDto = accountService.계좌목록보기_유저별(userId);

          // then
          assertThat(accountListResponseDto.getFullname()).isEqualTo("쌀");
          assertThat(accountListResponseDto.getAccounts().size()).isEqualTo(2);
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

          // when, then
          assertThrows(CustomApiException.class, () -> accountService.계좌삭제(number, userId));
     }

     @Test
     public void 계좌입금_test() throws Exception {
          // given
          AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
          accountDepositRequestDto.setNumber(1111L);
          accountDepositRequestDto.setAmount(100L);
          accountDepositRequestDto.setGubun("DEPOSIT");
          accountDepositRequestDto.setTel("01088887777");

          // stub 1
          User ssar = newMockUser(1L, "ssar", "쌀");
          Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
          when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1));

          // stub 2
          Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar);
          Transaction transaction = newMockDepositTransaction(1L, ssarAccount2);
          when(transactionRepository.save(any())).thenReturn(transaction);

          // when
          AccountDepositResponseDto accountDepositResponseDto = accountService.계좌입금(accountDepositRequestDto);
          System.out.println(
                    "테스트 : 트랜젝션 입금계좌 잔액 : " + accountDepositResponseDto.getTransaction().getDepositAccountBalance());
          System.out.println("테스트 : 계좌쪽 잔액 : " + ssarAccount1.getBalance());
          System.out.println("테스트 : 계좌쪽 잔액 : " + ssarAccount2.getBalance());

          // then
          assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
          assertThat(accountDepositResponseDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
     }

     @Test
     public void 계좌입금_test2() throws Exception {
          // given
          AccountDepositRequestDto accountDepositRequestDto = new AccountDepositRequestDto();
          accountDepositRequestDto.setNumber(1111L);
          accountDepositRequestDto.setAmount(100L);
          accountDepositRequestDto.setGubun("DEPOSIT");
          accountDepositRequestDto.setTel("01088887777");

          // stub 1
          User ssar = newMockUser(1L, "ssar", "쌀");
          Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
          when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1));

          // stub 2
          User ssar2 = newMockUser(1L, "ssar", "쌀");
          Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar2);
          Transaction transaction = newMockDepositTransaction(1L, ssarAccount2);
          when(transactionRepository.save(any())).thenReturn(transaction);

          // when
          AccountDepositResponseDto accountDepositResponseDto = accountService.계좌입금(accountDepositRequestDto);
          String resposneBody = om.writeValueAsString(accountDepositResponseDto);
          System.out.println("테스트 : " + resposneBody);

          // then
          assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
     }

     @Test
     public void 계좌입금_test3() throws Exception {
          // given
          Account account = newMockAccount(1L, 1111L, 1000L, null);
          Long amount = 100L;

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
          Long amount = 1000L;
          Long password = 1234L;
          Long userId = 1L;

          User ssar = newMockUser(1L, "ssar", "쌀");
          Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);

          // when
          if (amount <= 0L) {
               throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
          }

          ssarAccount.checkOwner(userId);

          ssarAccount.checkSamePassword(password);

          // ssarAccount.checkBalance(amount); // 생략한 이유 생각

          ssarAccount.withdraw(amount);

          // then
          assertThat(ssarAccount.getBalance()).isEqualTo(0L);
     }

     @Test
     public void 계좌이체_test() throws Exception {
          // given
          Long userId = 1L;
          AccountTransferRequestDto accountTransferRequestDto = new AccountTransferRequestDto();
          accountTransferRequestDto.setWithdrawNumber(1111L);
          accountTransferRequestDto.setDepositNumber(2222L);
          accountTransferRequestDto.setWithdrawPassword(1234L);
          accountTransferRequestDto.setAmount(100L);
          accountTransferRequestDto.setGubun("TRANSFER");

          User ssar = newMockUser(1L, "ssar", "쌀");
          User cos = newMockUser(1L, "cos", "코스");
          Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, ssar);
          Account depositAccount = newMockAccount(2L, 2222L, 1000L, cos);

          // when
          // 출금계좌 != 입금계좌
          if (accountTransferRequestDto.getWithdrawNumber().longValue() == accountTransferRequestDto.getDepositNumber()
                    .longValue()) {
               throw new CustomApiException("입출금계좌가 동일할 수 없습니다. ");
          }

          // 0원 체크
          if (accountTransferRequestDto.getAmount() <= 0L) {
               throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다. ");
          }

          // 출금 소유자 확인(로그인한 사람과 비교)
          withdrawAccount.checkOwner(userId);

          // 출금 비밀번호 확인
          withdrawAccount.checkSamePassword(accountTransferRequestDto.getWithdrawPassword());

          // 출금계좌 잔액 확인
          withdrawAccount.checkBalance(accountTransferRequestDto.getAmount());

          // 이체하기
          withdrawAccount.withdraw(accountTransferRequestDto.getAmount());
          depositAccount.deposit(accountTransferRequestDto.getAmount());

          // then
          assertThat(withdrawAccount.getBalance()).isEqualTo(900L);
          assertThat(depositAccount.getBalance()).isEqualTo(1100L);
     }
}
