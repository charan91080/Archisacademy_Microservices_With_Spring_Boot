package com.shop.order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrderRepository extends JpaRepository<ShopOrder,Long>{List<ShopOrder> findByCustomerId(Long customerId);}
