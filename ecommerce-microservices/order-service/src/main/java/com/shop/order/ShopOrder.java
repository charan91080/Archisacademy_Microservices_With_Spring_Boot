package com.shop.order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="shop_orders")
public class ShopOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long customerId;
 @Column(nullable=false) private Long productId;
 @Column(nullable=false) private Integer quantity;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal unitPrice;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal total;
 @Column(nullable=false) private String status;
 @Column(nullable=false) private Instant createdAt;
 protected ShopOrder(){}
 public ShopOrder(Long c,Long p,Integer q,BigDecimal price){customerId=c;productId=p;quantity=q;unitPrice=price;total=price.multiply(BigDecimal.valueOf(q));status="CREATED";createdAt=Instant.now();}
 public Long getId(){return id;} public Long getCustomerId(){return customerId;} public Long getProductId(){return productId;}
 public Integer getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public BigDecimal getTotal(){return total;}
 public String getStatus(){return status;} public Instant getCreatedAt(){return createdAt;}
}
