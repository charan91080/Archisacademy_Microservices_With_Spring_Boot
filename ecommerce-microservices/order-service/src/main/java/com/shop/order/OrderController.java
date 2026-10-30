package com.shop.order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/orders")
public class OrderController {
 private final OrderRepository repo; private final ProductClient products;
 public OrderController(OrderRepository repo,ProductClient products){this.repo=repo;this.products=products;}
 @GetMapping public List<ShopOrder> all(){return repo.findAll();}
 @GetMapping("/{id}") public ShopOrder one(@PathVariable Long id){return repo.findById(id).orElseThrow(()->new IllegalArgumentException("Order not found: "+id));}
 @GetMapping("/customer/{customerId}") public List<ShopOrder> customer(@PathVariable Long customerId){return repo.findByCustomerId(customerId);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ShopOrder create(@Valid @RequestBody CreateOrderRequest req){
  ProductClient.ProductResponse p=products.getProduct(req.productId());
  if(p==null||p.stock()==null||p.stock()<req.quantity())throw new IllegalArgumentException("Product unavailable or insufficient stock");
  return repo.save(new ShopOrder(req.customerId(),req.productId(),req.quantity(),p.price()));
 }
 public record CreateOrderRequest(@NotNull @Positive Long customerId,@NotNull @Positive Long productId,@NotNull @Min(1) Integer quantity){}
}
