package by.npc.rates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestController
@RequestMapping(value = "/")
@Slf4j
public class RatesController {
    @GetMapping("load")
    public String loadRates(@RequestParam(required = false) Date date) {
        return "OK: " + date;
    }

    @RestControllerAdvice(assignableTypes = RatesController.class)
    public static class RatesExceptionHandler {
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public String unexpected(Exception e) {
            log.error("Unexpected error occurred", e);
            return "Unexpected error has occurred. Associated message: [" + e.getMessage() + "]";
        }
    }
}
