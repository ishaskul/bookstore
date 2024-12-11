package com.devd.spring.bookstoreorderservice.controller;

import com.devd.spring.bookstoreorderservice.web.CreateOrderRequest;
import com.devd.spring.bookstoreorderservice.service.OrderService;
import com.devd.spring.bookstoreorderservice.web.CreateOrderResponse;
import com.devd.spring.bookstoreorderservice.web.PreviewOrderRequest;
import com.devd.spring.bookstoreorderservice.web.PreviewOrderResponse;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: Devaraj Reddy,
 * Date : 2019-07-14
 */
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/order")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        long startTime = System.nanoTime();
        CreateOrderResponse createOrderResponse = orderService.createOrder(createOrderRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("order.create.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.ok(createOrderResponse);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<CreateOrderResponse> getOrderById(@PathVariable("orderId") String orderId) {
        long startTime = System.nanoTime();
        CreateOrderResponse createOrderResponse = orderService.getOrderById(orderId);
        long endTime = System.nanoTime();
        meterRegistry.timer("order.getById.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.ok(createOrderResponse);
    }

    @GetMapping("/order/myorders")
    public ResponseEntity<List<CreateOrderResponse>> getMyOrders() {
        long startTime = System.nanoTime();
        List<CreateOrderResponse> createOrderResponse = orderService.getMyOrders();
        long endTime = System.nanoTime();
        meterRegistry.timer("order.getMyOrders.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.ok(createOrderResponse);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity<List<CreateOrderResponse>> getAllOrders() {
        long startTime = System.nanoTime();
        List<CreateOrderResponse> createOrderResponse = orderService.getAllOrders();
        long endTime = System.nanoTime();
        meterRegistry.timer("order.getAllOrders.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.ok(createOrderResponse);
    }

    @PostMapping("/previewOrder")
    public ResponseEntity<PreviewOrderResponse> previewOrder(@RequestBody @Valid PreviewOrderRequest previewOrderRequest) {
        long startTime = System.nanoTime();
        PreviewOrderResponse previewOrderResponse = orderService.previewOrder(previewOrderRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("order.previewOrder.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.ok(previewOrderResponse);
    }
}