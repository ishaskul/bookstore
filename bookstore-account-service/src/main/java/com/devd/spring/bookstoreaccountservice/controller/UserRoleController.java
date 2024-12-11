package com.devd.spring.bookstoreaccountservice.controller;

import com.devd.spring.bookstoreaccountservice.service.UserRoleService;
import com.devd.spring.bookstoreaccountservice.web.MapRoleToUsersRequest;
import com.devd.spring.bookstoreaccountservice.web.MapUserToRolesRequest;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * @author: Devaraj Reddy, Date : 2019-06-30
 */
@RestController
public class UserRoleController {

  @Autowired
  UserRoleService userRoleService;

  @Autowired
  private MeterRegistry meterRegistry;

  @PostMapping("/user/{userNameOrEmail}/roles")
  public void mapUserToRoles(@PathVariable("userNameOrEmail") String userNameOrEmail,
                             @RequestBody @Valid MapUserToRolesRequest mapUserToRolesRequest) {

    long startTime = System.nanoTime();
    userRoleService.mapUserToRoles(userNameOrEmail, mapUserToRolesRequest);
    long endTime = System.nanoTime();

    meterRegistry.timer("userRole.mapUserToRoles.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);
  }

  @PostMapping("/role/{roleName}/users")
  public void mapRoleToUsers(@PathVariable("roleName") String roleName,
                             @RequestBody @Valid MapRoleToUsersRequest mapRoleToUsersRequest) {

    long startTime = System.nanoTime();
    userRoleService.mapRoleToUsers(roleName, mapRoleToUsersRequest);
    long endTime = System.nanoTime();

    meterRegistry.timer("userRole.mapRoleToUsers.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);
  }
}