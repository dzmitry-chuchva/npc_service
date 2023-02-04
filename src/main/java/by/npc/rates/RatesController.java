package by.npc.rates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/")
@Slf4j
public class RatesController {
    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;

    public RatesController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("load")
    public String loadRates(LocalDate date) {
        Rate[] rates = restTemplate.getForObject("https://www.nbrb.by/api/exrates/rates?periodicity=0&ondate={ondate}",
                Rate[].class, date.toString());
        cacheManager.getCache("rates").put(date, rates);
        return "Loaded " + rates.length + " rates valid for " + date;
    }

    @GetMapping
    public String verifyRate(LocalDate date, String abbr) throws RatesReadinessException, RateNotFoundException {
        Cache.ValueWrapper ratesWrapper = cacheManager.getCache("rates").get(date);
        if (ratesWrapper == null) {
            throw new RatesReadinessException(date);
        }

        Rate[] rates = (Rate[]) ratesWrapper.get();
        for (Rate r : rates) {
            if (r.getAbbr().equals(abbr)) {
                return "Rate of " + r.getScale() + " " + r.getAbbr() + " is " + r.getRate() + " BYN";
            }
        }

        throw new RateNotFoundException(date, abbr);
    }

    @RestControllerAdvice(assignableTypes = RatesController.class)
    public static class RatesExceptionHandler {
        @ExceptionHandler(RatesReadinessException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public String ratesReadiness(RatesReadinessException e) {
            log.error("Rates not ready", e);
            return "No rates loaded yet for " + e.getOnDate();
        }

        @ExceptionHandler(RateNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public String ratePresence(RateNotFoundException e) {
            log.info("Rate not found", e);
            return "Rate for " + e.getAbbr() + " doesn't exist for " + e.getDate();
        }

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
