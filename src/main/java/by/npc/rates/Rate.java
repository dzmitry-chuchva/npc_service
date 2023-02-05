package by.npc.rates;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
class Rate {
    /**
     * Expecting abbr field to be unique.
     */
    private String abbr;
    private BigDecimal scale;
    private BigDecimal rate;
}
