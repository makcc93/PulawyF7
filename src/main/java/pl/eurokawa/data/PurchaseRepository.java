package pl.eurokawa.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer>, JpaSpecificationExecutor<Purchase> {

    @Query("SELECT p FROM Purchase p WHERE p.isConfirmed = true ORDER BY p.id DESC")
    List<Purchase> findConfirmedPurchasesHistory();
}
