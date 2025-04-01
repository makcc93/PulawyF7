package pl.eurokawa.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface MoneyRepository extends JpaRepository<Money,Integer>, JpaSpecificationExecutor<Money> {
    @Query("SELECT m FROM Money m WHERE m.id = (SELECT MAX (m2.id) FROM Money m2)")
    Optional<Money> findLatestBalance();
//
//    @Query("SELECT SUM(m.deposit) FROM money m JOIN user u ON m.user_id = u.id WHERE m.user_id = :userId")
//    double getSumOfUserDeposit(@Param("userId") Integer userId);

    @Query("SELECT COALESCE(SUM(m.deposit),0) FROM Money m WHERE m.user.id = :userId")
    Double getSumOfUserDeposit(@Param("userId") Integer userId);

    @Query("SELECT m FROM Money m WHERE m.deposit IS NOT NULL ORDER BY m.id DESC")
    List<Money> findHistoryOfUserDeposits();
}
