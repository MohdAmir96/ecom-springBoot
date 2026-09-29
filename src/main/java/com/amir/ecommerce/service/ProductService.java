package com.amir.ecommerce.service;

import com.amir.ecommerce.model.Product;
import com.amir.ecommerce.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(int prodId) {
        return productRepo.findById(prodId).orElse(null);
    }

    public Product addProduct(Product product, MultipartFile imageFile) throws IOException {
        String fileName = imageFile.getOriginalFilename();

        product.setImageData(imageFile.getBytes());
        product.setImageName(fileName);
        product.setImageType(imageFile.getContentType());

        return productRepo.save(product);
    }


    @Transactional
    public Product updateProduct(int id, Product product, MultipartFile imageFile) throws IOException {
        // 1. Fetch existing product
        Product existingProduct = productRepo.findById(id).orElse(null);
        if (existingProduct == null) {
            return null;
        }

        // 2. Update scalar fields matching your model
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setReleaseDate(product.getReleaseDate());
        existingProduct.setAvailable(product.isAvailable());
        existingProduct.setQuantity(product.getQuantity());

        // 3. Update image fields only if a new non-empty file is uploaded
        if (imageFile != null && !imageFile.isEmpty()) {
            existingProduct.setImageName(imageFile.getOriginalFilename());
            existingProduct.setImageType(imageFile.getContentType());
            existingProduct.setImageData(imageFile.getBytes());
        }

        // 4. Save updated entity
        return productRepo.save(existingProduct);
    }

    public boolean deleteProduct(int prodId) {
        if (productRepo.existsById(prodId)) {
            productRepo.deleteById(prodId);
            return true;
        }
        ;
        return false;
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword);
    }
}
