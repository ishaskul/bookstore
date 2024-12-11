package com.devd.spring.bookstorepaymentservice.controller;

import com.devd.spring.bookstorepaymentservice.service.PaymentsService;
import com.devd.spring.bookstorepaymentservice.web.CreatePaymentRequest;
import com.devd.spring.bookstorepaymentservice.web.CreatePaymentResponse;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * @author Devaraj Reddy, Date : 25-Jul-2020
 */
@RestController
@Slf4j
public class PaymentsController {

    @Autowired
    private PaymentsService paymentsService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/pay")
    public ResponseEntity<?> doPayment(@RequestBody @Valid CreatePaymentRequest createPaymentRequest) {
        long startTime = System.nanoTime();
        CreatePaymentResponse paymentRequest = paymentsService.createPaymentRequest(createPaymentRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("payment.create.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return new ResponseEntity<>(paymentRequest, HttpStatus.CREATED);
    }
}