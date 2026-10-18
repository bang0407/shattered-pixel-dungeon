/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

//bang's: puts enemies to sleep on hit
public class Drowsy extends Weapon.Enchantment {

	private static ItemSprite.Glowing LAVENDER = new ItemSprite.Glowing( 0xB088FF );

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		int level = Math.max( 0, weapon.buffedLvl() );

		// lvl 0 - 14%
		// lvl 1 - 25%
		// lvl 2 - 33%
		float procChance = (level+1f)/(level+7f) * procChanceMultiplier(attacker);
		if (defender.isAlive() && !defender.isImmune(Sleep.class) && Random.Float() < procChance) {
			//the hit itself would wake the target, so sleep starts right after it
			Buff.affect(defender, SleepDelay.class, 0f);
			defender.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 3 );
		}

		return damage;
	}

	public static void putToSleep( Char ch ){
		if (ch.isAlive() && !ch.isImmune(Sleep.class)) {
			Buff.affect(ch, SleepDelay.class, 0f);
		}
	}

	@Override
	public Glowing glowing() {
		return LAVENDER;
	}

	public static class SleepDelay extends FlavourBuff {

		{
			actPriority = VFX_PRIO;
		}

		@Override
		public boolean act() {
			if (target.isAlive()) {
				Buff.affect(target, MagicalSleep.class);
			}
			detach();
			return true;
		}
	}
}
