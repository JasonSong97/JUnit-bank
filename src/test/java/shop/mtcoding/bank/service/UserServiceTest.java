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

import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.service.UserService.JoinRequestDto;
import shop.mtcoding.bank.service.UserService.JoinResponseDto;

// Spring 관련 Bean들이 하나도 없는 환경
@ExtendWith(MockitoExtension.class) // 가짜 테스트
public class UserServiceTest {

     @InjectMocks // 가짜환경에다가 넣는다는 것
     private UserService userService;

     @Mock // 가짜
     private UserRepository userRepository;

     @Spy // 진짜
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
          when(userRepository.findByUsername(any())).thenReturn(Optional.empty()); // 가정법
          // when(userRepository.findByUsername(any())).thenReturn(Optional.of(new
          // User()));

          // stub 2
          User ssar = User.builder()
                    .id(1L)
                    .username("ssar")
                    .password("1234")
                    .email("ssa@nate.com")
                    .fullname("쌀")
                    .role(UserEnum.CUSTOMER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
          when(userRepository.save(any())).thenReturn(ssar);

          // when
          JoinResponseDto joinResponseDto = userService.회원가입(joinRequestDto);
          System.out.println("테스트 : " + joinResponseDto);

          // then
          assertThat(joinResponseDto.getId()).isEqualTo(1L);
          assertThat(joinResponseDto.getUsername()).isEqualTo("ssar");

     }
}
