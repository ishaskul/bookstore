package com.devd.spring.bookstorecatalogservice.controller;

import com.devd.spring.bookstorecatalogservice.repository.dao.Review;
import com.devd.spring.bookstorecatalogservice.service.ReviewService;
import com.devd.spring.bookstorecatalogservice.web.CreateOrUpdateReviewRequest;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Devaraj Reddy, Date : 08-Nov-2020
 */
@RestController
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/review")
    public ResponseEntity<?> createOrUpdateReview(@RequestBody @Valid CreateOrUpdateReviewRequest createOrUpdateReviewRequest) {
        long startTime = System.nanoTime();
        reviewService.createOrUpdateReview(createOrUpdateReviewRequest);
        long endTime = System.nanoTime();
        meterRegistry.timer("review.createOrUpdate.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/review")
    public ResponseEntity<?> getAllReviewsForProduct(@RequestParam("productId") String productId) {
        long startTime = System.nanoTime();
        List<Review> reviewsForProduct = reviewService.getReviewsForProduct(productId);
        long endTime = System.nanoTime();
        meterRegistry.timer("review.getAllForProduct.timer", "instance", System.getenv("HOSTNAME"))
                .record(endTime - startTime, TimeUnit.NANOSECONDS);

        return ResponseEntity.ok(reviewsForProduct);
    }
}