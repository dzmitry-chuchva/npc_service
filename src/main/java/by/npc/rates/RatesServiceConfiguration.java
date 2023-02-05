package by.npc.rates;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
class RatesServiceConfiguration {
    @Bean
    public FilterRegistrationBean<RatesServiceRequestResponseLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RatesServiceRequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>(new RatesServiceRequestResponseLoggingFilter());
        registrationBean.setUrlPatterns(Collections.singleton("/rates/*"));
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RatesServiceResponseSigningFilter> responseSigningFilter() {
        FilterRegistrationBean<RatesServiceResponseSigningFilter> registrationBean = new FilterRegistrationBean<>(new RatesServiceResponseSigningFilter());
        registrationBean.setUrlPatterns(Collections.singleton("/rates/*"));
        return registrationBean;
    }
}
