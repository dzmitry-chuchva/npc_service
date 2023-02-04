package by.npc.rates;

import lombok.Getter;

import java.time.LocalDate;

@Getter
class RateNotFoundException extends Exception {
    private final LocalDate date;
    private final String abbr;

    RateNotFoundException(LocalDate date, String abbr) {
        super(abbr + " absent on " + date);
        this.date = date;
        this.abbr = abbr;
    }
}
