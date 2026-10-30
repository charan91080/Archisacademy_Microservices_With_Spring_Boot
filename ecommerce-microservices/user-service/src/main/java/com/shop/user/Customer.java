package com.shop.user;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
@Entity @Table(name="customers")
public class Customer {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Column(nullable=false) private String fullName;
 @Email @NotBlank @Column(nullable=false,unique=true) private String email;
 @NotBlank @Column(nullable=false) private String phone;
 protected Customer(){}
 public Customer(String n,String e,String p){fullName=n;email=e;phone=p;}
 public Long getId(){return id;} public String getFullName(){return fullName;} public String getEmail(){return email;} public String getPhone(){return phone;}
 public void setFullName(String v){fullName=v;} public void setEmail(String v){email=v;} public void setPhone(String v){phone=v;}
}
