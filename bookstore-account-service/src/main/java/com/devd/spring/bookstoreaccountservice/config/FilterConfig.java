package com.devd.spring.bookstoreaccountservice.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.core.instrument.MeterRegistry;
import com.devd.spring.bookstoreaccountservice.filter.OAuthTimingFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<OAuthTimingFilter> oAuthTimingFilterRegistration(MeterRegistry meterRegistry) {
        FilterRegistrationBean<OAuthTimingFilter> registrationBean = new FilterRegistrationBean<>();

        // Register the OAuthTimingFilter with the MeterRegistry, which will be injected by Spring
        registrationBean.setFilter(new OAuthTimingFilter(meterRegistry));

        // Apply the filter only to the /oauth/token endpoint
        registrationBean.addUrlPatterns("/oauth/token");

        // Optional: Set filter order or name (if needed)
        registrationBean.setName("OAuthTimingFilter");
        registrationBean.setOrder(1); // Ensure proper execution order if multiple filters exist

        return registrationBean;
    }
}
