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
package com.gmail.tracebachi.zConomy.Handlers;

import com.gmail.tracebachi.zConomy.Exceptions.ExistingBankAccountException;
import com.gmail.tracebachi.zConomy.Storage.Settings;
import com.gmail.tracebachi.zConomy.zConomy;
import com.gmail.tracebachi.zConomy.zConomyDatabase;
import org.bukkit.command.CommandSender;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public class Create
{
    private final zConomy plugin;

    public Create(zConomy plugin)
    {
        this.plugin = plugin;
    }

    public void handle(CommandSender sender, String name)
    {
        if(!sender.hasPermission("zConomy.Create"))
        {
            sender.sendMessage(Settings.format("NoPermission", "zConomy.Create"));
            return;
        }

        if(name.equalsIgnoreCase("console"))
        {
            sender.sendMessage(Settings.format("NoAccountForConsole"));
            return;
        }

        zConomyDatabase database = plugin.getzConomyDatabase();

        try
        {
            database.createAccount(name);
            sender.sendMessage(Settings.format("AccountCreated", name));
        }
        catch(ExistingBankAccountException ex)
        {
            sender.sendMessage(Settings.format("ExistingAccountFound", name));
        }
    }
}
