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

import com.gmail.tracebachi.zConomy.Events.PaymentEvent;
import com.gmail.tracebachi.zConomy.Storage.BankAccount;
import com.gmail.tracebachi.zConomy.Storage.Settings;
import com.gmail.tracebachi.zConomy.Utils.HandlerUtils;
import com.gmail.tracebachi.zConomy.zConomy;
import com.gmail.tracebachi.zConomy.zConomyDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Map;

import static com.gmail.tracebachi.zConomy.Utils.HandlerUtils.formatAmount;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public class Pay
{
    private final zConomy plugin;

    public Pay(zConomy plugin)
    {
        this.plugin = plugin;
    }

    public void handle(CommandSender sender, String receiver, String amountString)
    {
        if(!sender.hasPermission("zConomy.Pay"))
        {
            sender.sendMessage(Settings.format("NoPermission", "zConomy.Pay"));
            return;
        }

        if(!(sender instanceof Player) || receiver.equalsIgnoreCase("console"))
        {
            sender.sendMessage(Settings.format("NoAccountForConsole"));
            return;
        }

        Double amount = HandlerUtils.parseDouble(amountString);

        if(amount == null || amount <= 0)
        {
            sender.sendMessage(Settings.format("InvalidAmount", amountString));
            return;
        }

        if(sender.getName().equalsIgnoreCase(receiver))
        {
            sender.sendMessage(Settings.format("PayToSelf"));
            return;
        }

        zConomyDatabase database = plugin.getzConomyDatabase();

        try
        {
            Map<String, BankAccount> balances = database.getBalances(sender.getName(), receiver);
            BankAccount senderAccount = balances.get(sender.getName().toLowerCase());
            BankAccount receiverAccount = balances.get(receiver.toLowerCase());

            if(senderAccount == null)
            {
                sender.sendMessage(Settings.format("NoAccountFound", sender.getName()));
                return;
            }

            if(receiverAccount == null)
            {
                sender.sendMessage(Settings.format("NoAccountFound", receiver));
                return;
            }

            if(senderAccount.getBalance().compareTo(new BigDecimal(amount)) < 0)
            {
                sender.sendMessage(Settings.format("InsufficientFunds", formatAmount(amount)));
                return;
            }

            database.addToBalance(sender.getName(), -amount);
            sender.sendMessage(Settings.format("PaySent", formatAmount(amount), receiver));

            database.addToBalance(receiver, amount);
            HandlerUtils.sendMessage(receiver, Settings.format("PayReceived", formatAmount(amount), sender.getName()));

            PaymentEvent event = new PaymentEvent(sender.getName(), receiver, amount);
            Bukkit.getPluginManager().callEvent(event);

            plugin.info(sender.getName() + " paid " + receiver + " " + amount);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            sender.sendMessage(Settings.format("NoAccountFound", receiver));
        }
    }
}
