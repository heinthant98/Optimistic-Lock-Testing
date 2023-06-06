package com.lock.demo.service;

import com.lock.demo.entity.Product;
import com.lock.demo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Product updateProduct(Product updateProduct) {
        var product = repository.findById(updateProduct.getId()).orElseThrow(() -> new EntityNotFoundException("Product Not found"));
        if (updateProduct.getVersion() == product.getVersion()) {
            product.updateProductQuantity(updateProduct.getQuantity());
            return repository.save(product);
        } else {
            throw new OptimisticLockException();
        }
    }

}
