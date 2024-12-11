package com.devd.spring.bookstorecatalogservice.controller;

import com.devd.spring.bookstorecatalogservice.service.ProductService;
import com.devd.spring.bookstorecatalogservice.web.CreateProductRequest;
import com.devd.spring.bookstorecatalogservice.web.ProductResponse;
import com.devd.spring.bookstorecatalogservice.web.ProductsPagedResponse;
import com.devd.spring.bookstorecatalogservice.web.UpdateProductRequest;
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
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/product")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity<?> createProduct(@RequestBody @Valid CreateProductRequest createProductRequest) {
        long startTime = System.nanoTime();
        String product = productService.createProduct(createProductRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("product.create.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{productId}")
                .buildAndExpand(product).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("productId") String productId) {
        long startTime = System.nanoTime();
        ProductResponse product = productService.getProduct(productId);
        long endTime = System.nanoTime();
        meterRegistry.timer("product.get.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/product/{productId}")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity<?> deleteProductCategory(@PathVariable("productId") String productId) {
        long startTime = System.nanoTime();
        productService.deleteProduct(productId);
        long endTime = System.nanoTime();
        meterRegistry.timer("product.delete.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/product")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid UpdateProductRequest updateProductRequest) {
        long startTime = System.nanoTime();
        productService.updateProduct(updateProductRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("product.update.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/products", produces = "application/json")
    public ResponseEntity<?> getAllProducts(@RequestParam(value = "sort", required = false) String sort,
                                            @RequestParam(value = "page", required = false) Integer page,
                                            @RequestParam(value = "size", required = false) Integer size,
                                            PagedResourcesAssembler<ProductResponse> assembler) {
        long startTime = System.nanoTime();
        Page<ProductResponse> list = productService.getAllProducts(sort, page, size);

        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString());

        PagedModel<EntityModel<ProductResponse>> resource = assembler.toModel(list, link);

        ProductsPagedResponse productsPagedResponse = new ProductsPagedResponse();
        productsPagedResponse.setPage(list);

        if (resource.getLink("first").isPresent()) {
            productsPagedResponse.get_links().put("first", resource.getLink("first").get().getHref());
        }

        if (resource.getLink("prev").isPresent()) {
            productsPagedResponse.get_links().put("prev", resource.getLink("prev").get().getHref());
        }

        if (resource.getLink("self").isPresent()) {
            productsPagedResponse.get_links().put("self", resource.getLink("self").get().getHref());
        }

        if (resource.getLink("next").isPresent()) {
            productsPagedResponse.get_links().put("next", resource.getLink("next").get().getHref());
        }

        if (resource.getLink("last").isPresent()) {
            productsPagedResponse.get_links().put("last", resource.getLink("last").get().getHref());
        }

        long endTime = System.nanoTime();
        meterRegistry.timer("product.getAll.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.ok(productsPagedResponse);
    }
}