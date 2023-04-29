package shop.mtcoding.bank.temp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LongTest {

     @Test
     public void long_test1() throws Exception {
          // given
          Long number1 = 1111L;
          Long number2 = 1111L;

          // when
          if (number1.longValue() == number2.longValue()) {
               System.out.println("테스트 : 동일합니다.");
          } else {
               System.out.println("테스트 : 동일하지 않습니다.");
          }

          Long amount1 = 1000L;
          Long amount2 = 1000L;

          if (amount1 < amount2) {
               System.out.println("테스트 : amount1이 작습니다.");
          } else {
               System.out.println("테스트 : amount1이 큽니다.");
          }
          // then

     }

     @Test
     public void long_test() throws Exception {
          // given (2^8 까지 - 256 범위 -127 ~ + 127)
          Long v1 = 128L;
          Long v2 = 128L;

          // when
          if (v1 == v2) {
               System.out.println("테스트 : 같습니다.");
          }

          // then

     }

     @Test
     public void long_test3() throws Exception {
          // given
          Long v1 = 128L;
          Long v2 = 128L;

          // when

          // then
          assertThat(v1).isEqualTo(v2);
     }
}
