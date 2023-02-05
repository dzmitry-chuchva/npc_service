package by.npc.rates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
class Rate {
    @JsonProperty("Cur_ID")
    private String id;
    @JsonProperty("Date")
    private LocalDate date;
    /**
     * Expecting abbr field to be unique.
     */
    @JsonProperty("Cur_Abbreviation")
    private String abbr;
    @JsonProperty("Cur_Scale")
    private BigDecimal scale;
    @JsonProperty("Cur_Name")
    private String name;
    @JsonProperty("Cur_OfficialRate")
    private BigDecimal rate;

}
