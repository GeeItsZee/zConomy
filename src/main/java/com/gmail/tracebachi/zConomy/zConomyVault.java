/*
 * This file is part of zConomy.
 *
 * zConomy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * zConomy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with zConomy.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.tracebachi.zConomy;

import com.gmail.tracebachi.zConomy.Exceptions.ExistingBankAccountException;
import com.gmail.tracebachi.zConomy.Storage.BankAccount;
import com.gmail.tracebachi.zConomy.Storage.Settings;
import com.gmail.tracebachi.zConomy.Utils.HandlerUtils;
import com.gmail.tracebachi.zConomy.Utils.Shutdownable;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
@SuppressWarnings("deprecation")
public class zConomyVault implements Economy, Shutdownable
{
    private zConomy plugin;

    public zConomyVault(zConomy plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void shutdown()
    {
        plugin = null;
    }

    /**
     * Checks if economy method is enabled.
     * @return Success or Failure
     */
    public boolean isEnabled()
    {
        return plugin != null;
    }

    /**
     * Gets name of economy method
     * @return Name of Economy Method
     */
    public String getName()
    {
        return "zConomy";
    }

    /**
     * Returns true if the given implementation supports banks.
     * @return true if the implementation supports banks
     */
    public boolean hasBankSupport()
    {
        return true;
    }

    /**
     * Some economy plugins round off after a certain number of digits.
     * This function returns the number of digits the plugin keeps
     * or -1 if no rounding occurs.
     * @return number of digits after the decimal point kept
     */
    public int fractionalDigits()
    {
        return 2;
    }

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.
     *
     * @param amount to format
     * @return Human readable string describing amount
     */
    public String format(double amount)
    {
        return HandlerUtils.formatAmount(amount);
    }

    /**
     * Returns the name of the currency in plural form.
     * If the economy being used does not support currency names then an empty string will be returned.
     *
     * @return name of the currency (plural)
     */
    public String currencyNamePlural()
    {
        return Settings.getCurrencyNamePlural();
    }

    /**
     * Returns the name of the currency in singular form.
     * If the economy being used does not support currency names then an empty string will be returned.
     *
     * @return name of the currency (singular)
     */
    public String currencyNameSingular()
    {
        return Settings.getCurrencyNameSingular();
    }

    /**
     *
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)} instead.
     */
    @Deprecated
    public boolean hasAccount(String playerName)
    {
        plugin.debug("zConomyVault#hasAccount()");

        BankAccount account = plugin.getzConomyDatabase().getBalance(playerName);
        return account != null;
    }

    /**
     * Checks if this player has an account on the server yet
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     *
     * @param player to check
     * @return if the player has an account
     */
    public boolean hasAccount(OfflinePlayer player)
    {
        return hasAccount(player.getName());
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean hasAccount(String playerName, String worldName)
    {
        return hasAccount(playerName);
    }

    /**
     * Checks if this player has an account on the server yet on the given world
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     *
     * @param player to check in the world
     * @param worldName world-specific account
     * @return if the player has an account
     */
    public boolean hasAccount(OfflinePlayer player, String worldName)
    {
        return hasAccount(player);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)} instead.
     */
    @Deprecated
    public double getBalance(String playerName)
    {
        plugin.debug("zConomyVault#getBalance()");

        BankAccount account = plugin.getzConomyDatabase().getBalance(playerName);
        return (account != null) ? account.getBalance().doubleValue() : 0.00D;
    }

    /**
     * Gets balance of a player
     *
     * @param player of the player
     * @return Amount currently held in players account
     */
    public double getBalance(OfflinePlayer player)
    {
        return getBalance(player.getName());
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)} instead.
     */
    @Deprecated
    public double getBalance(String playerName, String world)
    {
        return getBalance(playerName);
    }

    /**
     * Gets balance of a player on the specified world.
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @param player to check
     * @param world name of the world
     * @return Amount currently held in players account
     */
    public double getBalance(OfflinePlayer player, String world)
    {
        return getBalance(player);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)} instead.
     */
    @Deprecated
    public boolean has(String playerName, double amount)
    {
        plugin.debug("zConomyVault#has()");

        return getBalance(playerName) >= amount;
    }

    /**
     * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to check
     * @param amount to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    public boolean has(OfflinePlayer player, double amount)
    {
        return has(player.getName(), amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use @{link {@link #has(OfflinePlayer, String, double)} instead.
     */
    @Deprecated
    public boolean has(String playerName, String worldName, double amount)
    {
        return has(playerName, amount);
    }

    /**
     * Checks if the player account has the amount in a given world - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     *
     * @param player to check
     * @param worldName to check with
     * @param amount to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    public boolean has(OfflinePlayer player, String worldName, double amount)
    {
        return has(player, amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, double)} instead.
     */
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double amount)
    {
        plugin.debug("zConomyVault#withdrawPlayer()");

        if(amount < 0)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
        }

        zConomyDatabase zConomyDatabase = plugin.getzConomyDatabase();
        BankAccount account = zConomyDatabase.getBalance(playerName);

        if(account == null)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "No account found");
        }

        double balance = account.getBalance().doubleValue();

        if(balance < amount)
        {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }

        zConomyDatabase.addToBalance(playerName, -amount);
        return new EconomyResponse(amount, balance - amount, EconomyResponse.ResponseType.SUCCESS, null);
    }

    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to withdraw from
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount)
    {
        return withdrawPlayer(player.getName(), amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, String, double)} instead.
     */
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
    {
        return withdrawPlayer(playerName, amount);
    }

    /**
     * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @param player to withdraw from
     * @param worldName - name of the world
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount)
    {
        return withdrawPlayer(player.getName(), amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, double)} instead.
     */
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double amount)
    {
        plugin.debug("zConomyVault#depositPlayer()");

        if(amount < 0)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        zConomyDatabase zConomyDatabase = plugin.getzConomyDatabase();
        BankAccount account = zConomyDatabase.getBalance(playerName);

        if(account == null)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "No account found");
        }

        double balance = account.getBalance().doubleValue();

        zConomyDatabase.addToBalance(playerName, amount);
        return new EconomyResponse(amount, balance + amount, EconomyResponse.ResponseType.SUCCESS, null);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount)
    {
        return depositPlayer(player.getName(), amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, String, double)} instead.
     */
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
    {
        return depositPlayer(playerName, amount);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     *
     * @param player to deposit to
     * @param worldName name of the world
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount)
    {
        return depositPlayer(player.getName(), amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #createBank(String, OfflinePlayer)} instead.
     */
    @Deprecated
    public EconomyResponse createBank(String name, String player)
    {
        plugin.debug("zConomyVault#createBank()");

        zConomyDatabase zConomyDatabase = plugin.getzConomyDatabase();

        try
        {
            zConomyDatabase.createAccount(name);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        }
        catch(ExistingBankAccountException ex)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Account already exists");
        }
    }

    /**
     * Creates a bank account with the specified name and the player as the owner
     * @param name of account
     * @param player the account should be linked to
     * @return EconomyResponse Object
     */
    public EconomyResponse createBank(String name, OfflinePlayer player)
    {
        return createBank(name, player.getName());
    }

    /**
     * Deletes a bank account with the specified name.
     * @param name of the back to delete
     * @return if the operation completed successfully
     */
    public EconomyResponse deleteBank(String name)
    {
        plugin.debug("zConomyVault#deleteBank()");

        zConomyDatabase zConomyDatabase = plugin.getzConomyDatabase();
        boolean found = zConomyDatabase.removeAccount(name);

        if(found)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        }
        else
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "No account found");
        }
    }

    /**
     * Returns the amount the bank has
     * @param name of the account
     * @return EconomyResponse Object
     */
    public EconomyResponse bankBalance(String name)
    {
        plugin.debug("zConomyVault#bankBalance()");

        BankAccount account = plugin.getzConomyDatabase().getBalance(name);

        if(account == null)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "No account found");
        }

        double balance = account.getBalance().doubleValue();

        return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    /**
     * Returns true or false whether the bank has the amount specified - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name of the account
     * @param amount to check for
     * @return EconomyResponse Object
     */
    public EconomyResponse bankHas(String name, double amount)
    {
        plugin.debug("zConomyVault#bankHas()");

        BankAccount account = plugin.getzConomyDatabase().getBalance(name);

        if(account == null)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "No account found");
        }

        double balance = account.getBalance().doubleValue();

        if(balance < amount)
        {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
        else
        {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, null);
        }
    }

    /**
     * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name of the account
     * @param amount to withdraw
     * @return EconomyResponse Object
     */
    public EconomyResponse bankWithdraw(String name, double amount)
    {
        return withdrawPlayer(name, amount);
    }

    /**
     * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name of the account
     * @param amount to deposit
     * @return EconomyResponse Object
     */
    public EconomyResponse bankDeposit(String name, double amount)
    {
        return depositPlayer(name, amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #isBankOwner(String, OfflinePlayer)} instead.
     */
    @Deprecated
    public EconomyResponse isBankOwner(String name, String playerName)
    {
        boolean sameName = name.equalsIgnoreCase(playerName);

        if(sameName)
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        }
        else
        {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not account owner");
        }
    }

    /**
     * Check if a player is the owner of a bank account
     *
     * @param name of the account
     * @param player to check for ownership
     * @return EconomyResponse Object
     */
    public EconomyResponse isBankOwner(String name, OfflinePlayer player)
    {
        return isBankOwner(name, player.getName());
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #isBankMember(String, OfflinePlayer)} instead.
     */
    @Deprecated
    public EconomyResponse isBankMember(String name, String playerName)
    {
        return isBankOwner(name, playerName);
    }

    /**
     * Check if the player is a member of the bank account
     *
     * @param name of the account
     * @param player to check membership
     * @return EconomyResponse Object
     */
    public EconomyResponse isBankMember(String name, OfflinePlayer player)
    {
        return isBankMember(name, player.getName());
    }

    /**
     * Gets the list of banks
     * @return the List of Banks
     */
    public List<String> getBanks()
    {
        return Collections.singletonList("Economy.getBanks() is unsupported");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer)} instead.
     */
    @Deprecated
    public boolean createPlayerAccount(String playerName)
    {
        return createBank(playerName, playerName).transactionSuccess();
    }

    /**
     * Attempts to create a player account for the given player
     * @param player OfflinePlayer
     * @return if the account creation was successful
     */
    public boolean createPlayerAccount(OfflinePlayer player)
    {
        return createPlayerAccount(player.getName());
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName)
    {
        return createPlayerAccount(playerName);
    }

    /**
     * Attempts to create a player account for the given player on the specified world
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     * @param player OfflinePlayer
     * @param worldName String name of the world
     * @return if the account creation was successful
     */
    public boolean createPlayerAccount(OfflinePlayer player, String worldName)
    {
        return createPlayerAccount(player);
    }
}
