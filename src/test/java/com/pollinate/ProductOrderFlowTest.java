package com.pollinate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductOrderFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateProductAndPlaceOrder() throws Exception {
        String createProductJson = """
                {
                  "name": "Laptop",
                  "description": "14-inch business laptop",
                  "price": 1200.00,
                  "stockQuantity": 5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProductJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        String createOrderJson = """
                {
                  "items": [
                    {"productId": 1, "quantity": 2}
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalAmount").value(2400.0))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(3));
    }

    @Test
    void shouldRejectOrderWhenStockIsInsufficient() throws Exception {
        String createProductJson = """
                {
                  "name": "Mouse",
                  "description": "Wireless mouse",
                  "price": 25.00,
                  "stockQuantity": 1
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProductJson))
                .andExpect(status().isCreated());

        String createOrderJson = """
                {
                  "items": [
                    {"productId": 1, "quantity": 2}
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Insufficient stock"));
    }
}
