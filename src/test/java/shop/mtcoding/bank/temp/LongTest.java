package shop.mtcoding.bank.temp;

import org.junit.jupiter.api.Test;

public class LongTest {

     @Test
     public void long_test1() throws Exception {
          Long long1 = 1111L;
          Long long2 = 1111L;

          if (long1 == long2) { // longValue() 추가, 대소 비교시 longValue() 필요 X
               System.out.println("테스트 : 동일합니다. ");
          } else {
               System.out.println("테스트 : 동일하지 않습니다. ");
          }
     }
}
