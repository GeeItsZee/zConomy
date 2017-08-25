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

import com.gmail.tracebachi.SockExchange.Spigot.SockExchangeApi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class zConomyPlugin extends JavaPlugin implements Listener
{
  private boolean hasSockExchange = false;
  private Settings settings;
  private zConomyDatabase zConomyDatabase;
  private zConomyVault zConomyVault;
  private CommandMoney commandMoney;

  @Override
  public void onEnable()
  {
    saveDefaultConfig();
    reloadConfig();

    settings = new Settings();
    settings.read(getConfig());

    zConomyDatabase = new zConomyDatabase(settings);
    if (!zConomyDatabase.createTable())
    {
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    zConomyVault = new zConomyVault(this, settings);
    Bukkit.getServicesManager().register(Economy.class, zConomyVault, this, ServicePriority.High);

    Plugin sockExchangePlugin = getServer().getPluginManager().getPlugin("SockExchange");
    hasSockExchange = sockExchangePlugin != null;

    commandMoney = new CommandMoney(this, settings);
    commandMoney.register();

    getServer().getPluginManager().registerEvents(this, this);
  }

  public void onDisable()
  {
    HandlerList.unregisterAll((JavaPlugin) this);

    if (commandMoney != null)
    {
      commandMoney.unregister();
      commandMoney = null;
    }

    zConomyVault = null;
    zConomyDatabase = null;
    settings = null;
  }

  public zConomyDatabase getzConomyDatabase()
  {
    return zConomyDatabase;
  }

  public void sendMessage(String name, String message)
  {
    if (!hasSockExchange)
    {
      Player player = getServer().getPlayer(name);

      if (player != null)
      {
        player.sendMessage(message);
      }
    }
    else
    {
      SockExchangeApi api = SockExchangeApi.instance();
      api.sendChatMessages(Collections.singletonList(message), name, null);
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    String playerName = event.getPlayer().getName();
    BankAccount account = zConomyDatabase.getBankAccount(playerName);

    if (account == null)
    {
      zConomyDatabase.createBankAccount(playerName);
    }
  }
}
