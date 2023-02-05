package by.npc.rates.store;

import lombok.Getter;

import java.time.LocalDate;

public class RatesReadinessException extends Exception {
    @Getter
    private final LocalDate onDate;

    RatesReadinessException(LocalDate onDate) {
        super(onDate.toString());
        this.onDate = onDate;
    }
}
