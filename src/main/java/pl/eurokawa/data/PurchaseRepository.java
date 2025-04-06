package pl.eurokawa.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer>, JpaSpecificationExecutor<Purchase> {

    @Query("SELECT p FROM Purchase p WHERE p.isConfirmed = true ORDER BY p.id DESC")
    List<Purchase> findConfirmedPurchasesHistory();

    @Query("SELECT p FROM Purchase p JOIN p.user u WHERE p.isSaved = true AND p.isConfirmed = false ORDER BY p.id")
    List<Purchase> findSavedNotConfirmedPurchases();
}
