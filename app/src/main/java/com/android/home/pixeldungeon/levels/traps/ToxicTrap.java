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
package com.android.home.pixeldungeon.levels.traps;

import com.android.home.pixeldungeon.Dungeon;
import com.android.home.pixeldungeon.actors.Char;
import com.android.home.pixeldungeon.actors.blobs.Blob;
import com.android.home.pixeldungeon.actors.blobs.ToxicGas;
import com.android.home.pixeldungeon.scenes.GameScene;

public class ToxicTrap {

	// 0x40CC55
	
	public static void trigger( int pos, Char ch ) {
		
		GameScene.add( Blob.seed( pos, 300 + 20 * Dungeon.depth, ToxicGas.class ) );
		
	}
}
