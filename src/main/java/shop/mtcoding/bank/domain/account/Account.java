package shop.mtcoding.bank.domain.account;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb", indexes = {
          @Index(name = "idx_account_number", columnList = "number")
})
@Entity
public class Account {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
     @Column(unique = true, nullable = false, length = 4)
     private Long number; // 계좌변호
     @Column(nullable = false, length = 4)
     private Long password; // 계좌비번
     @Column(nullable = false)
     private Long balance; // 잔액(기본 1000원)

     @ManyToOne(fetch = FetchType.LAZY)
     private User user;
     @CreatedDate
     @Column(nullable = false)
     private LocalDateTime createAt;
     @LastModifiedDate
     @Column(nullable = false)
     private LocalDateTime updateAt;

     @Builder
     public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createAt,
               LocalDateTime updateAt) {
          this.id = id;
          this.number = number;
          this.password = password;
          this.balance = balance;
          this.user = user;
          this.createAt = createAt;
          this.updateAt = updateAt;
     }

     public void checkOwner(Long userId) {
          if (user.getId() != userId) { // lazy -> id는 조회 가능, SELECT 쿼리 X
               throw new CustomApiException("계좌 소유자가 아닙니다. ");
          }
     }
}
