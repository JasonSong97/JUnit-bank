package shop.mtcoding.bank.domain.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

     // jpa query method, selct * from account where number = :number
     Optional<Account> findByNumber(Long number);

     // jpa query method, selct * from account where user_id = :id
     List<Account> findByUser_id(Long id);
}
