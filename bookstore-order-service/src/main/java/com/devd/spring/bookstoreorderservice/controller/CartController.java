package com.devd.spring.bookstoreorderservice.controller;

import com.devd.spring.bookstoreorderservice.web.CreateCartResponse;
import com.devd.spring.bookstoreorderservice.service.CartService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author: Devaraj Reddy,
 * Date : 2019-07-02
 */
@RestController
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/cart")
    @PreAuthorize("hasAuthority('STANDARD_USER') or hasAuthority('ADMIN_USER')")
    public ResponseEntity<CreateCartResponse> createCart() {
        long startTime = System.nanoTime();
        String cartId = cartService.createCart();
        long endTime = System.nanoTime();
        meterRegistry.timer("cart.create.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        CreateCartResponse createCartResponse = CreateCartResponse.builder()
                .cartId(cartId)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(createCartResponse);
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart() {
        long startTime = System.nanoTime();
        Object cart = cartService.getCart();
        long endTime = System.nanoTime();
        meterRegistry.timer("cart.get.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.ok(cart);
    }
}