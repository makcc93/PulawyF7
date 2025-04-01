package pl.eurokawa.services;

import org.springframework.stereotype.Service;
import pl.eurokawa.data.Product;
import pl.eurokawa.data.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
}
