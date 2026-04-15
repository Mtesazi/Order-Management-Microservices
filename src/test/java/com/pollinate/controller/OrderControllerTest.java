package com.pollinate.controller;

import com.pollinate.dto.OrderItemResponse;
import com.pollinate.dto.OrderResponse;
import com.pollinate.exception.InsufficientStockException;
import com.pollinate.exception.ResourceNotFoundException;
import com.pollinate.model.OrderStatus;
import com.pollinate.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(OrderControllerTest.TestSecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrderShouldReturnCreatedOrder() throws Exception {
        OrderItemResponse item = new OrderItemResponse(
                1L,
                "Laptop",
                new BigDecimal("1200.00"),
                2,
                new BigDecimal("2400.00")
        );
        OrderResponse response = new OrderResponse(
                10L,
                List.of(item),
                OrderStatus.CREATED,
                new BigDecimal("2400.00"),
                null
        );

        when(orderService.createOrder(any())).thenReturn(response);

        String request = """
                {
                  "items": [
                    {"productId": 1, "quantity": 2}
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("apiuser", "apipassword"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(2400.00));
    }

    @Test
    void createOrderShouldReturnNotFoundWhenProductMissing() throws Exception {
        when(orderService.createOrder(any())).thenThrow(new ResourceNotFoundException("Product not found: 99"));

        String request = """
                {
                  "items": [
                    {"productId": 99, "quantity": 1}
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("apiuser", "apipassword"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product not found: 99"));
    }

    @Test
    void createOrderShouldReturnBadRequestWhenStockInsufficient() throws Exception {
        when(orderService.createOrder(any())).thenThrow(new InsufficientStockException("Insufficient stock for product 1"));

        String request = """
                {
                  "items": [
                    {"productId": 1, "quantity": 5}
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .with(httpBasic("apiuser", "apipassword"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Insufficient stock"))
                .andExpect(jsonPath("$.detail").value("Insufficient stock for product 1"));
    }

    @Test
    void getAllOrdersShouldReturnList() throws Exception {
        OrderResponse first = new OrderResponse(1L, List.of(), OrderStatus.CREATED, new BigDecimal("10.00"), null);
        OrderResponse second = new OrderResponse(2L, List.of(), OrderStatus.CREATED, new BigDecimal("20.00"), null);
        when(orderService.getAllOrders()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/orders")
                        .with(httpBasic("apiuser", "apipassword")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].totalAmount").value(20.00));
    }

    @Test
    void ordersEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ordersEndpointShouldRejectInvalidCredentials() throws Exception {
        mockMvc.perform(get("/api/orders")
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

