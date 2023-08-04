package shop.mtcoding.bank.handler.aop;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import shop.mtcoding.bank.handler.ex.CustomValidationException;

@Aspect // 관점
@Component // 메모리에 띄우기
public class CustomValidationAdvice {

     @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
     public void postMapping() {
     }

     @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
     public void putMapping() {
     }

     // Before, After
     @Around("putMapping() || postMapping()") // jointpoint 전, 후 제어
     public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
          Object[] args = proceedingJoinPoint.getArgs(); // joinpoint 매개변수
          for (Object arg : args) {
               if (arg instanceof BindingResult) {
                    BindingResult bindingResult = (BindingResult) arg;

                    if (bindingResult.hasErrors()) {
                         Map<String, String> errorMap = new HashMap<>();

                         for (FieldError error : bindingResult.getFieldErrors()) {
                              errorMap.put(error.getField(), error.getDefaultMessage());
                         }
                         throw new CustomValidationException("유효성검사 실패", errorMap);
                    }
               }
          }
          return proceedingJoinPoint.proceed();
     }
}
