package by.npc.rates;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class ErrorResponse {
    private ErrorCode code;
    private String description;

    enum ErrorCode {
        ERR_GENERAL,
        ERR_RATES_NOT_READY,
        ERR_NO_SUCH_RATE,
        ERR_PARTNER_COMMUNICATIONS,
        ERR_BAD_REQUEST_PARAMETER,
        ERR_BAD_REQUEST,
    }
}
