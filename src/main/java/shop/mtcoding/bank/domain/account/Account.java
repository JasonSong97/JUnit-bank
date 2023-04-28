package shop.mtcoding.bank.domain.account;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import shop.mtcoding.bank.domain.user.User;
import shop.mtcoding.bank.handler.ex.CustomApiException;

@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
@Entity
@Getter
public class Account {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
     @Column(unique = true, nullable = false, length = 4)
     private Long number; // 계좌번호
     @Column(nullable = false, length = 4)
     private Long password; // 계좌비번
     @Column(nullable = false)
     private Long balance; // 잔약(기본값 1000원)

     // 항상 ORM에서 fk의 주인은 Many Entity 쪽이다.
     @ManyToOne(fetch = FetchType.LAZY) // account.getUser().아무필드호출() == Lazy 발동
     private User user; // 특별한 설정 하지않으면, user_id

     @CreatedDate // insert
     @Column(nullable = false)
     private LocalDateTime createdAt;

     @LastModifiedDate // insert, update
     @Column(nullable = false)
     private LocalDateTime updatedAt;

     @Builder
     public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createdAt,
               LocalDateTime updatedAt) {
          this.id = id;
          this.number = number;
          this.password = password;
          this.balance = balance;
          this.user = user;
          this.createdAt = createdAt;
          this.updatedAt = updatedAt;
     }

     public void checkOnwer(Long userId) {
          // String testUsername = user.getUsername();
          // System.out.println("테스트 : " + testUsername);
          if (user.getId() != userId) { // Lazy 로등이여도 id를 조회할 때는 select 쿼리가 날라가지 않는다.
               throw new CustomApiException("계좌 소유자가 아닙니다.");
          }
     }
}
