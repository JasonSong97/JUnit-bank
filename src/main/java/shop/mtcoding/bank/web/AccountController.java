package shop.mtcoding.bank.web;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveReqeustDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.mtcoding.bank.handler.ex.CustomForbiddenException;
import shop.mtcoding.bank.service.AccountService;
import shop.mtcoding.bank.service.AccountService.AccountListResponseDto;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {

     private final AccountService accountService;

     @PostMapping("/s/account")
     public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveReqeustDto accountSaveReqeustDto,
               BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser) {
          AccountSaveResponseDto accountSaveResponseDto = accountService.계좌등록(accountSaveReqeustDto,
                    loginUser.getUser().getId());
          return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", accountSaveResponseDto), HttpStatus.CREATED);
     }

     // 인증이 필요하고, account 테이블에 login한 유저의 계좌만 주세요
     @GetMapping("/s/account/login-user")
     public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {
          AccountListResponseDto accountListResponseDto = accountService.계좌목록보기_유저별(loginUser.getUser().getId());
          return new ResponseEntity<>(new ResponseDto<>(1, "계획목록보기_유저별 성공", accountListResponseDto), HttpStatus.OK);
     }
}
