package com.devd.spring.bookstorebillingservice.controller;

import com.devd.spring.bookstorebillingservice.service.AddressService;
import com.devd.spring.bookstorebillingservice.web.CreateAddressRequest;
import com.devd.spring.bookstorebillingservice.web.GetAddressResponse;
import com.devd.spring.bookstorebillingservice.web.UpdateAddressRequest;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: Devaraj Reddy,
 * Date : 2019-06-04
 */
@RestController
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/address")
    public ResponseEntity<Object> createAddress(@RequestBody CreateAddressRequest createAddressRequest) {
        long startTime = System.nanoTime();
        addressService.createAddress(createAddressRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("address.createAddress.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/address")
    public ResponseEntity<Object> updateAddress(@RequestBody UpdateAddressRequest updateAddressRequest) {
        long startTime = System.nanoTime();
        addressService.updateAddress(updateAddressRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("address.updateAddress.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/address")
    public ResponseEntity<List<GetAddressResponse>> getAddress() {
        long startTime = System.nanoTime();
        List<GetAddressResponse> address = addressService.getAddress();
        long endTime = System.nanoTime();
        meterRegistry.timer("address.getAddress.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<GetAddressResponse> getAddressById(@PathVariable("addressId") String addressId) {
        long startTime = System.nanoTime();
        GetAddressResponse address = addressService.getAddressById(addressId);
        long endTime = System.nanoTime();
        meterRegistry.timer("address.getAddressById.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<?> deleteAddressById(@PathVariable("addressId") String addressId) {
        long startTime = System.nanoTime();
        addressService.deleteAddressById(addressId);
        long endTime = System.nanoTime();
        meterRegistry.timer("address.deleteAddressById.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}