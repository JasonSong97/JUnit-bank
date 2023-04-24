package shop.mtcoding.bank.temp;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

// java.util.regex.Pattern
public class RegexTest {

     @Test
     public void 한글만된다_test() throws Exception {
          String value = "한글";
          boolean result = Pattern.matches("^[가-힣]+$", value);
          System.out.println("테스트 : " + result);
     }

     @Test
     public void 한글은안된다_test() throws Exception {
          String value = "가";
          boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value);
          System.out.println("테스트 : " + result);
     }

     @Test
     public void 영어만된다_test() throws Exception {
          String value = "asd";
          boolean result = Pattern.matches("^[a-zA-Z]+$", value);
          System.out.println("테스트 : " + result);
     }

     @Test
     public void 영어는안된다_test() throws Exception {
          String value = "";
          boolean result = Pattern.matches("^[^a-zA-Z]*$", value);
          System.out.println("테스트 : " + result);
     }

     @Test
     public void 영어와숫자만된다_test() throws Exception {
          String value = "";
          boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
          System.out.println("테스트 : " + result);
     }

     @Test
     public void 영어만되고길이는최소2최대4이다_test() throws Exception {
          String value = "asd1";
          boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);
          System.out.println("테스트 : " + result);
     }

     // username, email, fullname test===================================
}
