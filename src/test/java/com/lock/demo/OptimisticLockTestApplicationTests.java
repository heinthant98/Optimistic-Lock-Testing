package com.lock.demo;

import com.lock.demo.entity.Product;
import com.lock.demo.repository.ProductRepository;
import com.lock.demo.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OptimisticLockTestApplicationTests {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Test
	public void testOptimisticLocking() throws InterruptedException {
		Product product = new Product("Product A", 10);
		productRepository.save(product);

		// Start the first transaction
		Product firstTransaction = productRepository.findById(1).orElseThrow(() -> new EntityNotFoundException("Data not found"));

		// Start the second transaction
		Product secondTransaction = productRepository.findById(1).orElseThrow(() -> new EntityNotFoundException("Data not found"));

		//update data in first transaction
		firstTransaction.setQuantity(product.getQuantity() - 2);
		productService.updateProductQuantity(firstTransaction);

		assertThat(firstTransaction.getQuantity()).isEqualTo(8);

		Thread.sleep(2000);

		//update same data in second transaction
		Assertions.assertThrows(OptimisticLockException.class, () -> {
			secondTransaction.setQuantity(product.getQuantity() - 5);
			productService.updateProductQuantity(secondTransaction);
		});
	}

}
