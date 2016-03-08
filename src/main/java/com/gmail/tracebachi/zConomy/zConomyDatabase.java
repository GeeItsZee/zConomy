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

import com.gmail.tracebachi.zConomy.Events.*;
import com.gmail.tracebachi.zConomy.Exceptions.ExistingBankAccountException;
import com.gmail.tracebachi.zConomy.Storage.BankAccount;
import com.gmail.tracebachi.zConomy.Storage.Settings;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public class zConomyDatabase
{
    private static final int DUPLICATE_ENTRY = 1062;

    private zConomy plugin;

    public zConomyDatabase(zConomy plugin)
    {
        this.plugin = plugin;
    }

    public void shutdown()
    {
        plugin = null;
    }

    public boolean createTable() throws SQLException
    {
        try(Connection connection = Settings.getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(createTableQuery()))
            {
                plugin.debug("Creating Database Table ...");
                return statement.execute();
            }
        }
    }

    public boolean createAccount(String name)
    {
        Preconditions.checkNotNull(name);

        try(Connection connection = Settings.getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(createAccountQuery()))
            {
                BankAccount account = new BankAccount(name, new BigDecimal(Settings.getDefaultBalance()));

                statement.setString(1, account.getOwner());
                statement.setBigDecimal(2, account.getBalance());

                if(statement.execute())
                {
                    AccountCreateEvent event = new AccountCreateEvent(account);
                    Bukkit.getPluginManager().callEvent(event);

                    plugin.debug("Created account {name: " + name + ", balance: " + Settings.getDefaultBalance() + "}");
                    return true;
                }
                else
                {
                    plugin.debug("Failed to create account {name: " + name + "}");
                    return false;
                }
            }
        }
        catch(SQLException ex)
        {
            if(ex.getErrorCode() == DUPLICATE_ENTRY)
            {
                plugin.debug("Found existing account for {name: " + name + "}");
                throw new ExistingBankAccountException(ex);
            }
            else
            {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public boolean removeAccount(String name)
    {
        Preconditions.checkNotNull(name);

        try(Connection connection = Settings.getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(removeAccountQuery()))
            {
                statement.setString(1, name);

                if(statement.executeUpdate() > 0)
                {
                    AccountRemoveEvent event = new AccountRemoveEvent(name);
                    Bukkit.getPluginManager().callEvent(event);

                    plugin.debug("Removed account {name: " + name + "}");
                    return true;
                }
                else
                {
                    plugin.debug("Tried to remove non-existent account {name: " + name + "}");
                    return false;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public BankAccount getBalance(String name)
    {
        Preconditions.checkNotNull(name);

        try(Connection connection = Settings.getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(selectSingleBalanceQuery()))
            {
                statement.setString(1, name);

                try(ResultSet resultSet = statement.executeQuery())
                {
                    if(!resultSet.next()) return null;

                    String owner = resultSet.getString("owner");
                    BigDecimal balance = resultSet.getBigDecimal("balance");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");
                    BankAccount account = new BankAccount(owner, balance, timestamp.getTime());

                    plugin.debug("Found account: " + account);
                    return account;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public Map<String, BankAccount> getBalances(String first, String second)
    {
        Preconditions.checkNotNull(first);
        Preconditions.checkNotNull(second);

        try(Connection connection = Settings.getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(selectDoubleBalanceQuery()))
            {
                statement.setString(1, first);
                statement.setString(2, second);

                try(ResultSet resultSet = statement.executeQuery())
                {
                    HashMap<String, BankAccount> accountsMap = new HashMap<>();

                    for(int i = 0; i < 2 && resultSet.next(); ++i)
                    {
                        String owner = resultSet.getString("owner");
                        BigDecimal balance = resultSet.getBigDecimal("balance");
                        Timestamp timestamp = resultSet.getTimestamp("timestamp");
                        BankAccount account = new BankAccount(owner, balance, timestamp.getTime());

                        plugin.debug("Found account: " + account);
                        accountsMap.put(owner.toLowerCase(), account);
                    }

                    return accountsMap;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public boolean updateBalance(String name, double amount)
    {
        Preconditions.checkNotNull(name);

        try(Connection connection = Settings.getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(updateBalanceQuery()))
            {
                statement.setBigDecimal(1, new BigDecimal(amount));
                statement.setString(2, name);

                if(statement.executeUpdate() > 0)
                {
                    AccountSetEvent event = new AccountSetEvent(name, amount);
                    Bukkit.getPluginManager().callEvent(event);

                    plugin.debug("Set balance for: {name: " + name + ", amount: " + amount + "}");
                    return true;
                }
                else
                {
                    plugin.debug("Failed to set balance for: {name: " + name + "}");
                    return false;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean addToBalance(String name, double amount)
    {
        Preconditions.checkNotNull(name);

        try(Connection connection = Settings.getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(addToBalanceQuery()))
            {
                statement.setBigDecimal(1, new BigDecimal(amount));
                statement.setString(2, name);

                if(statement.executeUpdate() > 0)
                {
                    AccountUpdateEvent event = new AccountUpdateEvent(name, amount);
                    Bukkit.getPluginManager().callEvent(event);

                    plugin.debug("Added to balance: {name: " + name + ", amount: " + amount + "}");
                    return true;
                }
                else
                {
                    plugin.debug("Failed to add to balance for: {name: " + name + "}");
                    return false;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public List<BankAccount> getTopAccounts()
    {
        try(Connection connection = Settings.getDataSource().getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(selectTopAccountsQuery()))
            {
                try(ResultSet resultSet = statement.executeQuery())
                {
                    List<BankAccount> accountList = new ArrayList<>(10);

                    for(int i = 0; i < 10 && resultSet.next(); ++i)
                    {
                        String owner = resultSet.getString("owner");
                        BigDecimal balance = resultSet.getBigDecimal("balance");
                        Timestamp timestamp = resultSet.getTimestamp("timestamp");
                        BankAccount account = new BankAccount(owner, balance, timestamp.getTime());

                        plugin.debug("Found top account: #" + i + " " + account);
                        accountList.add(account);
                    }

                    return accountList;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public int purgeAccounts()
    {
        try(Connection connection = Settings.getDataSource().getConnection())
        {
            int purgeDays = Settings.getPurgeDays();

            try(PreparedStatement statement = connection.prepareStatement(selectPurgeAccountsQuery(purgeDays)))
            {
                try(ResultSet resultSet = statement.executeQuery())
                {
                    while(resultSet.next())
                    {
                        String owner = resultSet.getString("owner");
                        BigDecimal balance = resultSet.getBigDecimal("balance");
                        Timestamp timestamp = resultSet.getTimestamp("timestamp");
                        BankAccount account = new BankAccount(owner, balance, timestamp.getTime());

                        plugin.info("Found purge-able account: " + account);

                        AccountPurgeEvent event = new AccountPurgeEvent(owner);
                        Bukkit.getPluginManager().callEvent(event);
                    }
                }
            }

            try(PreparedStatement statement = connection.prepareStatement(deletePurgeAccountsQuery(purgeDays)))
            {
                int count = statement.executeUpdate();

                plugin.info("Purged " + count +
                    " accounts with: {ageInDays: " + purgeDays + "}" +
                    " or {balance: " + Settings.getDefaultBalance() + "}");

                return count;
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return 0;
        }
    }

    private String createTableQuery()
    {
        return
            " CREATE TABLE IF NOT EXISTS " + Settings.getTableName() +
            "(" +
            " `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT," +
            " `owner`       VARCHAR(64) NOT NULL," +
            " `balance`     DECIMAL(20, 2) NOT NULL," +
            " `timestamp`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            " CONSTRAINT PRIMARY KEY (`id`)," +
            " CONSTRAINT UNIQUE KEY (`owner`)" +
            " ) ENGINE = InnoDB DEFAULT CHARSET = utf8;";
    }

    private String createAccountQuery()
    {
        return
            " INSERT INTO " + Settings.getTableName() +
            " (owner, balance)" +
            " VALUES (?, ?);";
    }

    private String removeAccountQuery()
    {
        return
            " DELETE FROM " + Settings.getTableName() +
            " WHERE owner = ?" +
            " LIMIT 1;";
    }

    private String selectSingleBalanceQuery()
    {
        return
            " SELECT owner, balance, timestamp" +
            " FROM " + Settings.getTableName() +
            " WHERE owner = ? LIMIT 1;";
    }

    private String selectDoubleBalanceQuery()
    {
        return
            " SELECT owner, balance, timestamp" +
            " FROM " + Settings.getTableName() +
            " WHERE owner IN (?, ?);";
    }

    private String updateBalanceQuery()
    {
        return
            " UPDATE " + Settings.getTableName() +
            " SET balance = ?" +
            " WHERE owner = ?" +
            " LIMIT 1;";
    }

    private String addToBalanceQuery()
    {
        return
            " UPDATE " + Settings.getTableName() +
                " SET balance = balance + ?" +
                " WHERE owner = ?" +
                " LIMIT 1;";
    }

    private String selectTopAccountsQuery()
    {
        return
            " SELECT owner, balance, timestamp" +
            " FROM " + Settings.getTableName() +
            " ORDER BY balance DESC" +
            " LIMIT 10;";
    }

    private String selectPurgeAccountsQuery(int days)
    {
        return
            " SELECT owner, balance, timestamp FROM " + Settings.getTableName() +
            " WHERE balance = " + Settings.getDefaultBalance() +
            " OR timestamp < DATE_SUB(NOW(), INTERVAL '" + days + "' DAY);";
    }

    private String deletePurgeAccountsQuery(int days)
    {
        return
            " DELETE FROM " + Settings.getTableName() +
            " WHERE balance = " + Settings.getDefaultBalance() +
            " OR timestamp < DATE_SUB(NOW(), INTERVAL '" + days + "' DAY);";
    }
}
