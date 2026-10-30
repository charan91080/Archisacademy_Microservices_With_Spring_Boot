package com.shop.product;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/products")
public class ProductController {
 private final ProductRepository repo;
 public ProductController(ProductRepository repo){this.repo=repo;}
 @GetMapping public List<Product> all(){return repo.findAll();}
 @GetMapping("/{id}") public Product one(@PathVariable Long id){return repo.findById(id).orElseThrow(()->new IllegalArgumentException("Product not found: "+id));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Product create(@Valid @RequestBody Product p){return repo.save(p);}
 @PutMapping("/{id}") public Product update(@PathVariable Long id,@Valid @RequestBody Product in){
  Product p=one(id);p.setName(in.getName());p.setDescription(in.getDescription());p.setPrice(in.getPrice());p.setStock(in.getStock());return repo.save(p);
 }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){repo.delete(one(id));}
}
