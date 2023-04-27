package shop.mtcoding.bank.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserRepository;

@Service // IoC
public class LoginService implements UserDetailsService {

     @Autowired
     private UserRepository userRepository;

     // 시큐리티로 로그인이 될때, 시큐리티가 loadUserByUsername() 실행 username 체크!!
     // 없음: 오류
     // 있음: SecurityContext 내부세션에 세션이 만들어진다.
     @Override
     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
          User userPS = userRepository.findByUsername(username).orElseThrow(
                    () -> new InternalAuthenticationServiceException("인증 실패"));
          return new LoginUser(userPS); // 세션에 만들어지는 객체
     }

}
