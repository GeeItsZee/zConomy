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

import com.gmail.tracebachi.zConomy.Handlers.*;
import com.gmail.tracebachi.zConomy.Utils.Registerable;
import com.gmail.tracebachi.zConomy.Utils.Shutdownable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public class CommandMoney implements CommandExecutor, Registerable, Shutdownable
{
    private zConomy plugin;
    private Balance balance;
    private Create create;
    private Give give;
    private Help help;
    private Pay pay;
    private Purge purge;
    private Remove remove;
    private Set set;
    private Take take;
    private Top top;

    public CommandMoney(zConomy plugin)
    {
        this.plugin = plugin;
        this.balance = new Balance(plugin);
        this.create = new Create(plugin);
        this.give = new Give(plugin);
        this.pay = new Pay(plugin);
        this.purge = new Purge(plugin);
        this.help = new Help();
        this.remove = new Remove(plugin);
        this.set = new Set(plugin);
        this.take = new Take(plugin);
        this.top = new Top(plugin);
    }

    @Override
    public void register()
    {
        plugin.getCommand("money").setExecutor(this);
    }

    @Override
    public void unregister()
    {
        plugin.getCommand("money").setExecutor(null);
    }

    @Override
    public void shutdown()
    {
        unregister();
        top = null;
        take = null;
        set = null;
        remove = null;
        help = null;
        purge = null;
        pay = null;
        give = null;
        create = null;
        balance = null;
        plugin = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        if(args.length == 0)
        {
            balance.handle(sender, sender.getName());
        }
        else if(args.length >= 2 && args[0].equalsIgnoreCase("create"))
        {
            create.handle(sender, args[1]);
        }
        else if(args.length >= 3 && args[0].equalsIgnoreCase("give"))
        {
            give.handle(sender, args[1], args[2]);
        }
        else if(args[0].equalsIgnoreCase("help"))
        {
            help.handle(sender);
        }
        else if(args.length >= 3 && args[0].equalsIgnoreCase("pay"))
        {
            pay.handle(sender, args[1], args[2]);
        }
        else if(args[0].equalsIgnoreCase("purge"))
        {
            purge.handle(sender);
        }
        else if(args.length >= 2 && args[0].equalsIgnoreCase("remove"))
        {
            remove.handle(sender, args[1]);
        }
        else if(args.length >= 3 && args[0].equalsIgnoreCase("set"))
        {
            set.handle(sender, args[1], args[2]);
        }
        else if(args.length >= 3 && args[0].equalsIgnoreCase("take"))
        {
            take.handle(sender, args[1], args[2]);
        }
        else if(args[0].equalsIgnoreCase("top"))
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
