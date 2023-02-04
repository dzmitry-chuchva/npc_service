package by.npc.rates;

import lombok.Getter;

import java.time.LocalDate;

class RatesReadinessException extends Exception {
    @Getter
    private final LocalDate onDate;

    RatesReadinessException(LocalDate onDate) {
        super(onDate.toString());
        this.onDate = onDate;
    }
}
