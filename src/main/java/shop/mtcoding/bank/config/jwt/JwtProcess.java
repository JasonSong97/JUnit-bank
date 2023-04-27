package shop.mtcoding.bank.config.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.domain.user.UserEnum;

public class JwtProcess {
     private final Logger log = LoggerFactory.getLogger(getClass());

     // JWT 생성
     public static String create(LoginUser loginUser) {
          String jwtToken = JWT.create()
                    .withSubject("bank") // jwt 제목
                    .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME))
                    .withClaim("id", loginUser.getUser().getId())
                    .withClaim("role", loginUser.getUser().getRole() + "") // ENUNM STRING 이기떄문에
                    .sign(Algorithm.HMAC512(JwtVO.SECRET));
          return JwtVO.TOKEN_PREFIX + jwtToken;
     }

     // JWT 검증 -> return 되는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입할 예정(강제 로그인)
     public static LoginUser verify(String token) {
          DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(token); // 검증 안되면 터짐
          // id랑 role 정보만 넣어라
          Long id = decodedJWT.getClaim("id").asLong();
          String role = decodedJWT.getClaim("role").asString();
          User user = User.builder().id(id).role(UserEnum.valueOf(role)).build();
          LoginUser loginUser = new LoginUser(user);
          return loginUser;
     }
}
