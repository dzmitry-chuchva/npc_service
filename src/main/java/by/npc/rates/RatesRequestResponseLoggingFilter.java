package by.npc.rates;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
class RatesRequestResponseLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Rates request: " + request.getMethod() + " " + request.getRequestURL().append(request.getQueryString()));
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            byte[] responseBytes = responseWrapper.getContentAsByteArray();
            String responseStr = new String(responseBytes, responseWrapper.getCharacterEncoding());
            log.info("Rates response: [" + responseStr + "]");
            responseWrapper.copyBodyToResponse();
        }
    }
}
