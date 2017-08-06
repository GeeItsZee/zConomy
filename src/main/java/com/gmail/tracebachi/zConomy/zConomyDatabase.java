/*
 * zConomy - Database-backed, Vault-compatible economy plugin
 * Copyright (C) 2017 tracebachi@gmail.com (GeeItsZee)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.tracebachi.zConomy;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class zConomyDatabase
{
  private static final int MYSQL_CODE_ER_DUP_ENTRY = 1062;

  private final Settings settings;
  private final String tableName;

  public zConomyDatabase(Settings settings)
  {
    this.settings = settings;
    this.tableName = settings.getTableName();
  }

  public boolean createTable()
  {
    try (Connection connection = settings.getConnection())
    {
      try (PreparedStatement statement = connection.prepareStatement(createTableQuery()))
      {
        statement.execute();
        return true;
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean createBankAccount(String name)
  {
    Preconditions.checkNotNull(name);

    try (Connection connection = settings.getConnection())
    {
      try (PreparedStatement statement = connection.prepareStatement(createAccountQuery()))
      {
        BankAccount account = new BankAccount(name, new BigDecimal(settings.getDefaultBalance()));

        statement.setString(1, account.getOwner());
        statement.setBigDecimal(2, account.getBalance());

        return (statement.executeUpdate() > 0);
      }
    }
    catch (SQLException ex)
    {
      if (ex.getErrorCode() == MYSQL_CODE_ER_DUP_ENTRY)
      {
        throw new ExistingBankAccountException(ex);
      }
      else
      {
        ex.printStackTrace();
        return false;
      }
    }
  }

  public boolean removeBankAccount(String name)
  {
    Preconditions.checkNotNull(name);

    try (Connection connection = settings.getConnection())
    {
      try (PreparedStatement statement = connection.prepareStatement(removeAccountQuery()))
      {
        statement.setString(1, name);

        return (statement.executeUpdate() > 0);
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
      return false;
    }
  }

  public BankAccount getBankAccount(String name)
  {
    Preconditions.checkNotNull(name);

    try (Connection connection = settings.getConnection())
    {
      try (PreparedStatement statement = connection.prepareStatement(selectSingleBalanceQuery()))
      {
        statement.setString(1, name);

        try (ResultSet resultSet = statement.executeQuery())
        {
          if (!resultSet.next())
          {
            return null;
          }

          String owner = resultSet.getString("owner");
          BigDecimal balance = resultSet.getBigDecimal("balance");
          Timestamp timestamp = resultSet.getTimestamp("timestamp");
          return new BankAccount(owner, balance, timestamp.getTime());
        }
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }

  public Map<String, BankAccount> getBankAccounts(String first, String second)
  {
    Preconditions.checkNotNull(first);
    Preconditions.checkNotNull(second);

    try (Connection connection = settings.getConnection())
    {
      try (PreparedStatement statement = connection.prepareStatement(selectDoubleBalanceQuery()))
      {
        statement.setString(1, first);
        statement.setString(2, second);

        try (ResultSet resultSet = statement.executeQuery())
        {
          HashMap<String, BankAccount> accountsMap = new HashMap<>();

          for (int i = 0; i < 2 && resultSet.next(); ++i)
          {
            String owner = resultSet.getString("owner");
            BigDecimal balance = resultSet.getBigDecimal("balance");
            Timestamp timestamp = resultSet.getTimestamp("timestamp");
            BankAccount account = new BankAccount(owner, balance, timestamp.getTime());

            accountsMap.put(owner.toLowerCase(), account);
          }

          return accountsMap;
        }
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
      return Collections.emptyMap();
    }
  }

  public boolean setBankAccountBalance(String name, double amount)
  {
    Preconditions.checkNotNull(name);

    try (Connection connection = settings.getConnection())
    {
      try (PreparedStatement statement = connection.prepareStatement(updateBalanceQuery()))
      {
        statement.setBigDecimal(1, new BigDecimal(amount));
        statement.setString(2, name);

        return (statement.executeUpdate() > 0);
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean addToBankAccountBalance(String name, double amount)
  {
    Preconditions.checkNotNull(name);

    try (Connection connection = settings.getConnection())
    {
      try (PreparedStatement statement = connection.prepareStatement(addToBalanceQuery()))
      {
        statement.setBigDecimal(1, new BigDecimal(amount));
        statement.setString(2, name);

        return (statement.executeUpdate() > 0);
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
      return false;
    }
  }

  public List<BankAccount> getTopBankAccounts()
  {
    try (Connection connection = settings.getConnection())
    {
      try (PreparedStatement statement = connection.prepareStatement(selectTopAccountsQuery()))
      {
        try (ResultSet resultSet = statement.executeQuery())
        {
          List<BankAccount> accountList = new ArrayList<>(10);

          for (int i = 0; i < 10 && resultSet.next(); ++i)
          {
            String owner = resultSet.getString("owner");
            BigDecimal balance = resultSet.getBigDecimal("balance");
            Timestamp timestamp = resultSet.getTimestamp("timestamp");
            BankAccount account = new BankAccount(owner, balance, timestamp.getTime());

            accountList.add(account);
          }

          return accountList;
        }
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
      return Collections.emptyList();
    }
  }

  private String createTableQuery()
  {
    return
      " CREATE TABLE IF NOT EXISTS " + tableName + " (" +
      " `owner`       VARCHAR(64) NOT NULL," +
      " `balance`     DECIMAL(20, 2) NOT NULL," +
      " `timestamp`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
      " CONSTRAINT PRIMARY KEY (`owner`)" +
      " ) ENGINE = InnoDB DEFAULT CHARSET = utf8;";
  }

  private String createAccountQuery()
  {
    return "INSERT INTO " + tableName + " (owner, balance) VALUES (?, ?);";
  }

  private String removeAccountQuery()
  {
    return "DELETE FROM " + tableName + " WHERE owner = ? LIMIT 1;";
  }

  private String selectSingleBalanceQuery()
  {
    return "SELECT owner, balance, timestamp FROM " + tableName + " WHERE owner = ? LIMIT 1;";
  }

  private String selectDoubleBalanceQuery()
  {
    return "SELECT owner, balance, timestamp FROM " + tableName + " WHERE owner IN (?, ?);";
  }

  private String updateBalanceQuery()
  {
    return "UPDATE " + tableName + " SET balance = ? WHERE owner = ? LIMIT 1;";
  }

  private String addToBalanceQuery()
  {
    return "UPDATE " + tableName + " SET balance = (balance + ?) WHERE owner = ? LIMIT 1;";
  }

  private String selectTopAccountsQuery()
  {
    return "SELECT owner, balance, timestamp FROM " + tableName +
      " ORDER BY balance DESC LIMIT 10;";
  }
}
