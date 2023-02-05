package by.npc.rates;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
class RatesConfiguration {
    @Bean
    public FilterRegistrationBean<RatesRequestResponseLoggingFilter> loggingFilter() {
        FilterRegistrationBean<RatesRequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>(new RatesRequestResponseLoggingFilter());
        registrationBean.setUrlPatterns(Collections.singleton("/rates/*"));
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RatesResponseSigningFilter> responseSigningFilter() {
        FilterRegistrationBean<RatesResponseSigningFilter> registrationBean = new FilterRegistrationBean<>(new RatesResponseSigningFilter());
        registrationBean.setUrlPatterns(Collections.singleton("/rates/*"));
        return registrationBean;
    }
}
