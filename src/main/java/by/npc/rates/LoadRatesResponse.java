package by.npc.rates;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
class LoadRatesResponse {
    private String status;
    private int count;
    private LocalDate date;
}
