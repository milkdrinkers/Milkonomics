package io.github.milkdrinkers.milkonomics.economy.denomination;

import io.github.milkdrinkers.milkonomics.AbstractMilkonomics;
import io.github.milkdrinkers.milkonomics.Reloadable;
import io.github.milkdrinkers.milkonomics.api.denomination.Denomination;
import io.github.milkdrinkers.milkonomics.config.DenominationConfig;
import io.github.milkdrinkers.milkonomics.utility.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DenominationHandler implements Reloadable, io.github.milkdrinkers.milkonomics.api.DenominationManager {
    private ThreadLocal<Denomination> defaultDenomination;
    private final Map<String, Denomination> denominations = new ConcurrentHashMap<>();

    @Override
    public void onLoad(AbstractMilkonomics plugin) {
        for (DenominationConfig denominationConfig : plugin.getConfigHandler().getDenominationConfigs()) {
            final Denomination denomination = new DenominationImpl(
                denominationConfig.id,
                denominationConfig.displayName,
                denominationConfig.displayNamePlural,
                denominationConfig.symbol,
                denominationConfig.prefix,
                denominationConfig.suffix,
                denominationConfig.format,
                denominationConfig.decimalPlaces,
                denominationConfig.isDefault,
                denominationConfig.defaultBalance
            );

            if (denominationConfig.isDefault) {
                if (defaultDenomination != null) {
                    Logger.get().warn("Multiple default denominations found! Defaulting to the first one found: {}, ignoring {}", defaultDenomination.get().id(), denomination.id());
                }
                if (defaultDenomination == null) {
                    defaultDenomination = ThreadLocal.withInitial(() -> denomination);
                }
            }
            denominations.put(denomination.id(), denomination);
        };

        if (defaultDenomination == null) {
            Logger.get().error("No default denomination found! Defaulting to the first denomination found, or falling back to dollar.");
            if (!denominations.isEmpty()) {
                defaultDenomination = ThreadLocal.withInitial(() -> denominations.values().iterator().next());
            } else {
                defaultDenomination = ThreadLocal.withInitial(() -> new DenominationImpl(
                    "dollar",
                    "Dollar",
                    "Dollars",
                    "$",
                    "$",
                    "",
                    "%s%s",
                    2,
                    true,
                    BigDecimal.valueOf(0)));
                denominations.put(defaultDenomination.get().id(), defaultDenomination.get());
            }
        }
    }

    @Override
    public void onDisable(AbstractMilkonomics plugin) {
        defaultDenomination = null;
        denominations.clear();
    }

    public Denomination getDefaultDenomination() {
        return defaultDenomination.get();
    }

    public Denomination getDenomination(String id) {
        return denominations.get(id);
    }

    public Map<String, Denomination> getAllDenominations() {
        return denominations;
    }

    public Map<Denomination, BigDecimal> getDenominationsDefault() {
        return getAllDenominations().values()
            .stream()
            .collect(
                Collectors.toMap(denom -> denom, Denomination::defaultBalance)
            );
    }

    public Map<String, BigDecimal> getDenominationsDefaults() {
        return getDenominationsDefault().entrySet()
            .stream()
            .map(entry -> Map.entry(entry.getKey().id(), entry.getValue()))
            .collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
            );
    }
}
