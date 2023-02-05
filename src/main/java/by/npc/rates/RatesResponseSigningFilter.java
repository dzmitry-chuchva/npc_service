package by.npc.rates;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.zip.CRC32;

class RatesResponseSigningFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            byte[] responseBytes = responseWrapper.getContentAsByteArray();
            CRC32 crc32 = new CRC32();
            crc32.update(responseBytes);
            responseWrapper.addHeader("Checksum", String.valueOf(crc32.getValue()));
            responseWrapper.copyBodyToResponse();
        }
    }
}
