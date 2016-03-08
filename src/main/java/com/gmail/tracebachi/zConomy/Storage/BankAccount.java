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
package com.gmail.tracebachi.zConomy.Storage;

import java.math.BigDecimal;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public class BankAccount
{
    private final String owner;
    private final BigDecimal balance;
    private final long lastUpdateAt;

    public BankAccount(String owner, BigDecimal balance)
    {
        this.owner = owner;
        this.balance = balance;
        this.lastUpdateAt = System.currentTimeMillis();
    }

    public BankAccount(String owner, BigDecimal balance, long lastUpdateAt)
    {
        this.owner = owner;
        this.balance = balance;
        this.lastUpdateAt = lastUpdateAt;
    }

    public String getOwner()
    {
        return owner;
    }

    public BigDecimal getBalance()
    {
        return balance;
    }

    public long getLastUpdateAt()
    {
        return lastUpdateAt;
    }

    @Override
    public String toString()
    {
        return "{owner: " + owner + ", balance: " + balance + ", lastUpdateAt:" + lastUpdateAt + "}";
    }
}
