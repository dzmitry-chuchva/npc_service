package by.npc.rates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping(value = "/")
@Slf4j
public class RatesController {
    private final RestTemplate restTemplate;

    public RatesController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("load")
    public String loadRates(@RequestParam(required = false) LocalDate date) {
        Rate[] rates = restTemplate.getForObject("https://www.nbrb.by/api/exrates/rates?periodicity=0&ondate={ondate}",
                Rate[].class, Optional.ofNullable(date).map(Object::toString).orElse(null));
        return "OK. Date: " + date + ". Rates loaded: " + rates.length;
    }

    @RestControllerAdvice(assignableTypes = RatesController.class)
    public static class RatesExceptionHandler {
        @ExceptionHandler(RestClientResponseException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public String unexpectedRemoteAPIResponse(RestClientResponseException e) {
            log.error("Unexpected response of remote API received. API response status was: {}", e.getStatusText());
            return "Remote API response was not what we expected. The status code was: [" + e.getStatusText() + "]";
        }

        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public String unexpected(Exception e) {
            log.error("Unexpected error occurred", e);
            return "Unexpected error has occurred. Associated message: [" + e.getMessage() + "]";
        }
    }
}
