package shop.mtcoding.bank.domain.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

     // jpa query method
     // select * from account where number = :number
     // join fetch를 하면 조인해서 객체에 값을 미리 가져올 수 있다.
     // @Query("SELECT ac FROM Accout ac join fetch ac.user u WHERE ac.number =
     // :number")
     Optional<Account> findByNumber(Long number);

     // jpa query method
     // select * from account where user_id = :id
     List<Account> findByUser_id(Long id);

}
