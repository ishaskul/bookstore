package com.devd.spring.bookstoreaccountservice.controller;

import com.devd.spring.bookstoreaccountservice.service.AuthService;
import com.devd.spring.bookstoreaccountservice.web.CreateOAuthClientRequest;
import com.devd.spring.bookstoreaccountservice.web.CreateOAuthClientResponse;
import com.devd.spring.bookstoreaccountservice.web.CreateUserResponse;
import com.devd.spring.bookstoreaccountservice.web.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.instrument.MeterRegistry;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * @author: Devaraj Reddy, Date : 2019-05-18
 */
@RestController
@CrossOrigin
public class AuthController {

  @Autowired
  AuthService authService;

  @Autowired
  private MeterRegistry meterRegistry;

  @PostMapping("/client")
  @PreAuthorize("hasAuthority('ADMIN_USER')")
  public ResponseEntity<CreateOAuthClientResponse> createOAuthClient(
          @Valid @RequestBody CreateOAuthClientRequest createOAuthClientRequest) {

    long startTime = System.nanoTime();
    CreateOAuthClientResponse oAuthClient = authService.createOAuthClient(createOAuthClientRequest);
    long endTime = System.nanoTime();

    meterRegistry.timer("auth.createOAuthClient.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return new ResponseEntity<>(oAuthClient, HttpStatus.CREATED);
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

    long startTime = System.nanoTime();
    CreateUserResponse createUserResponse = authService.registerUser(signUpRequest);
    long endTime = System.nanoTime();

    meterRegistry.timer("auth.registerUser.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return new ResponseEntity<>(createUserResponse, HttpStatus.CREATED);
  }
}