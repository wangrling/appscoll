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
package com.android.home.pixeldungeon.actors.mobs;

import java.util.HashSet;

import com.android.home.pixeldungeon.noosa.Camera;
import com.android.home.pixeldungeon.noosa.audio.Sample;
import com.android.home.pixeldungeon.Assets;
import com.android.home.pixeldungeon.Badges;
import com.android.home.pixeldungeon.Dungeon;
import com.android.home.pixeldungeon.Statistics;
import com.android.home.pixeldungeon.actors.Actor;
import com.android.home.pixeldungeon.actors.Char;
import com.android.home.pixeldungeon.actors.blobs.Blob;
import com.android.home.pixeldungeon.actors.blobs.ToxicGas;
import com.android.home.pixeldungeon.actors.buffs.Buff;
import com.android.home.pixeldungeon.actors.buffs.Paralysis;
import com.android.home.pixeldungeon.effects.CellEmitter;
import com.android.home.pixeldungeon.effects.Speck;
import com.android.home.pixeldungeon.effects.particles.ElmoParticle;
import com.android.home.pixeldungeon.items.keys.SkeletonKey;
import com.android.home.pixeldungeon.items.rings.RingOfThorns;
import com.android.home.pixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.android.home.pixeldungeon.items.weapon.enchantments.Death;
import com.android.home.pixeldungeon.levels.Level;
import com.android.home.pixeldungeon.levels.Terrain;
import com.android.home.pixeldungeon.scenes.GameScene;
import com.android.home.pixeldungeon.sprites.DM300Sprite;
import com.android.home.pixeldungeon.utils.GLog;
import com.android.home.pixeldungeon.utils.Random;

public class DM300 extends Mob {
	
	{
		name = Dungeon.depth == Statistics.deepestFloor ? "DM-300" : "DM-350";
		spriteClass = DM300Sprite.class;
		
		HP = HT = 200;
		EXP = 30;
		defenseSkill = 18;
		
		loot = new RingOfThorns().random();
		lootChance = 0.333f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 18, 24 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28;
	}
	
	@Override
	public int dr() {
		return 10;
	}
	
	@Override
	public boolean act() {
		GameScene.add( Blob.seed( pos, 30, ToxicGas.class ) );
		return super.act();
	}
	
	@Override
	public void move( int step ) {
		super.move( step );
		
		if (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && HP < HT) {
			
			HP += Random.Int( 1, HT - HP );
			sprite.emitter().burst( ElmoParticle.FACTORY, 5 );
			
			if (Dungeon.visible[step] && Dungeon.hero.isAlive()) {
				GLog.n( "DM-300 repairs itself!" );
			}
		}

		int[] cells = {
			step-1, step+1, step-Level.WIDTH, step+Level.WIDTH, 
			step-1-Level.WIDTH, 
			step-1+Level.WIDTH, 
			step+1-Level.WIDTH, 
			step+1+Level.WIDTH
		};
		int cell = cells[Random.Int( cells.length )];
		
		if (Dungeon.visible[cell]) {
			CellEmitter.get( cell ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
			Camera.main.shake( 3, 0.7f );
			Sample.INSTANCE.play( Assets.SND_ROCKS );
			
			if (Level.water[cell]) {
				GameScene.ripple( cell );
			} else if (Dungeon.level.map[cell] == Terrain.EMPTY) {
				Level.set( cell, Terrain.EMPTY_DECO );
				GameScene.updateMap( cell );
			}
		}

		Char ch = Actor.findChar( cell );
		if (ch != null && ch != this) {
			Buff.prolong( ch, Paralysis.class, 2 );
		}
	}
	
	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey(), pos ).sprite.drop();
		
		Badges.validateBossSlain();
		
		yell( "Mission failed. Shutting down." );
	}
	
	@Override
	public void notice() {
		super.notice();
		yell( "Unauthorised personnel detected." );
	}
	
	@Override
	public String description() {
		return
			"This machine was created by the Dwarves several centuries ago. Later, Dwarves started to replace machines with " +
			"golems, elementals and even demons. Eventually it led their civilization to the decline. The DM-300 and similar " +
			"machines were typically used for construction and mining, and in some cases, for city defense.";
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
	
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
	static {
		IMMUNITIES.add( ToxicGas.class );
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
