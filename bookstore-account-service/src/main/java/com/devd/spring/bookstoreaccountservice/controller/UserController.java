package com.devd.spring.bookstoreaccountservice.controller;

import com.devd.spring.bookstoreaccountservice.service.UserService;
import com.devd.spring.bookstoreaccountservice.web.CreateUserRequest;
import com.devd.spring.bookstoreaccountservice.web.GetUserInfoResponse;
import com.devd.spring.bookstoreaccountservice.web.GetUserResponse;
import com.devd.spring.bookstoreaccountservice.web.UpdateUserRequest;
import com.devd.spring.bookstoreaccountservice.web.UpdateUserRequestFromAdmin;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author: Devaraj Reddy, Date : 2019-06-30
 */
@RestController
@CrossOrigin
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private MeterRegistry meterRegistry;

  @PostMapping("/user")
  @PreAuthorize("hasAuthority('ADMIN_USER')")
  public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
    long startTime = System.nanoTime();
    String userId = userService.createUser(createUserRequest);
    long endTime = System.nanoTime();
    meterRegistry.timer("user.createUser.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest().path("/{userId}")
            .buildAndExpand(userId).toUri();

    return ResponseEntity.created(location).build();
  }

  @GetMapping("/user")
  @PreAuthorize("hasAuthority('ADMIN_USER')")
  public ResponseEntity<GetUserResponse> getUser(
          @RequestParam("userName") Optional<String> userName,
          @RequestParam("userId") Optional<String> userId) {
    long startTime = System.nanoTime();
    GetUserResponse user = null;
    if (userName.isPresent()) {
      user = userService.getUserByUserName(userName.get());
    } else if (userId.isPresent()) {
      user = userService.getUserByUserId(userId.get());
    }
    long endTime = System.nanoTime();
    meterRegistry.timer("user.getUser.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return ResponseEntity.ok(user);
  }

  @PutMapping("/user/{userId}")
  @PreAuthorize("hasAuthority('ADMIN_USER')")
  public ResponseEntity<?> updateUser(@PathVariable("userId") String userId,
                                      @RequestBody @Valid UpdateUserRequestFromAdmin updateUserRequestFromAdmin) {
    long startTime = System.nanoTime();
    userService.updateUser(userId, updateUserRequestFromAdmin);
    long endTime = System.nanoTime();
    meterRegistry.timer("user.updateUser.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/users")
  @PreAuthorize("hasAuthority('ADMIN_USER')")
  public ResponseEntity<List<GetUserResponse>> getAllUsers() {
    long startTime = System.nanoTime();
    List<GetUserResponse> allUsers = userService.getAllUsers();
    long endTime = System.nanoTime();
    meterRegistry.timer("user.getAllUsers.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return ResponseEntity.ok(allUsers);
  }

  @GetMapping("/userInfo")
  public ResponseEntity<GetUserInfoResponse> getUserInfo() {
    long startTime = System.nanoTime();
    GetUserInfoResponse userInfo = userService.getUserInfo();
    long endTime = System.nanoTime();
    meterRegistry.timer("user.getUserInfo.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return new ResponseEntity<>(userInfo, HttpStatus.OK);
  }

  @PutMapping("/userInfo")
  public ResponseEntity<?> updateUserInfo(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
    long startTime = System.nanoTime();
    userService.updateUserInfo(updateUserRequest);
    long endTime = System.nanoTime();
    meterRegistry.timer("user.updateUserInfo.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/user/{userId}")
  @PreAuthorize("hasAuthority('ADMIN_USER')")
  public ResponseEntity<?> deleteUserById(@PathVariable("userId") String userId) {
    long startTime = System.nanoTime();
    userService.deleteUserById(userId);
    long endTime = System.nanoTime();
    meterRegistry.timer("user.deleteUserById.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}