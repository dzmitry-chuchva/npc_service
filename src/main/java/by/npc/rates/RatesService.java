package by.npc.rates;

import by.npc.rates.external.ExternalRate;
import by.npc.rates.external.ExternalRatesService;
import by.npc.rates.external.ExternalRatesServiceResponseException;
import by.npc.rates.store.Rate;
import by.npc.rates.store.RateNotFoundException;
import by.npc.rates.store.RatesReadinessException;
import by.npc.rates.store.RatesStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rates")
@Slf4j
@RequiredArgsConstructor
public class RatesService {
    private final RatesStorage ratesStorage;
    private final ExternalRatesService externalRatesService;

    @GetMapping("load")
    public LoadRatesResponse loadRates(@RequestParam LocalDate date) throws ExternalRatesServiceResponseException {
        List<ExternalRate> externalRates = externalRatesService.getRates(date);
        ratesStorage.storeRates(date, toInternalRates(externalRates));
        return LoadRatesResponse.builder()
                .status("OK")
                .count(externalRates.size())
                .date(date)
                .build();
    }

    @GetMapping("verify")
    public VerifyRateResponse verifyRate(@RequestParam LocalDate date, @RequestParam String abbr) throws RatesReadinessException, RateNotFoundException {
        Rate rate = ratesStorage.getRate(date, abbr);
        Rate previousDayRate;
        try {
            previousDayRate = ratesStorage.getRate(date.minusDays(1), abbr);
        } catch (RatesReadinessException | RateNotFoundException e) {
            previousDayRate = null;
        }

        return VerifyRateResponse.builder()
                .abbr(rate.getAbbr())
                .rate(rate.getRate())
                .scale(rate.getScale())
                .trend(Optional.ofNullable(previousDayRate)
                        .map(prev -> switch (rate.getRate().divide(rate.getScale(), RoundingMode.HALF_UP)
                                .compareTo(prev.getRate().divide(prev.getScale(), RoundingMode.HALF_UP))) {
                            case 1 -> VerifyRateResponse.Trend.UP;
                            case -1 -> VerifyRateResponse.Trend.DOWN;
                            case 0 -> VerifyRateResponse.Trend.SAME;
                            default -> throw new IllegalStateException();
                        }).orElse(VerifyRateResponse.Trend.NOT_ENOUGH_INFO))
                .build();
    }

    private static List<Rate> toInternalRates(List<ExternalRate> externalRates) {
        return externalRates.stream().map(r -> Rate.builder()
                        .abbr(r.getAbbr())
                        .rate(r.getRate())
                        .scale(r.getScale())
                        .build())
                .collect(Collectors.toList());
    }

    @ExceptionHandler(RatesReadinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse ratesReadiness(RatesReadinessException e) {
        log.error("Rates not ready", e);
        return ErrorResponse.builder()
                .code(ErrorResponse.ErrorCode.ERR_RATES_NOT_READY)
                .description("No rates loaded yet for " + e.getOnDate())
                .build();
    }

    @ExceptionHandler(RateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse ratePresence(RateNotFoundException e) {
        log.info("Rate not found", e);
        return ErrorResponse.builder()
                .code(ErrorResponse.ErrorCode.ERR_NO_SUCH_RATE)
                .description("Rate for " + e.getAbbr() + " doesn't exist for " + e.getDate())
                .build();
    }

    @ExceptionHandler(ExternalRatesServiceResponseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse unexpectedRemoteAPIResponse(ExternalRatesServiceResponseException e) {
        log.error("Unexpected response of remote API received", e);
        return ErrorResponse.builder()
                .code(ErrorResponse.ErrorCode.ERR_PARTNER_COMMUNICATIONS)
                .description("Problems occurred while communicating our partners")
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse unexpected(Exception e) {
        log.error("Unexpected error occurred", e);
        return ErrorResponse.builder()
                .code(ErrorResponse.ErrorCode.ERR_GENERAL)
                .description("Unexpected error has occurred")
                .build();
    }
}
