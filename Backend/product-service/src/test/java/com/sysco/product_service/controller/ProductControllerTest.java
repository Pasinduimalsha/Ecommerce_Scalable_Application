//package com.sysco.product_service.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sysco.product_service.dto.ProductDTO;
//import com.sysco.product_service.service.ProductService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//@WebMvcTest(ProductController.class)
//class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ProductService productService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void createProduct_ReturnsCreated() throws Exception {
//        ProductDTO request = ProductDTO.builder()
//                .name("Test Product")
//                .price(10.0)
//                .category("Test Category")
//                .producer("Test Producer")
//                .build();
//
//        ProductDTO response = ProductDTO.builder()
//                .id(1L)
//                .name("Test Product")
//                .price(10.0)
//                .category("Test Category")
//                .producer("Test Producer")
//                .build();
//
//        Mockito.when(productService.createProduct(Mockito.any(ProductDTO.class))).thenReturn(response);
//
//        mockMvc.perform(post("/api/v1/products/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.name").value("Test Product"));
//    }
//}
