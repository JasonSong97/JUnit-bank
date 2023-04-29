package shop.mtcoding.bank.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.mtcoding.bank.domain.account.Account;
import shop.mtcoding.bank.domain.account.AccountRepository;
import shop.mtcoding.bank.domain.transaction.Transaction;
import shop.mtcoding.bank.domain.transaction.TransactionRepository;
import shop.mtcoding.bank.dto.transaction.TransactionResponseDto.TransactionListResponseDto;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

     private final TransactionRepository transactionRepository;
     private final AccountRepository accountRepository;

     public TransactionListResponseDto 입출금목록보기(Long userId, Long accountNumber, String gubun, int page) {
          // stub
          Account accountPS = accountRepository.findByNumber(accountNumber).orElseThrow(
                    () -> new CustomApiException("해당 계좌를 찾을 수 없습니다."));

          accountPS.checkOnwer(userId);

          // stub
          List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountPS.getId(), gubun,
                    page);

          return new TransactionListResponseDto(transactionListPS, accountPS);
     }

}
