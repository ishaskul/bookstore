package com.devd.spring.bookstoreaccountservice.filter;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class OAuthTimingFilter implements Filter {

    private final MeterRegistry meterRegistry;

    public OAuthTimingFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Target only /oauth/token requests
            if (httpRequest.getRequestURI().contains("/oauth/token")) {
                long startTime = System.nanoTime();

                try {
                    chain.doFilter(request, response);
                } finally {
                    long endTime = System.nanoTime();

                    meterRegistry.timer("oauth.token.timer", "instance", System.getenv("HOSTNAME"))
                            .record(endTime - startTime, TimeUnit.NANOSECONDS);
                }
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
