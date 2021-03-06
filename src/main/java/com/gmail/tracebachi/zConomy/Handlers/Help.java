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
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class Help
{
  private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\\\n");

  private final Settings settings;

  public Help(Settings settings)
  {
    this.settings = settings;
  }

  public void handle(CommandSender sender)
  {
    for (String line : NEWLINE_PATTERN.split(settings.format("HelpMessage")))
    {
      sender.sendMessage(line);
    }
  }
}
