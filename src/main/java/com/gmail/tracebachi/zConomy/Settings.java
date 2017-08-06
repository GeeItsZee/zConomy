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

import com.gmail.tracebachi.DbShare.DbShare;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class Settings
{
  private boolean debugMode;
  private String databaseName;
  private String tableName;
  private String currencyNameSingular;
  private String currencyNamePlural;
  private double defaultBalance;
  private HashMap<String, MessageFormat> formats = new HashMap<>();
  private DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

  public void read(ConfigurationSection config)
  {
    debugMode = config.getBoolean("DebugMode", false);
    databaseName = config.getString("DatabaseName");
    tableName = config.getString("TableName");
    currencyNameSingular = config.getString("CurrencyName.Singular", "Dollar");
    currencyNamePlural = config.getString("CurrencyName.Plural", "Dollars");
    defaultBalance = config.getDouble("DefaultBalance", 500.0);
    formats = new HashMap<>();

    ConfigurationSection section = config.getConfigurationSection("Formats");
    for (Map.Entry<String, Object> entry : section.getValues(false).entrySet())
    {
      String value = ChatColor.translateAlternateColorCodes('&', (String) entry.getValue());
      formats.put(entry.getKey(), new MessageFormat(value));
    }
  }

  public boolean isDebugEnabled()
  {
    return debugMode;
  }

  public Connection getConnection() throws SQLException
  {
    return DbShare.instance().getDataSource(databaseName).getConnection();
  }

  public String getTableName()
  {
    return tableName;
  }

  public String getCurrencyNameSingular()
  {
    return currencyNameSingular;
  }

  public String getCurrencyNamePlural()
  {
    return currencyNamePlural;
  }

  public double getDefaultBalance()
  {
    return defaultBalance;
  }

  public String format(String key, String... args)
  {
    MessageFormat format = formats.get(key);

    if (format == null)
    {
      return "Format, " + key + ", not found.";
    }

    return format.format(args);
  }

  public String formatAmount(BigDecimal amount)
  {
    return formatAmount(amount.doubleValue());
  }

  public String formatAmount(double amount)
  {
    return DECIMAL_FORMAT.format(amount);
  }
}
