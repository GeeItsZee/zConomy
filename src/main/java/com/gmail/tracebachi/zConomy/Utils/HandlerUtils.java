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
package com.gmail.tracebachi.zConomy.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 3/6/16.
 */
public interface HandlerUtils
{
    static Integer parseInt(String source)
    {
        try
        {
            return Integer.parseInt(source);
        }
        catch(NumberFormatException ex)
        {
            return null;
        }
    }

    static Double parseDouble(String source)
    {
        try
        {
            return Double.parseDouble(source);
        }
        catch(NumberFormatException ex)
        {
            return null;
        }
    }

    static void sendMessage(String name, String message)
    {
        Player player = Bukkit.getPlayer(name);

        if(player != null)
        {
            player.sendMessage(message);
        }
    }
}