package shop.mtcoding.bank.domain.transaction;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.data.repository.query.Param;

import lombok.RequiredArgsConstructor;

interface Dao {
     List<Transaction> findTransactionList(@Param("accountId") Long accountId, @Param("gubun") String gubun,
               @Param("page") Integer page);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {

     private final EntityManager em;

     // JPQL
     // join fetch
     // outer join
     @Override
     public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) { // 동적쿼리 (구분값에 따라서)
          // JPQL
          String sql = "";
          sql += "select t from Transaction t ";

          if (gubun.equals("WITHDRAW")) {
               sql += "join fetch t.withdrawAccount wa ";
               sql += "where t.withdrawAccount.id = :withdrawAccountId ";
          } else if (gubun.equals("DEPOSIT")) {
               sql += "join fetch t.depositAccount da ";
               sql += "where t.depositAccount.id = :depositAccountId ";
          } else {
               // left join fetch(null o), join fetch(null x) 비교하기
               sql += "left join fetch t.withdrawAccount wa ";
               sql += "left join fetch t.depositAccount da ";
               sql += "where t.withdrawAccount.id = :withdrawAccountId ";
               sql += "or ";
               sql += "t.depositAccount.id = :depositAccountId";
          }

          TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

          if (gubun.equals("WITHDRAW")) {
               query = query.setParameter("withdrawAccountId", accountId);
          } else if (gubun.equals("DEPOSIT")) {
               query = query.setParameter("depositAccountId", accountId);
          } else {
               query = query.setParameter("withdrawAccountId", accountId);
               query = query.setParameter("depositAccountId", accountId);
          }

          query.setFirstResult(page * 5); // 0, 5, 10, 15
          query.setMaxResults(5);

          return query.getResultList();

     }

}
