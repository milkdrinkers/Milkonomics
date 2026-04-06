package io.github.milkdrinkers.milkonomicsplugin.economy.denomination;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.milkonomicsplugin.api.denomination.Denomination;
import io.github.milkdrinkers.milkonomicsplugin.utility.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DenominationHandler implements Reloadable {
    private Denomination defaultDenomination;
    private final Map<String, Denomination> denominations = new ConcurrentHashMap<>();

    @Override
    public void onEnable(AbstractMilkonomicsPlugin plugin) {
        plugin.getConfigHandler().getDenominationConfigs().forEach(denominationConfig -> {
            final Denomination denomination = new DenominationImpl(
                denominationConfig.id,
                denominationConfig.displayName,
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
                    Logger.get().warn("Multiple default denominations found! Defaulting to the first one found: {}, ignoring {}", defaultDenomination.id(), denomination.id());
                }
                if (defaultDenomination == null) {
                    defaultDenomination = denomination;
                }
            }
            denominations.put(denomination.id(), denomination);
        });

        if (defaultDenomination == null) {
            Logger.get().error("No default denomination found! Defaulting to the first denomination found, or falling back to dollar.");
            if (!denominations.isEmpty()) {
                defaultDenomination = denominations.values().iterator().next();
            } else {
                defaultDenomination = new DenominationImpl(
                    "dollar",
                    "Dollar",
                    "$",
                    "$",
                    "",
                    "%s%s",
                    2,
                    true,
                    BigDecimal.valueOf(0));
                denominations.put(defaultDenomination.id(), defaultDenomination);
            }
        }
    }

    @Override
    public void onDisable(AbstractMilkonomicsPlugin plugin) {
        defaultDenomination = null;
        denominations.clear();
    }

    public Denomination getDefaultDenomination() {
        return defaultDenomination;
    }

    public Denomination getDenomination(String id) {
        return denominations.get(id);
    }

    public Map<String, Denomination> getAllDenominations() {
        return denominations;
    }

    public Map<Denomination, BigDecimal> getDenominationsDefault() {
        return denominations.values()
            .stream()
            .collect(
                Collectors.toMap(denom -> denom, Denomination::defaultBalance)
            );
    }

    public Map<String, BigDecimal> getDenominationsDefaults() {
        return denominations.values()
            .stream()
            .collect(
                Collectors.toMap(Denomination::id, Denomination::defaultBalance)
            );
    }
}
