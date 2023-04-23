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
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transaction_tb")
@Entity
public class Transaction {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @ManyToOne(fetch = FetchType.LAZY)
      private Account withdrawAccount;

      @ManyToOne(fetch = FetchType.LAZY)
      private Account depositAccount;

      private Long amount;

      // 히스토리 내역을 남기기 위해서 만든 필드
      private Long withdrawAccountBalance; // 1111계좌 -> 1000 -> 500 -> 200
      private Long depositAccountBalance;

      @Enumerated(EnumType.STRING)
      @Column(nullable = false)
      private TransactionEnum gubun; // WITHDRAW, DEPOSIT, TRANSFER, ALL

      // 계좌가 사라져도 로그는 남아야 한다.
      private String sender;
      private String receiver;
      private String tel;

      @CreatedDate
      @Column(nullable = false)
      private LocalDateTime createdAt;

      @LastModifiedDate
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
