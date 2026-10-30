package com.shop.product;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
@Entity @Table(name="products")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Column(nullable=false) private String name;
 private String description;
 @NotNull @DecimalMin("0.01") @Column(nullable=false,precision=12,scale=2) private BigDecimal price;
 @NotNull @Min(0) @Column(nullable=false) private Integer stock;
 protected Product() {}
 public Product(String n,String d,BigDecimal p,Integer s){name=n;description=d;price=p;stock=s;}
 public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;}
 public BigDecimal getPrice(){return price;} public Integer getStock(){return stock;}
 public void setName(String v){name=v;} public void setDescription(String v){description=v;}
 public void setPrice(BigDecimal v){price=v;} public void setStock(Integer v){stock=v;}
}
