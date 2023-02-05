package by.npc.rates;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
class VerifyRateResponse {
    private String abbr;
    private Trend trend;
    private BigDecimal rate;
    private BigDecimal scale;

    enum Trend {
        UP, DOWN, SAME, NOT_ENOUGH_INFO
    }
}
