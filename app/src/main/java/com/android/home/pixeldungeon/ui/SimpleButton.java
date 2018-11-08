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
package com.android.home.pixeldungeon.ui;

import com.android.home.pixeldungeon.input.Touchscreen.Touch;
import com.android.home.pixeldungeon.noosa.Image;
import com.android.home.pixeldungeon.noosa.TouchArea;
import com.android.home.pixeldungeon.noosa.ui.Component;

public class SimpleButton extends Component {
			
	private Image image;
	
	public SimpleButton( Image image ) {
		super();
		
		this.image.copy( image );
		width = image.width;
		height = image.height;
	}
	
	@Override
	protected void createChildren() {
		image = new Image();
		add( image );
		
		add( new TouchArea( image ) {
			@Override
			protected void onTouchDown(Touch touch) {
				image.brightness( 1.2f );
			};
			@Override
			protected void onTouchUp(Touch touch) {
				image.brightness( 1.0f );
			};
			@Override
			protected void onClick( Touch touch ) {
				SimpleButton.this.onClick();
			};
		} );
	}
	
	@Override
	protected void layout() {
		image.x = x;
		image.y = y;
	}
	
	protected void onClick() {};
}
