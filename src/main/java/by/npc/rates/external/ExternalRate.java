package by.npc.rates.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExternalRate {
    @JsonProperty("Cur_ID")
    private String id;
    @JsonProperty("Date")
    private LocalDate date;
    @JsonProperty("Cur_Abbreviation")
    private String abbr;
    @JsonProperty("Cur_Scale")
    private BigDecimal scale;
    @JsonProperty("Cur_Name")
    private String name;
    @JsonProperty("Cur_OfficialRate")
    private BigDecimal rate;

}
