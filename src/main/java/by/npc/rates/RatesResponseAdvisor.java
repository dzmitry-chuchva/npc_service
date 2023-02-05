package by.npc.rates;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Optional;
import java.util.zip.CRC32;

@ControllerAdvice
class RatesResponseAdvisor implements ResponseBodyAdvice<String> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return String.class.isAssignableFrom(returnType.getParameterType())
                && StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public String beforeBodyWrite(String body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        CRC32 crc32 = new CRC32();
        // attempt to determine the charset from media type. in case of absence, use default of StringHttpMessageConverter.
        // the fallback approach is not reliable, as default charset for StringHttpMessageConverter may change on runtime.
        crc32.update(body.getBytes(Optional.ofNullable(selectedContentType.getCharset())
                .orElse(StringHttpMessageConverter.DEFAULT_CHARSET)));

        response.getHeaders().add("Checksum", String.valueOf(crc32.getValue()));
        return body;
    }
}
