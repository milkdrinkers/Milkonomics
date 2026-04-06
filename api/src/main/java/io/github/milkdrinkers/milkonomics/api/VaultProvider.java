package io.github.milkdrinkers.milkonomics.api;

import net.milkbowl.vault.economy.Economy;

public interface VaultProvider {
    Economy getEconomy();
}
