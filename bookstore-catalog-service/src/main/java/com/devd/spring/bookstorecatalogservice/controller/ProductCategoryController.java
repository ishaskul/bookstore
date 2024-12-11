package com.devd.spring.bookstorecatalogservice.controller;

import com.devd.spring.bookstorecatalogservice.repository.dao.ProductCategory;
import com.devd.spring.bookstorecatalogservice.service.ProductCategoryService;
import com.devd.spring.bookstorecatalogservice.web.CreateProductCategoryRequest;
import com.devd.spring.bookstorecatalogservice.web.ProductCategoriesPagedResponse;
import com.devd.spring.bookstorecatalogservice.web.UpdateProductCategoryRequest;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author: Devaraj Reddy,
 * Date : 2019-06-06
 */
@RestController
public class ProductCategoryController {

    @Autowired
    ProductCategoryService productCategoryService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/productCategory")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity<?> createProductCategory(@RequestBody @Valid CreateProductCategoryRequest createProductCategoryRequest) {
        long startTime = System.nanoTime();
        String productCategory = productCategoryService.createProductCategory(createProductCategoryRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("productCategory.create.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{productCategoryId}")
                .buildAndExpand(productCategory).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/productCategory/{productCategoryId}")
    public ResponseEntity<ProductCategory> getProductCategory(@PathVariable("productCategoryId") String productCategoryId) {
        long startTime = System.nanoTime();
        ProductCategory productCategory = productCategoryService.getProductCategory(productCategoryId);
        long endTime = System.nanoTime();
        meterRegistry.timer("productCategory.get.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.ok(productCategory);
    }

    @DeleteMapping("/productCategory/{productCategoryId}")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity<?> deleteProductCategory(@PathVariable("productCategoryId") String productCategoryId) {
        long startTime = System.nanoTime();
        productCategoryService.deleteProductCategory(productCategoryId);
        long endTime = System.nanoTime();
        meterRegistry.timer("productCategory.delete.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/productCategory")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity<?> updateProductCategory(@RequestBody @Valid UpdateProductCategoryRequest updateProductCategoryRequest) {
        long startTime = System.nanoTime();
        productCategoryService.updateProductCategory(updateProductCategoryRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("productCategory.update.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/productCategories", produces = "application/json")
    public ResponseEntity<?> getAllProductCategories(@RequestParam(value = "sort", required = false) String sort,
                                                     @RequestParam(value = "page", required = false) Integer page,
                                                     @RequestParam(value = "size", required = false) Integer size,
                                                     PagedResourcesAssembler<ProductCategory> assembler) {
        long startTime = System.nanoTime();
        Page<ProductCategory> list = productCategoryService.getAllProductCategories(sort, page, size);

        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .toUriString());

        PagedModel<EntityModel<ProductCategory>> resource = assembler.toModel(list, link);

        ProductCategoriesPagedResponse productCategoriesPagedResponse = new ProductCategoriesPagedResponse();
        productCategoriesPagedResponse.setPage(list);

        if (resource.getLink("first").isPresent()) {
            productCategoriesPagedResponse.get_links().put("first", resource.getLink("first").get().getHref());
        }

        if (resource.getLink("prev").isPresent()) {
            productCategoriesPagedResponse.get_links().put("prev", resource.getLink("prev").get().getHref());
        }

        if (resource.getLink("self").isPresent()) {
            productCategoriesPagedResponse.get_links().put("self", resource.getLink("self").get().getHref());
        }

        if (resource.getLink("next").isPresent()) {
            productCategoriesPagedResponse.get_links().put("next", resource.getLink("next").get().getHref());
        }

        if (resource.getLink("last").isPresent()) {
            productCategoriesPagedResponse.get_links().put("last", resource.getLink("last").get().getHref());
        }

        long endTime = System.nanoTime();
        meterRegistry.timer("productCategory.getAll.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.ok(productCategoriesPagedResponse);
    }
}