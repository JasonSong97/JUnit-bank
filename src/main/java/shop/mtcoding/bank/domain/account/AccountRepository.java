package shop.mtcoding.bank.domain.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

     // jpa query method, selct * from account where number = :number
     Optional<Account> findByNumber(Long number);
}
