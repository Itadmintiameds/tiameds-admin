package com.example.tiamedsadmin.controller;

import com.example.tiamedsadmin.entity.TestProduct;
import com.example.tiamedsadmin.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/products/dummy")
    public List<TestProduct> getDummyProducts() {
        return testService.getDummyProducts();
    }
}