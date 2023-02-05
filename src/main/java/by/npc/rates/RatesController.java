package by.npc.rates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@Slf4j
public class RatesController {
    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;

    public RatesController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("load")
    public String loadRates(@RequestParam LocalDate date) {
        Rate[] rates = restTemplate.getForObject("https://www.nbrb.by/api/exrates/rates?periodicity=0&ondate={ondate}",
                Rate[].class, date.toString());

        // immediately process flat rates array into Rate::getAbbr keyed map to prepare faster lookup later
        Map<String, Rate> ratesMap = Arrays.stream(rates).collect(Collectors.toUnmodifiableMap(
                Rate::getAbbr,
                Function.identity()));

        cacheManager.getCache("rates").put(date, ratesMap);

        return "Loaded " + rates.length + " rates valid for " + date;
    }

    @GetMapping
    public String verifyRate(@RequestParam LocalDate date, @RequestParam String abbr) throws RatesReadinessException, RateNotFoundException {
        Rate rate = getRate(date, abbr);
        Rate previousDayRate;
        try {
            previousDayRate = getRate(date.minusDays(1), abbr);
        } catch (RatesReadinessException | RateNotFoundException e) {
            previousDayRate = null;
        }

        return "Rate of " + rate.getScale() + " " + rate.getAbbr() + " is " + rate.getRate() + " BYN (trending "
                + Optional.ofNullable(previousDayRate)
                .map(r -> switch (rate.getRate().divide(rate.getScale(), RoundingMode.HALF_UP)
                        .compareTo(r.getRate().divide(r.getScale(), RoundingMode.HALF_UP))) {
                    case 1 -> "up";
                    case -1 -> "down";
                    case 0 -> "same";
                    default -> throw new IllegalStateException();
                }).orElse("n/a")
                + ")";
    }

    private Rate getRate(LocalDate date, String abbr) throws RatesReadinessException, RateNotFoundException {
        Cache.ValueWrapper ratesWrapper = cacheManager.getCache("rates").get(date);
        if (ratesWrapper == null) {
            throw new RatesReadinessException(date);
        }

        Map<String, Rate> ratesMap = (Map<String, Rate>) ratesWrapper.get();
        Rate rate = ratesMap.get(abbr);
        if (rate == null) {
            throw new RateNotFoundException(date, abbr);
        }
        return rate;
    }

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
