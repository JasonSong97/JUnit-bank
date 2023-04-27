package shop.mtcoding.bank.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor // RequiredArgsConstructor: 응답의 DTO는 1번 만들면 수정할 일이 없어서
public class ResponseDto<T> {
     private final Integer code; // -1, 1
     private final String msg;
     private final T data;
}
