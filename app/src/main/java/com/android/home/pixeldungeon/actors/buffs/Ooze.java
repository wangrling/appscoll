/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.android.home.pixeldungeon.actors.buffs;

import com.android.home.pixeldungeon.Dungeon;
import com.android.home.pixeldungeon.ResultDescriptions;
import com.android.home.pixeldungeon.levels.Level;
import com.android.home.pixeldungeon.ui.BuffIndicator;
import com.android.home.pixeldungeon.utils.GLog;
import com.android.home.pixeldungeon.utils.Utils;

public class Ooze extends Buff {
	
	private static final String TXT_HERO_KILLED = "%s killed you...";
	
	public int damage	= 1;
	
	@Override
	public int icon() {
		return BuffIndicator.OOZE;
	}
	
	@Override
	public String toString() {
		return "Caustic ooze";
	}
	
	@Override
	public boolean act() {
		if (target.isAlive()) {
			target.damage( damage, this );
			if (!target.isAlive() && target == Dungeon.hero) {
				Dungeon.fail( Utils.format( ResultDescriptions.OOZE, Dungeon.depth ) );
				GLog.n( TXT_HERO_KILLED, toString() );
			}
			spend( TICK );
		}
		if (Level.water[target.pos]) {
			detach();
		}
		return true;
	}
}
