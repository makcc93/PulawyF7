package pl.eurokawa.services;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
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

    public Purchase addPurchase(User user, Integer productId, Double price, Integer quantity,String receiptImagePath){
        Product product = productRepository.findById(productId).orElseThrow();
        Purchase purchase = new Purchase(user, product, price, quantity,receiptImagePath);
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

    public static Image getPurchasePhoto(Purchase purchase){
        if (purchase.getReceiptImagePath() != null) {
            String fileName = purchase.getReceiptImagePath();

            Image image = new Image(purchase.getReceiptImagePath(), "Photo");
            image.setWidth("50px");

            image.addClickListener(event ->{
                Dialog dialog = new Dialog();
                dialog.setModal(true);
                dialog.setDraggable(true);
                dialog.setResizable(false);
                dialog.setCloseOnEsc(true);
                dialog.setCloseOnOutsideClick(true);

                Image fullImage = new Image(purchase.getReceiptImagePath(),"Full size photo");
                fullImage.setMaxHeight("100%");
                fullImage.setMaxWidth("100%");

                fullImage.getStyle()
                        .set("margin", "auto")
                        .set("dispay","block")
                        .set("object-fit","contain");

                dialog.add(fullImage);
                dialog.setMaxHeight("100%");
                dialog.setMaxWidth("100%");

                dialog.open();
            });

            return image;
        } else {
            return null;
        }
    }

    public List<Purchase> getSavedNotConfirmedPurchases(){

        return purchaseRepository.findSavedNotConfirmedPurchases();
    }
}
