package shop.mtcoding.bank.config.jwt;

/**
 * SECRET 노출되면 안된다. (클라우드AWS - 환경변수, 파일에 있는 것을 읽을 수도 있고!)
 * reflesh Token(X)
 */
public interface JwtVO {

     public static final String SECRET = "메타코딩"; // HS256(대칭키) -> PATH 설정 가능
     public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 만료시간 1주일
     public static final String TOKEN_PREFIX = "Bearer "; // 1칸 띄우기
     public static final String HEADER = "Authorization";
}
