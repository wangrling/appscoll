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
import com.android.home.pixeldungeon.effects.Swap;
import com.android.home.pixeldungeon.items.Dewdrop;
import com.android.home.pixeldungeon.items.Heap;
import com.android.home.pixeldungeon.items.Item;
import com.android.home.pixeldungeon.items.potions.Potion;
import com.android.home.pixeldungeon.items.potions.PotionOfMight;
import com.android.home.pixeldungeon.items.potions.PotionOfStrength;
import com.android.home.pixeldungeon.items.scrolls.Scroll;
import com.android.home.pixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.android.home.pixeldungeon.items.scrolls.ScrollOfEnchantment;
import com.android.home.pixeldungeon.levels.Level;
import com.android.home.pixeldungeon.levels.Terrain;
import com.android.home.pixeldungeon.mechanics.Ballistica;
import com.android.home.pixeldungeon.scenes.GameScene;
import com.android.home.pixeldungeon.utils.GLog;
import com.android.home.pixeldungeon.utils.Callback;

public class WandOfReach extends Wand {

	private static final String TXT_YOU_NOW_HAVE	= "You have magically transported %s into your backpack"; 
	
	{
		name = "Wand of Reach";
		hitChars = false;
	}
	
	@Override
	protected void onZap( int cell ) {
		
		int reach = Math.min( Ballistica.distance, power() + 4 );
		
		boolean mapUpdated = false;
		for (int i=1; i < reach; i++) {
			
			int c = Ballistica.trace[i];
			
			int before = Dungeon.level.map[c];
			
			Char ch = Actor.findChar( c );
			if (ch != null) {
				Actor.addDelayed( new Swap( curUser, ch ), -1 );
				break;
			}
			
			Heap heap = Dungeon.level.heaps.get( c );
			if (heap != null) {
				switch (heap.type) {
				case HEAP:
					transport( heap );
					break;
				case CHEST:
				case MIMIC:
				case TOMB:
				case SKELETON:
					heap.open( curUser );
					break;
				default:
				}
				
				break;
			}
			
			Dungeon.level.press( c, null );
			if (before == Terrain.OPEN_DOOR) {	
				Level.set( c, Terrain.DOOR );
				GameScene.updateMap( c );
			} else if (Level.water[c]) {
				GameScene.ripple( c );
			}
			
			mapUpdated = mapUpdated || Dungeon.level.map[c] != before;
		}
		
		if (mapUpdated) {
			Dungeon.observe();
		}
	}
	
	private void transport( Heap heap ) {
		Item item = heap.pickUp();
		if (item.doPickUp( curUser )) {
			
			if (item instanceof Dewdrop) {
				// Do nothing
			} else {
				if (((item instanceof ScrollOfUpgrade || item instanceof ScrollOfEnchantment) && ((Scroll)item).isKnown()) ||
					((item instanceof PotionOfStrength || item instanceof PotionOfMight) && ((Potion)item).isKnown())) {
					GLog.p( TXT_YOU_NOW_HAVE, item.name() );
				} else {
					GLog.i( TXT_YOU_NOW_HAVE, item.name() );
				}
			}

		} else {
			Dungeon.level.drop( item, curUser.pos ).sprite.drop();
		}
	}
	
	protected void fx( int cell, Callback callback ) {
		MagicMissile.force( curUser.sprite.parent, curUser.pos, cell, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}
	
	@Override
	public String desc() {
		return
			"This utility wand can be used to grab objects from a distance and to switch places with enemies. " +
			"Waves of magic force radiated from it will affect all cells on their way triggering traps, " +
			"trampling high vegetation, opening closed doors and closing open ones.";
	}
}
