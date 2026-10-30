package com.shop.order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(OrderController.class)
class OrderControllerTest {
 @Autowired MockMvc mvc; @MockBean OrderRepository repo; @MockBean ProductClient products;
 @Test void returnsEmptyOrders() throws Exception {
  given(repo.findAll()).willReturn(List.of());
  mvc.perform(get("/api/orders")).andExpect(status().isOk()).andExpect(content().json("[]"));
 }
}
