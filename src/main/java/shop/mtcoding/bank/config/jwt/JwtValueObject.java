package shop.mtcoding.bank.config.jwt;

public interface JwtValueObject {

     public static final String SECRET = "메타코딩"; // HS256 (대칭키), 환경변수
     public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 일주일
     public static final String TOKEN_PREFIX = "Bearer ";
     public static final String HEADER = "Authorization";
}
