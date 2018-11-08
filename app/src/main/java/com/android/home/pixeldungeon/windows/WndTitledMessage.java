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
package com.android.home.pixeldungeon.windows;

import com.android.home.pixeldungeon.noosa.Image;
import com.android.home.pixeldungeon.noosa.ui.Component;
import com.android.home.pixeldungeon.PixelDungeon;
import com.android.home.pixeldungeon.ui.HighlightedText;
import com.android.home.pixeldungeon.ui.Window;

public class WndTitledMessage extends Window {

	private static final int WIDTH_P	= 120;
	private static final int WIDTH_L	= 144;
	
	private static final int GAP	= 2;
	
	public WndTitledMessage( Image icon, String title, String message ) {
		
		this( new IconTitle( icon, title ), message );

	}
	
	public WndTitledMessage( Component titlebar, String message ) {
		
		super();
		
		int width = PixelDungeon.landscape() ? WIDTH_L : WIDTH_P;
		
		titlebar.setRect( 0, 0, width, 0 );
		add( titlebar );
		
		HighlightedText text = new HighlightedText( 6 );
		text.text( message, width );
		text.setPos( titlebar.left(), titlebar.bottom() + GAP );
		add( text );
		
		resize( width, (int)text.bottom() );
	}
}
