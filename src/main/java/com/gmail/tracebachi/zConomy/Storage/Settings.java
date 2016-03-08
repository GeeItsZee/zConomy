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
package com.gmail.tracebachi.zConomy.Storage;

import com.gmail.tracebachi.DbShare.DbShare;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public class Settings
{
    private static boolean debug;
    private static String databaseName;
    private static String tableName;
    private static String currencyNameSingular;
    private static String currencyNamePlural;
    private static double defaultBalance;
    private static int purgeDays;
    private static HashMap<String, MessageFormat> formats = new HashMap<>();

    public static boolean read(ConfigurationSection config)
    {
        debug = config.getBoolean("Debug");
        databaseName = config.getString("DatabaseName");
        tableName = config.getString("TableName");
        currencyNameSingular = config.getString("CurrencyName.Singular");
        currencyNamePlural = config.getString("CurrencyName.Plural");
        defaultBalance = config.getDouble("DefaultBalance");
        purgeDays = config.getInt("PurgeDays");

        ConfigurationSection section = config.getConfigurationSection("Formats");

        for(Map.Entry<String, Object> entry : section.getValues(false).entrySet())
        {
            formats.put(entry.getKey(), new MessageFormat((String) entry.getValue()));
        }

        return false;
    }

    public static boolean isDebugEnabled()
    {
        return debug;
    }

    public static HikariDataSource getDataSource()
    {
        return DbShare.getDataSource(databaseName);
    }

    public static String getTableName()
    {
        return tableName;
    }

    public static String getCurrencyNameSingular()
    {
        return currencyNameSingular;
    }

    public static String getCurrencyNamePlural()
    {
        return currencyNamePlural;
    }

    public static double getDefaultBalance()
    {
        return defaultBalance;
    }

    public static int getPurgeDays()
    {
        return purgeDays;
    }

    public static String format(String key, String... args)
    {
        MessageFormat format = formats.get(key);

        if(format != null)
        {
            return format.format(args);
        }
        else
        {
            return "Undefined format in settings: " + key;
        }
    }
}
