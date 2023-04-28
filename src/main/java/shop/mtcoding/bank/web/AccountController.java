package shop.mtcoding.bank.web;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.config.auth.LoginUser;
import shop.mtcoding.bank.dto.ResponseDto;
import shop.mtcoding.bank.dto.account.AccountRequestDto.AccountSaveReqeustDto;
import shop.mtcoding.bank.dto.account.AccountResponseDto.AccountSaveResponseDto;
import shop.mtcoding.bank.service.AccountService;

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
}
