package com.devd.spring.bookstorepaymentservice.controller;

import com.devd.spring.bookstorepaymentservice.service.PaymentMethodService;
import com.devd.spring.bookstorepaymentservice.web.CreatePaymentMethodRequest;
import com.devd.spring.bookstorepaymentservice.web.GetPaymentMethodResponse;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Devaraj Reddy, Date : 25-Jul-2020
 */
@RestController
@Slf4j
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/paymentMethod")
    public ResponseEntity<?> createPaymentMethod(@RequestBody @Valid CreatePaymentMethodRequest createPaymentMethodRequest) {
        long startTime = System.nanoTime();
        paymentMethodService.createPaymentMethod(createPaymentMethodRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("payment.create.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/paymentMethod")
    public ResponseEntity<?> getAllMyPaymentMethods() {
        long startTime = System.nanoTime();
        List<GetPaymentMethodResponse> allMyPaymentMethods = paymentMethodService.getAllMyPaymentMethods();
        long endTime = System.nanoTime();
        meterRegistry.timer("payment.getAll.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return new ResponseEntity<>(allMyPaymentMethods, HttpStatus.OK);
    }

    @GetMapping("/paymentMethod/{paymentMethodId}")
    public ResponseEntity<GetPaymentMethodResponse> getMyPaymentMethodById(@PathVariable("paymentMethodId") String paymentMethodId) {
        long startTime = System.nanoTime();
        GetPaymentMethodResponse paymentMethodDetail = paymentMethodService.getMyPaymentMethodById(paymentMethodId);
        long endTime = System.nanoTime();
        meterRegistry.timer("payment.getById.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return new ResponseEntity<>(paymentMethodDetail, HttpStatus.OK);
    }
}