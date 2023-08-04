package shop.mtcoding.bank.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;
import shop.mtcoding.bank.dto.user.UserRequestDto.JoinRequestDto;
import shop.mtcoding.bank.dto.user.UserResponseDto.JoinResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@RequiredArgsConstructor
@Service
public class UserService {

     private final Logger log = LoggerFactory.getLogger(getClass());
     private final UserRepository userRepository;
     private final BCryptPasswordEncoder passwordEncoder;

     @Transactional
     public JoinResponseDto 회원가입(JoinRequestDto joinRequestDto) {
          // 1. 동일 유저네임 검사
          System.out.println("테스트 : ");
          Optional<User> userOP = userRepository.findByUsername(joinRequestDto.getUsername());
          if (userOP.isPresent()) {
               throw new CustomApiException("동일한 유저네임이 존재합니다. ");
          }

          // 2. 패스워드 인코딩 + 회원가입
          User userPS = userRepository.save(joinRequestDto.toEntity(passwordEncoder));

          // 3. dto 응답
          return new JoinResponseDto(userPS);
     }
}
