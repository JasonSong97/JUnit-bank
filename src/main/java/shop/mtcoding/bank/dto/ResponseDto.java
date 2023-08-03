package shop.mtcoding.bank.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseDto<T> {

     private final Integer code; // 1 성공 -1 실패
     private final String msg;
     private final T data; // 다양한 형태의 데이터가 오기 때문에
}
