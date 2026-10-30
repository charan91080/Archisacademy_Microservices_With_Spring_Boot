package com.shop.order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
@Component
public class ProductClient {
 private final RestClient client;
 public ProductClient(RestClient.Builder builder,@Value("${services.product.base-url:http://localhost:8081}") String baseUrl){client=builder.baseUrl(baseUrl).build();}
 public ProductResponse getProduct(Long id){return client.get().uri("/api/products/{id}",id).retrieve().body(ProductResponse.class);}
 public record ProductResponse(Long id,String name,String description,BigDecimal price,Integer stock){}
}
