package shop.mtcoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveReqeustDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends DummyObject {

     // 가짜 UserRepository가 AccountService의 주인이 된다.
     @InjectMocks // 모든 Mock들이 InjectMocks 로 주입됨
     private AccountService accountService;
     @Mock
     private UserRepository userRepository;
     @Mock
     private AccountRepository accountRepository;

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
}
