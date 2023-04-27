package shop.mtcoding.bank.domain.transaction;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.mtcoding.bank.domain.account.Account;

@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transaction_tb")
@Entity
@Getter
public class Transaction {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @ManyToOne(fetch = FetchType.LAZY)
     private Account withdrawAccount;

     @ManyToOne(fetch = FetchType.LAZY)
     private Account depositAccount;

     private Long amount; // 입금의 양

     private Long withdrawAccountBalance; // 1111 계좌 1000원 -> 500원 -> 200원(history 남기기)
     private Long depositAccountBalance;

     @Column(nullable = false)
     @Enumerated(EnumType.STRING)
     private TransactionEnum gubun; // WITHDRAW, DEPOSIT, TRANSFER, ALL

     // 계좌가 사라져도 로그는 남아야한다.
     private String sender;
     private String receiver;
     private String tel;

     @CreatedDate // insert
     @Column(nullable = false)
     private LocalDateTime createdAt;

     @LastModifiedDate // insert, update
     @Column(nullable = false)
     private LocalDateTime updatedAt;

     @Builder
     public Transaction(Long id, Account withdrawAccount, Account depositAccount, Long amount,
               Long withdrawAccountBalance, Long depositAccountBalance, TransactionEnum gubun, String sender,
               String receiver, String tel, LocalDateTime createdAt, LocalDateTime updatedAt) {
          this.id = id;
          this.withdrawAccount = withdrawAccount;
          this.depositAccount = depositAccount;
          this.amount = amount;
          this.withdrawAccountBalance = withdrawAccountBalance;
          this.depositAccountBalance = depositAccountBalance;
          this.gubun = gubun;
          this.sender = sender;
          this.receiver = receiver;
          this.tel = tel;
          this.createdAt = createdAt;
          this.updatedAt = updatedAt;
     }
}
