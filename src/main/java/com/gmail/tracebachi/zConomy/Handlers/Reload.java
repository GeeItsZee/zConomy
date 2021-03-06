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
package com.gmail.tracebachi.zConomy.Handlers;

import com.gmail.tracebachi.zConomy.Settings;
import com.gmail.tracebachi.zConomy.zConomyPlugin;
import org.bukkit.command.CommandSender;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class Reload
{
  private static final String COMMAND_PERM = "zConomy.Reload";

  private final zConomyPlugin plugin;
  private final Settings settings;

  public Reload(zConomyPlugin plugin, Settings settings)
  {
    this.plugin = plugin;
    this.settings = settings;
  }

  public void handle(CommandSender sender)
  {
    if (!sender.hasPermission(COMMAND_PERM))
    {
      sender.sendMessage(settings.format("NoPermission", COMMAND_PERM));
      return;
    }

    plugin.reloadConfig();
    settings.read(plugin.getConfig());

    sender.sendMessage(settings.format("ConfigReloaded"));
  }
}
