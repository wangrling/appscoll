package com.android.home.pixeldungeon.scene;

import com.android.home.pixeldungeon.noosa.Group;
import com.android.home.pixeldungeon.noosa.SkinnedBlock;

public class GameScene extends PixelScene {
    private static final String TXT_WELCOME			= "Welcome to the level %d of Pixel Dungeon!";
    private static final String TXT_WELCOME_BACK	= "Welcome back to the level %d of Pixel Dungeon!";
    private static final String TXT_NIGHT_MODE		= "Be cautious, since the dungeon is even more dangerous at night!";

    private static final String TXT_CHASM	= "Your steps echo across the dungeon.";
    private static final String TXT_WATER	= "You hear the water splashing around you.";
    private static final String TXT_GRASS	= "The smell of vegetation is thick in the air.";
    private static final String TXT_SECRETS	= "The atmosphere hints that this floor hides many secrets.";

    static GameScene scene;

    private SkinnedBlock water;
    /*
    private DungeonTilemap tiles;
    private FogOfWar fog;
    private HeroSprite hero;

    private GameLog log;

    private BusyIndicator busy;

    private static CellSelector cellSelector;
*/
    private Group terrain;
    private Group ripples;
    private Group plants;
    private Group heaps;
    private Group mobs;
    private Group emitters;
    private Group effects;
    private Group gases;
    private Group spells;
    private Group statuses;
    private Group emoicons;
/*
    private Toolbar toolbar;
    private Toast prompt;
    */


}
