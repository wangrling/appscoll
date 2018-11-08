package com.android.home.pixeldungeon.ui;

import com.android.home.pixeldungeon.noosa.BitmapTextMultiline;
import com.android.home.pixeldungeon.noosa.ui.Component;
import com.android.home.pixeldungeon.scenes.PixelScene;
import com.android.home.pixeldungeon.utils.Highlighter;

public class HighlightedText extends Component {

	protected BitmapTextMultiline normal;
	protected BitmapTextMultiline highlighted;
	
	protected int nColor = 0xFFFFFF;
	protected int hColor = 0xFFFF44;
	
	public HighlightedText( float size ) {
		normal = PixelScene.createMultiline( size );
		add( normal );
		
		highlighted = PixelScene.createMultiline( size );
		add( highlighted );
		
		setColor( 0xFFFFFF, 0xFFFF44 );
	}
	
	@Override
	protected void layout() {
		normal.x = highlighted.x = x;
		normal.y = highlighted.y = y;
	}
	
	public void text( String value, int maxWidth ) {
		Highlighter hl = new Highlighter( value );
		
		normal.text( hl.text );
		normal.maxWidth = maxWidth;
		normal.measure();
		
		if (hl.isHighlighted()) {
			normal.mask = hl.inverted();
			
			highlighted.text( hl.text );
			highlighted.maxWidth = maxWidth;
			highlighted.measure();
			
			highlighted.mask = hl.mask;
			highlighted.visible = true;
		} else {
			highlighted.visible = false;
		}
		
		width = normal.width();
		height = normal.height();
	}
	
	public void setColor( int n, int h ) {
		normal.hardlight( n );
		highlighted.hardlight( h );
	}
}
