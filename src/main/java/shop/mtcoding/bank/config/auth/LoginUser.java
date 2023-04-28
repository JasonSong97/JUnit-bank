package shop.mtcoding.bank.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.domain.user.User;

@Getter
@RequiredArgsConstructor
public class LoginUser implements UserDetails { // 시큐리티를 알면 알 수 있다.

     private final User user;

     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() { // User의 권한을 가져오는 메소드 여러개일수 있으니까
          Collection<GrantedAuthority> authorities = new ArrayList<>();
          authorities.add(() -> "ROLE_" + user.getRole());
          return authorities;
     }

     @Override
     public String getPassword() {
          return user.getPassword();
     }

     @Override
     public String getUsername() {
          return user.getUsername();
     }

     @Override
     public boolean isAccountNonExpired() {
          return true;
     }

     @Override
     public boolean isAccountNonLocked() {
          return true;
     }

     @Override
     public boolean isCredentialsNonExpired() {
          return true;
     }

     @Override
     public boolean isEnabled() {
          return true;
     }

}