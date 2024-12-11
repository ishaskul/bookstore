package com.devd.spring.bookstoreorderservice.controller;

import com.devd.spring.bookstoreorderservice.service.CartItemService;
import com.devd.spring.bookstoreorderservice.web.CartItemRequest;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author: Devaraj Reddy,
 * Date : 2019-06-17
 */
@RestController
@CrossOrigin
public class CartItemController {

    @Autowired
    CartItemService cartItemService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/cart/cartItem")
    @ResponseStatus(value = HttpStatus.OK)
    public void addCartItem(@RequestBody CartItemRequest cartItemRequest) {
        long startTime = System.nanoTime();
        cartItemService.addCartItem(cartItemRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("cartItem.add.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
    }

    @DeleteMapping("/cart/cartItem/{cartItemId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeCartItem(@PathVariable(value = "cartItemId") String cartItemId) {
        long startTime = System.nanoTime();
        cartItemService.removeCartItem(cartItemId);
        long endTime = System.nanoTime();
        meterRegistry.timer("cartItem.remove.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
    }

    @DeleteMapping("/cart/cartItem")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeAllCartItems(@RequestParam(value = "cartId") String cartId) {
        long startTime = System.nanoTime();
        cartItemService.removeAllCartItems(cartId);
        long endTime = System.nanoTime();
        meterRegistry.timer("cartItem.removeAll.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
    }
}