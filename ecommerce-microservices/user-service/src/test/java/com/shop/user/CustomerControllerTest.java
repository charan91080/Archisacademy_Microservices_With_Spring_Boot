package com.shop.user;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
 @Autowired MockMvc mvc; @MockBean CustomerRepository repo;
 @Test void returnsUsers() throws Exception {
  given(repo.findAll()).willReturn(List.of(new Customer("Asha","asha@example.com","9876543210")));
  mvc.perform(get("/api/users")).andExpect(status().isOk()).andExpect(jsonPath("$[0].email").value("asha@example.com"));
 }
}
