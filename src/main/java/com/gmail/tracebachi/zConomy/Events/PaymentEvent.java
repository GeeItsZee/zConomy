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
package com.gmail.tracebachi.zConomy.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/7/16.
 */
public class PaymentEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private final String sender;
    private final String receiver;
    private final double amount;

    public PaymentEvent(String sender, String receiver, double amount)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public String getSender()
    {
        return sender;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public double getAmount()
    {
        return amount;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}