package pl.eurokawa.services;

import org.springframework.stereotype.Service;
import pl.eurokawa.data.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PurchaseService{

    PurchaseRepository purchaseRepository;
    ProductRepository productRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, ProductRepository productRepository){
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
    }

    public Purchase addPurchase(User user, Integer productId, Double price, Integer quantity){
        Product product = productRepository.findById(productId).orElseThrow();
        Purchase purchase = new Purchase(user, product, price, quantity);
        purchase.setSaved(true);

        return purchaseRepository.save(purchase);
    }


    public Purchase confirmPurchase(Product product, Integer quantity, Double price, Double total){
        Purchase purchase =  new Purchase();
        purchase.setProduct(product);
        purchase.setQuantity(quantity);
        purchase.setPrice(price);
        purchase.setTotal(total);

        if (purchase.isSaved() && !purchase.isConfirmed()){
            purchase.setConfirmed(true);
        }

        return purchaseRepository.save(purchase);
    }

    public List<Purchase> getConfirmedPurchases(){

        return purchaseRepository.findConfirmedPurchasesHistory();
    }

    public List<Purchase> getSavedNotConfirmedPurchases(){

        return purchaseRepository.findSavedNotConfirmedPurchases();
    }
}
