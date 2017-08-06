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

import com.gmail.tracebachi.zConomy.Handlers.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class CommandMoney implements CommandExecutor
{
  private static final String COMMAND_NAME = "money";

  private final zConomyPlugin plugin;
  private final Settings settings;
  private final Balance balance;
  private final Create create;
  private final Give give;
  private final Help help;
  private final Pay pay;
  private final Reload reload;
  private final Remove remove;
  private final Set set;
  private final Take take;
  private final Top top;

  public CommandMoney(zConomyPlugin plugin, Settings settings)
  {
    this.plugin = plugin;
    this.settings = settings;
    this.balance = new Balance(plugin, settings);
    this.create = new Create(plugin, settings);
    this.give = new Give(plugin, settings);
    this.pay = new Pay(plugin, settings);
    this.help = new Help(settings);
    this.reload = new Reload(plugin, settings);
    this.remove = new Remove(plugin, settings);
    this.set = new Set(plugin, settings);
    this.take = new Take(plugin, settings);
    this.top = new Top(plugin, settings);
  }

  public void register()
  {
    plugin.getCommand(COMMAND_NAME).setExecutor(this);
  }

  public void unregister()
  {
    plugin.getCommand(COMMAND_NAME).setExecutor(null);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
  {
    if (args.length == 0)
    {
      balance.handle(sender, sender.getName());
    }
    else if (args[0].equalsIgnoreCase("create"))
    {
      if (args.length < 2)
      {
        sender.sendMessage(settings.format("CreateUsage"));
      }
      else
      {
        create.handle(sender, args[1]);
      }
    }
    else if (args[0].equalsIgnoreCase("give"))
    {
      if (args.length < 3)
      {
        sender.sendMessage(settings.format("GiveUsage"));
      }
      else
      {
        give.handle(sender, args[1], args[2]);
      }
    }
    else if (args[0].equalsIgnoreCase("help"))
    {
      help.handle(sender);
    }
    else if (args[0].equalsIgnoreCase("pay"))
    {
      if (args.length < 3)
      {
        sender.sendMessage(settings.format("PayUsage"));
      }
      else
      {
        pay.handle(sender, args[1], args[2]);
      }
    }
    else if (args[0].equalsIgnoreCase("reload"))
    {
      reload.handle(sender);
    }
    else if (args[0].equalsIgnoreCase("remove"))
    {
      if (args.length < 2)
      {
        sender.sendMessage(settings.format("RemoveUsage"));
      }
      else
      {
        remove.handle(sender, args[1]);
      }
    }
    else if (args[0].equalsIgnoreCase("set"))
    {
      if (args.length < 3)
      {
        sender.sendMessage(settings.format("SetUsage"));
      }
      else
      {
        set.handle(sender, args[1], args[2]);
      }
    }
    else if (args[0].equalsIgnoreCase("take"))
    {
      if (args.length < 3)
      {
        sender.sendMessage(settings.format("TakeUsage"));
      }
      else
      {
        take.handle(sender, args[1], args[2]);
      }
    }
    else if (args[0].equalsIgnoreCase("top"))
    {
      top.handle(sender);
    }
    else
    {
      balance.handle(sender, args[0]);
    }

    return true;
  }
}
