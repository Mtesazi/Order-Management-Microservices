package com.pollinate.controller;

import com.pollinate.dto.ProductResponse;
import com.pollinate.exception.ResourceNotFoundException;
import com.pollinate.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.TestSecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProductShouldReturnCreatedProduct() throws Exception {
        ProductResponse response = new ProductResponse(
                1L,
                "Laptop",
                "Business laptop",
                new BigDecimal("1200.00"),
                5,
                null,
                null
        );

        when(productService.createProduct(any())).thenReturn(response);

        String request = """
                {
                  "name": "Laptop",
                  "description": "Business laptop",
                  "price": 1200.00,
                  "stockQuantity": 5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .with(httpBasic("apiuser", "apipassword"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.stockQuantity").value(5));
    }

    @Test
    void getProductByIdShouldReturnNotFoundWhenMissing() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product not found: 99"));

        mockMvc.perform(get("/api/products/99")
                        .with(httpBasic("apiuser", "apipassword")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product not found: 99"));
    }

    @Test
    void getAllProductsShouldReturnList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(
                new ProductResponse(1L, "Laptop", "Business laptop", new BigDecimal("1200.00"), 5, null, null),
                new ProductResponse(2L, "Mouse", "Wireless", new BigDecimal("25.00"), 10, null, null)
        ));

        mockMvc.perform(get("/api/products")
                        .with(httpBasic("apiuser", "apipassword")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
    }

    @Test
    void searchProductsShouldDelegateQueryParamsAndReturnFilteredList() throws Exception {
        when(productService.searchProducts("laptop", true, 1, 5)).thenReturn(List.of(
                new ProductResponse(2L, "Laptop Pro", "Business laptop", new BigDecimal("1599.00"), 3, null, null)
        ));

        mockMvc.perform(get("/api/products/search")
                        .with(httpBasic("apiuser", "apipassword"))
                        .param("name", "laptop")
                        .param("inStock", "true")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop Pro"));

        verify(productService).searchProducts("laptop", true, 1, 5);
    }

    @Test
    void updateStockShouldReturnUpdatedProduct() throws Exception {
        ProductResponse updated = new ProductResponse(
                1L,
                "Laptop",
                "Business laptop",
                new BigDecimal("1200.00"),
                9,
                null,
                null
        );

        when(productService.updateStock(1L, 9)).thenReturn(updated);

        mockMvc.perform(patch("/api/products/1/stock")
                        .with(httpBasic("apiuser", "apipassword"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockQuantity\":9}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stockQuantity").value(9));
    }

    @Test
    void productsEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void productsEndpointShouldRejectInvalidCredentials() throws Exception {
        mockMvc.perform(get("/api/products")
                        .with(httpBasic("apiuser", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .csrf(AbstractHttpConfigurer::disable)
                    .httpBasic(Customizer.withDefaults());
            return http.build();
        }

        @Bean
        UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
            return new InMemoryUserDetailsManager(
                    User.withUsername("apiuser")
                            .password(passwordEncoder.encode("apipassword"))
                            .roles("USER")
                            .build()
            );
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }
    }
}

