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

import com.gmail.tracebachi.zConomy.Storage.BankAccount;
import com.gmail.tracebachi.zConomy.Storage.Settings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Locale;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public class zConomy extends JavaPlugin implements Listener
{
    private zConomyDatabase zConomyDatabase;
    private zConomyVault zConomyVault;
    private CommandMoney commandMoney;

    @Override
    public void onLoad()
    {
        Locale.setDefault(Locale.US);
        saveDefaultConfig();
    }

    @Override
    public void onEnable()
    {
        reloadConfig();
        Settings.read(getConfig());

        try
        {
            zConomyDatabase = new zConomyDatabase(this);
            zConomyDatabase.createTable();
        }
        catch(SQLException ex)
        {
            severe("Unable to create or verify if tables exist. Expect errors on shutdown as well.");
            ex.printStackTrace();
            return;
        }

        commandMoney = new CommandMoney(this);
        commandMoney.register();

        getServer().getPluginManager().registerEvents(this, this);

        zConomyVault = new zConomyVault(this);
        Bukkit.getServicesManager().register(Economy.class, zConomyVault, this, ServicePriority.High);
    }

    public void onDisable()
    {
        zConomyVault = null;

        commandMoney.shutdown();
        commandMoney = null;

        zConomyDatabase.shutdown();
        zConomyDatabase = null;
    }

    public zConomyDatabase getzConomyDatabase()
    {
        return zConomyDatabase;
    }

    public void info(String string)
    {
        getLogger().info(string);
    }

    public void severe(String string)
    {
        getLogger().info(string);
    }

    public void debug(String string)
    {
        if(Settings.isDebugEnabled())
        {
            getLogger().info("[Debug] " + string);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        String playerName = event.getPlayer().getName();
        BankAccount account = zConomyDatabase.getBalance(playerName);

        if(account == null)
        {
            zConomyDatabase.createAccount(playerName);
        }
    }
}
