package io.github.milkdrinkers.milkonomicsplugin.api;

import net.milkbowl.vault.economy.Economy;

public interface VaultProvider {
    Economy getEconomy();
}
