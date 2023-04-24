package shop.mtcoding.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
      // save - 이미 만들어져 있다.
}
