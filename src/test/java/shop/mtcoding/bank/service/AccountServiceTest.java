package shop.mtcoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveRequestDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountListResponseDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends DummyObject {

     @InjectMocks
     private AccountService accountService;
     @Mock
     private UserRepository userRepository;
     @Mock
     private AccountRepository accountRepository;
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
}
