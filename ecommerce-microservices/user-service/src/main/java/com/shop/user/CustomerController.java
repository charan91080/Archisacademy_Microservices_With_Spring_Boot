package com.shop.user;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/users")
public class CustomerController {
 private final CustomerRepository repo;
 public CustomerController(CustomerRepository repo){this.repo=repo;}
 @GetMapping public List<Customer> all(){return repo.findAll();}
 @GetMapping("/{id}") public Customer one(@PathVariable Long id){return repo.findById(id).orElseThrow(()->new IllegalArgumentException("User not found: "+id));}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Customer create(@Valid @RequestBody Customer c){
  if(repo.existsByEmail(c.getEmail()))throw new IllegalArgumentException("Email already registered");return repo.save(c);
 }
 @PutMapping("/{id}") public Customer update(@PathVariable Long id,@Valid @RequestBody Customer in){
  Customer c=one(id);c.setFullName(in.getFullName());c.setEmail(in.getEmail());c.setPhone(in.getPhone());return repo.save(c);
 }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){repo.delete(one(id));}
}
