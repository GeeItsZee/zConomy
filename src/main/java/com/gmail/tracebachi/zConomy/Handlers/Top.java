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

import com.gmail.tracebachi.zConomy.Storage.BankAccount;
import com.gmail.tracebachi.zConomy.Storage.Settings;
import com.gmail.tracebachi.zConomy.Utils.HandlerUtils;
import com.gmail.tracebachi.zConomy.zConomy;
import com.gmail.tracebachi.zConomy.zConomyDatabase;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public class Top
{
    private final zConomy plugin;

    public Top(zConomy plugin)
    {
        this.plugin = plugin;
    }

    public void handle(CommandSender sender)
    {
        if(!sender.hasPermission("zConomy.Top"))
        {
            sender.sendMessage(Settings.format("NoPermission", "zConomy.Top"));
            return;
        }

        zConomyDatabase database = plugin.getzConomyDatabase();
        List<BankAccount> topAccounts = database.getTopAccounts();

        sender.sendMessage(Settings.format("TopListHeader"));

        for(int i = 0; i < topAccounts.size(); ++i)
        {
            BankAccount account = topAccounts.get(i);
            sender.sendMessage(Settings.format("TopListItemFormat",
                String.valueOf(i), account.getOwner(),
                HandlerUtils.formatAmount(account.getBalance())));
        }
    }
}
