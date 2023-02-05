package by.npc.rates;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Collections;

@Configuration
class RatesConfiguration {
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        return filter;
    }

    @Bean
    public FilterRegistrationBean<RatesResponseSigningFilter> responseSigningFilter() {
        FilterRegistrationBean<RatesResponseSigningFilter> registrationBean = new FilterRegistrationBean<>(new RatesResponseSigningFilter());
        registrationBean.setUrlPatterns(Collections.singleton("/rates/*"));
        return registrationBean;
    }
}
