package by.npc.rates.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class ExternalRatesService {
    private final RestTemplate restTemplate;

    public ExternalRatesService() {
        this.restTemplate = new RestTemplate();
    }

    public List<ExternalRate> getRates(LocalDate date) throws ExternalRatesServiceResponseException {
        try {
            return Arrays.asList(restTemplate.getForObject("https://www.nbrb.by/api/exrates/rates?periodicity=0&ondate={ondate}",
                    ExternalRate[].class, date.toString()));
        } catch (RestClientResponseException e) {
            throw new ExternalRatesServiceResponseException(e.getStatusText(), e);
        }
    }
}