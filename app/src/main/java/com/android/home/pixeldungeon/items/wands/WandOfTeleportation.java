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
package com.android.home.pixeldungeon.items.wands;

import com.android.home.pixeldungeon.noosa.audio.Sample;
import com.android.home.pixeldungeon.Assets;
import com.android.home.pixeldungeon.Dungeon;
import com.android.home.pixeldungeon.actors.Actor;
import com.android.home.pixeldungeon.actors.Char;
import com.android.home.pixeldungeon.effects.MagicMissile;
import com.android.home.pixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.android.home.pixeldungeon.utils.GLog;
import com.android.home.pixeldungeon.utils.Callback;

public class WandOfTeleportation extends Wand {

	{
		name = "Wand of Teleportation";
	}

	@Override
	protected void onZap( int cell ) {
		
		Char ch = Actor.findChar( cell );
		
		if (ch == curUser) {
			
			setKnown();
			ScrollOfTeleportation.teleportHero( curUser );
			
		} else if (ch != null) {
			
			int count = 10;
			int pos;
			do {
				pos = Dungeon.level.randomRespawnCell();
				if (count-- <= 0) {
					break;
				}
			} while (pos == -1);
			
			if (pos == -1) {
				
				GLog.w( ScrollOfTeleportation.TXT_NO_TELEPORT );
				
			} else {
			
				ch.pos = pos;
				ch.sprite.place( ch.pos );
				ch.sprite.visible = Dungeon.visible[pos];
				GLog.i( curUser.name + " teleported " + ch.name + " to somewhere" );
				
			}

		} else {
			
			GLog.i( "nothing happened" );
			
		}
	}
	
	protected void fx( int cell, Callback callback ) {
		MagicMissile.coldLight( curUser.sprite.parent, curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}
	
	@Override
	public String desc() {
		return
			"A blast from this wand will teleport a creature against " +
			"its will to a random place on the current level.";
	}
}
