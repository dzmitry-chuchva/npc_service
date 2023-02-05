package by.npc.rates.store;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RatesStorage {
    private static final String CACHE_NAME = "rates";
    private final CacheManager cacheManager;

    public void storeRates(LocalDate date, List<Rate> rates) {
        // immediately process flat rates array into Rate::getAbbr keyed map to prepare faster lookup later
        Map<String, Rate> ratesMap = rates.stream()
                .collect(Collectors.toUnmodifiableMap(
                        Rate::getAbbr,
                        Function.identity()));

        cacheManager.getCache(CACHE_NAME).put(date, ratesMap);
    }

    public Rate getRate(LocalDate date, String abbr) throws RatesReadinessException, RateNotFoundException {
        Cache.ValueWrapper ratesWrapper = cacheManager.getCache(CACHE_NAME).get(date);
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

}
