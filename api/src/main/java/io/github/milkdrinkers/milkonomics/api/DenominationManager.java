package io.github.milkdrinkers.milkonomics.api;

import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;

import java.math.BigDecimal;
import java.util.Map;

public interface DenominationManager {
    Denomination getDefaultDenomination();

    Denomination getDenomination(String id);

    Map<String, Denomination> getAllDenominations();

    Map<Denomination, BigDecimal> getDenominationsDefault();

    Map<String, BigDecimal> getDenominationsDefaults();
}
