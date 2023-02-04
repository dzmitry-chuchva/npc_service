package by.npc.rates;

import lombok.Getter;

import java.time.LocalDate;

public class RatesReadinessException extends Exception {
    @Getter
    private final LocalDate onDate;

    public RatesReadinessException(LocalDate onDate) {
        super(onDate.toString());
        this.onDate = onDate;
    }
}
