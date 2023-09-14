package com.klasha.assessment.repository;

import com.klasha.assessment.entity.ExchangeRate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findExchangeRatesByTargetCurrencyAndSourceCurrency(String targetCurrency, String sourceCurrency);
}
