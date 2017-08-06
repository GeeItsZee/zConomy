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

import java.math.BigDecimal;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
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
