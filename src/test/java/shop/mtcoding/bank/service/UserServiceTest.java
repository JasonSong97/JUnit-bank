package shop.mtcoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto.JoinRequestDto;
import shop.mtcoding.bank.dto.user.UserResponseDto.JoinResponseDto;

@ExtendWith(MockitoExtension.class) // Service 가짜 환경에서 실행
public class UserServiceTest extends DummyObject {

     @InjectMocks // 가짜
     private UserService userService;

     @Mock // 가짜
     private UserRepository userRepository;

     @Spy // 진짜 꺼내서 가짜에 넣는다.
     private BCryptPasswordEncoder passwordEncoder;

     @Test
     public void 회원가입_test() throws Exception {
          // given
          JoinRequestDto joinRequestDto = new JoinRequestDto();
          joinRequestDto.setUsername("ssar");
          joinRequestDto.setPassword("1234");
          joinRequestDto.setEmail("ssar@nate.com");
          joinRequestDto.setFullname("쌀");

          // stub 1
          when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
          // when(userRepository.findByUsername(any())).thenReturn(Optional.of(new
          // User())); // 에러 발생

          // stub 2
          User ssar = newMockUser(1L, "ssar", "쌀");
          when(userRepository.save(any())).thenReturn(ssar);

          // when
          JoinResponseDto joinResponseDto = userService.회원가입(joinRequestDto);
          System.out.println("테스트 : " + joinResponseDto.toString());

          // then
          assertThat(joinResponseDto.getId()).isEqualTo(1L);
          assertThat(joinResponseDto.getUsername()).isEqualTo("ssar");
     }
}
