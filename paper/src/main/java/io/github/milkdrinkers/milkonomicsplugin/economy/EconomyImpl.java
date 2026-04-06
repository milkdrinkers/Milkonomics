package io.github.milkdrinkers.milkonomicsplugin.economy;

import io.github.milkdrinkers.milkonomicsplugin.AbstractMilkonomicsPlugin;
import io.github.milkdrinkers.milkonomicsplugin.Reloadable;
import io.github.milkdrinkers.milkonomicsplugin.api.AccountManager;
import io.github.milkdrinkers.milkonomicsplugin.api.account.Account;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;

import java.math.BigDecimal;
import java.util.*;

public class EconomyImpl implements Economy, Reloadable {
    private final AbstractMilkonomicsPlugin plugin;
    private final AccountManager<Account> manager;

    public EconomyImpl(AbstractMilkonomicsPlugin plugin, AccountManager<Account> manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public void onEnable(AbstractMilkonomicsPlugin plugin) {
        Bukkit.getServicesManager().register(Economy.class, this, plugin, ServicePriority.Highest);
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        // TODO Allow setting in config
        return 0;
    }

    @Override
    public String format(double amount) {
        final int decimals = fractionalDigits();

        // TODO Format
//        DecimalFormat df;
//        if (fractionalDigits > 0 && amount % 1 != 0)
//        {
//            df = new DecimalFormat("#,##0." + StringUtils.repeat("0", fractionalDigits));
//        }
//        else
//        {
//            df = new DecimalFormat("#,##0");
//        }


        return "";
    }

    @Override
    public String currencyNamePlural() {
        // TODO Allow setting in config
        return "";
    }

    @Override
    public String currencyNameSingular() {
        // TODO Allow setting in config
        return "";
    }

    private boolean hasAccount(UUID uuid) {
        return manager.hasAccount(uuid);
    }

    @Override
    public boolean hasAccount(String accountId) {
        return manager.hasAccount(accountId);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String accountId, String worldName) {
        return hasAccount(accountId);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    private double getBalance(Account account) {
        return account.getDouble();
    }

    @Override
    public double getBalance(String accountId) {
        return getBalance(EconomyUtil.getUUIDFromCache(accountId));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(EconomyUtil.getUUIDFromCache(player.getUniqueId()));
    }

    @Override
    public double getBalance(String accountId, String world) {
        return getBalance(accountId);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    private boolean has(Account account, double amount) {
        return account.has(amount);
    }

    @Override
    public boolean has(String accountId, double amount) {
        return has(EconomyUtil.getUUIDFromCache(accountId), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(EconomyUtil.getUUIDFromCache(player.getUniqueId()), amount);
    }

    @Override
    public boolean has(String accountId, String worldName, double amount) {
        return has(accountId, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    private EconomyResponse withdrawPlayer(Account account, double originalAmount) {
        final BigDecimal balance = account.get();
        final BigDecimal amount = BigDecimal.valueOf(originalAmount);

        if (EconomyUtil.isNegative(amount)) {
            return new EconomyResponse(
                originalAmount,
                balance.doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Amount lower than zero."
            );
        }

        if (amount.scale() > fractionalDigits()) {
            return new EconomyResponse(
                originalAmount,
                balance.doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Balance leads to illegal decimals."
            );
        }

        if (!has(account, originalAmount)) {
            return new EconomyResponse(
                originalAmount,
                balance.doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Cannot afford."
            );
        }

        final BigDecimal newBalance = balance.subtract(amount);
        account.set(newBalance);

        return new EconomyResponse(
            originalAmount,
            newBalance.doubleValue(),
            EconomyResponse.ResponseType.SUCCESS,
            null
        );
    }

    @Override
    public EconomyResponse withdrawPlayer(String accountId, double amount) {
        return withdrawPlayer(EconomyUtil.getUUIDFromCache(accountId), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(EconomyUtil.getUUIDFromCache(player.getUniqueId()), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String accountId, String worldName, double amount) {
        return withdrawPlayer(accountId, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    private EconomyResponse depositPlayer(Account account, double originalAmount) {
        final BigDecimal balance = account.get();
        final BigDecimal amount = BigDecimal.valueOf(originalAmount);

        if (EconomyUtil.isNegative(amount)) {
            return new EconomyResponse(
                originalAmount,
                balance.doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Amount lower than zero."
            );
        }

        if (amount.scale() > fractionalDigits()) {
            return new EconomyResponse(
                originalAmount,
                balance.doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Balance leads to illegal decimals."
            );
        }

        final BigDecimal newBalance = balance.add(amount);

        if (EconomyUtil.exceedsAccountLimit(newBalance)) {
            return new EconomyResponse(
                originalAmount,
                balance.doubleValue(),
                EconomyResponse.ResponseType.FAILURE,
                "Balance exceeds maximum."
            );
        }

        account.set(newBalance);

        return new EconomyResponse(
            originalAmount,
            newBalance.doubleValue(),
            EconomyResponse.ResponseType.SUCCESS,
            null
        );
    }

    @Override
    public EconomyResponse depositPlayer(String accountId, double amount) {
        return depositPlayer(EconomyUtil.getUUIDFromCache(accountId), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(EconomyUtil.getUUIDFromCache(player.getUniqueId()), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String accountId, String worldName, double amount) {
        return depositPlayer(accountId, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String accountId) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse isBankMember(String name, String accountId) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Banks are not supported.");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    private boolean createPlayerAccount(UUID uuid, String accountId) {
        final BigDecimal initialBalance = new BigDecimal(0).stripTrailingZeros(); // TODO Allow setting in config

        if (EconomyUtil.isNegative(initialBalance)) {
            return false;
        }

        if (initialBalance.scale() > fractionalDigits()) { // TODO Do rounding
            return false;
        }

        if (EconomyUtil.exceedsAccountLimit(initialBalance)) { // TODO Set to max value
            return false;
        }

        manager.createAccount(uuid, accountId, plugin.getDenominationHandler().getDefaultDenomination(), plugin.getDenominationHandler().getDenominationsDefaults());

        return true;
    }

    @Override
    public boolean createPlayerAccount(String accountId) {
        try {
            return createPlayerAccount(UUID.fromString(accountId), accountId);
        } catch (IllegalArgumentException e) {
            return createPlayerAccount(UUID.nameUUIDFromBytes(accountId.getBytes()), accountId);
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player.getUniqueId(), Optional.ofNullable(player.getName()).orElse(player.getUniqueId().toString()));
    }

    @Override
    public boolean createPlayerAccount(String accountId, String worldName) {
        return createPlayerAccount(accountId);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }
}
