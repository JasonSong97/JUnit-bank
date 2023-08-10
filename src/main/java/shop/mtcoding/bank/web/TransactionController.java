package shop.mtcoding.bank.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.transaction.TransactionResponseDto.TransactionListResponseDto;
import shop.mtcoding.bank.service.TrsansactionService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TransactionController {

     private final TrsansactionService trsansactionService;

     @GetMapping("/s/account/{number}/transaction")
     public ResponseEntity<?> findTransactionList(@PathVariable Long number,
               @RequestParam(value = "gubun", defaultValue = "ALL") String gubun,
               @RequestParam(value = "page", defaultValue = "0") Integer page,
               @AuthenticationPrincipal LoginUser loginUser) {
          TransactionListResponseDto transactionListResponseDto = trsansactionService
                    .입출금목록보기(loginUser.getUser().getId(), number, gubun, page);
          return new ResponseEntity<>(new ResponseDto<>(1, "입출금목록보기 성공", transactionListResponseDto), HttpStatus.OK);
     }
}
