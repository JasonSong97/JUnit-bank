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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import shop.mtcoding.bank.config.dummy.DummyObject;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto.JoinRequestDto;
import shop.mtcoding.bank.dto.user.UserResponseDto.JoinResponseDto;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends DummyObject {

     @InjectMocks // 가짜환경에 집어 넣는 것, @Autowired랑 다름
     private UserService userService;
     @Mock
     private UserRepository userRepository;
     @Spy
     private BCryptPasswordEncoder bCryptPasswordEncoder;

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

          // stub 2
          User ssar = newMockUser(1L, "ssar", "쌀");
          when(userRepository.save(any())).thenReturn(ssar);

          // when
          JoinResponseDto joinResponseDto = userService.회원가입(joinRequestDto);

          // then
          assertThat(joinResponseDto.getId()).isEqualTo(1L);
          assertThat(joinResponseDto.getUsername()).isEqualTo("ssar");
     }
}
