package by.npc.rates.store;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Rate {
    /**
     * Expecting abbr field to be unique.
     */
    private String abbr;
    private BigDecimal scale;
    private BigDecimal rate;
}
