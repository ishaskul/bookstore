package com.devd.spring.bookstoreaccountservice.controller;

import com.devd.spring.bookstoreaccountservice.repository.dao.Role;
import com.devd.spring.bookstoreaccountservice.service.RoleService;
import com.devd.spring.bookstoreaccountservice.web.CreateRoleRequest;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author: Devaraj Reddy, Date : 2019-06-30
 */
@RestController
public class RoleController {

  @Autowired
  private RoleService roleService;

  @Autowired
  private MeterRegistry meterRegistry;

  @PostMapping("/role")
  @PreAuthorize("hasAuthority('ADMIN_USER')")
  public ResponseEntity<?> createRole(@RequestBody @Valid CreateRoleRequest createRoleRequest) {

    long startTime = System.nanoTime();
    String userId = roleService.createRole(createRoleRequest);
    long endTime = System.nanoTime();

    meterRegistry.timer("role.createRole.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    URI location = ServletUriComponentsBuilder
            .fromCurrentRequest().path("/{roleId}")
            .buildAndExpand(userId).toUri();

    return ResponseEntity.created(location).build();
  }

  @GetMapping("/roles")
  @PreAuthorize("hasAuthority('ADMIN_USER')")
  public ResponseEntity<?> getAllRoles() {

    long startTime = System.nanoTime();
    List<Role> allRoles = roleService.getAllRoles();
    long endTime = System.nanoTime();

    meterRegistry.timer("role.getAllRoles.timer", "instance", System.getenv("HOSTNAME"))
            .record(endTime - startTime, TimeUnit.NANOSECONDS);

    return ResponseEntity.ok(allRoles);
  }

  //TODO CRUD for role
}