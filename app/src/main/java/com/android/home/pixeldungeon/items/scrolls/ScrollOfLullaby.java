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
package com.android.home.pixeldungeon.items.scrolls;

import com.android.home.pixeldungeon.noosa.audio.Sample;
import com.android.home.pixeldungeon.Assets;
import com.android.home.pixeldungeon.Dungeon;
import com.android.home.pixeldungeon.actors.buffs.Buff;
import com.android.home.pixeldungeon.actors.buffs.Invisibility;
import com.android.home.pixeldungeon.actors.buffs.Sleep;
import com.android.home.pixeldungeon.actors.mobs.Mob;
import com.android.home.pixeldungeon.effects.Speck;
import com.android.home.pixeldungeon.levels.Level;
import com.android.home.pixeldungeon.utils.GLog;

public class ScrollOfLullaby extends Scroll {

	{
		name = "Scroll of Lullaby";
	}
	
	@Override
	protected void doRead() {
		
		curUser.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );
		Sample.INSTANCE.play( Assets.SND_LULLABY );
		Invisibility.dispel();
		
		int count = 0;
		Mob affected = null;
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Level.fieldOfView[mob.pos]) {
				Buff.affect( mob, Sleep.class );
				if (mob.buff( Sleep.class ) != null) {
					affected = mob;
					count++;
				}
			}
		}
		
		switch (count) {
		case 0:
			GLog.i( "The scroll utters a soothing melody." );
			break;
		case 1:
			GLog.i( "The scroll utters a soothing melody and the " + affected.name + " falls asleep!" );
			break;
		default:
			GLog.i( "The scroll utters a soothing melody and the monsters fall asleep!" );
		}
		setKnown();
		
		readAnimation();
	}
	
	@Override
	public String desc() {
		return
			"A soothing melody will put all creatures in your field of view into a deep sleep, " +
			"giving you a chance to flee or make a surprise attack on them.";
	}
	
	@Override
	public int price() {
		return isKnown() ? 50 * quantity : super.price();
	}
}
