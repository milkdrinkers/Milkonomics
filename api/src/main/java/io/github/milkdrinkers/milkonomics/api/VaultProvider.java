package io.github.milkdrinkers.milkonomics.api;

import net.milkbowl.vault.economy.Economy;
import org.jetbrains.annotations.NotNull;

public interface VaultProvider {
    /**
     * Get the Vault Economy instance.
     *
     * @return The Vault Economy instance.
     */
    @NotNull Economy getEconomy();
}
