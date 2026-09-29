package com.amir.ecommerce.controller;

import com.amir.ecommerce.model.Product;
import com.amir.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor  // Better than @Autowire
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/product/{prodId}")
    public Product getProduct(@PathVariable int prodId) {
        return productService.getProductById(prodId);
    }

    @GetMapping("/product/{prodId}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable int prodId) {
        Product product = productService.getProductById(prodId);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found".getBytes());
        }

        MediaType mediaType = product.getImageType() == null
                ? MediaType.APPLICATION_OCTET_STREAM
                : MediaType.parseMediaType(product.getImageType());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(product.getImageData());
    }

    @PostMapping(value = "/product")
    public ResponseEntity<?> addProduct(
            Product product,
            MultipartFile imageFile) {
        try {
            Product product1 = productService.addProduct(product, imageFile);
            return new ResponseEntity<>(product1, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/product/{prodId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable int prodId,
            Product product,
            MultipartFile imageFile) {
        try {
            Product updatedProduct = productService.updateProduct(prodId, product, imageFile);

            if (updatedProduct != null) {
                return new ResponseEntity<>("Updated Successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/product/{prodId}")
    public ResponseEntity<String> deleteProduct(@PathVariable int prodId) {
        try {

            boolean isDeleted = productService.deleteProduct(prodId);
            if (isDeleted) {
                return new ResponseEntity<>("Deleted", HttpStatus.OK);
            } else return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {

        List<Product> products = productService.searchProducts(keyword);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

}
