package main;

public final class Constants {


    /* Protocol*/
    public static final byte PROTOCOL_TIME_SYNC = 1;
    public static final byte PROTOCOL_GAME_STATE = 2;
    public static final byte PROTOCOL_ID_DISTRIBUTION = 3;
    public static final byte PROTOCOL_GAME_INPUT = 4;
    /* Object States*/
    public static final int ALIVE = 0;
    public static final int DYING0 = 1;
    public static final int DYING1 = 2;
    public static final int DEAD = 3;

    /* Types of input*/
    public static final byte STAND_RIGHT = 0;
    public static final byte STAND_LEFT = 1;
    public static final byte STAND_UP = 2;
    public static final byte STAND_DOWN = 3;
    public static final byte WALK_RIGHT = 4;
    public static final byte WALK_LEFT = 5;
    public static final byte WALK_UP = 6;
    public static final byte WALK_DOWN = 7;
    public static final byte PLACE_BOMB = 20;
    public static final byte NO_ACTION = 21;

    /* Object types*/
    public final static int GOT_ROBOT = 1;
    public final static int GOT_ROBOT_BOMB = 2;
    public final static int GOT_EXPLOSION = 3;
    public final static int GOT_BLOCK = 4;
    public final static int GOT_CRATE = 5;

    /* Types of animations*/
    public static final int ANIM_STAND_RIGHT = STAND_RIGHT;
    public static final int ANIM_STAND_LEFT = STAND_LEFT;
    public static final int ANIM_STAND_UP = STAND_UP;
    public static final int ANIM_STAND_DOWN = STAND_DOWN;
    public static final int ANIM_WALK_RIGHT = WALK_RIGHT;
    public static final int ANIM_WALK_LEFT = WALK_LEFT;
    public static final int ANIM_WALK_UP = WALK_UP;
    public static final int ANIM_WALK_DOWN = WALK_DOWN;
    public static final int ANIM_PLACE_BOMB = PLACE_BOMB;
    public static final int ANIM_DYING = 8;

    public static final int ANIM_BOMB = 0;
    public static final int ANIM_EXPLOSION_MIDDLE = 0;
    public static final int ANIM_EXPLOSION_HORIZONTAL = 1;
    public static final int ANIM_EXPLOSION_VERTICAL = 2;

    public static final int ANIM_FIELD0 = 0;

    public static final int ANIM_BACKGROUND = 0;
    public static final int ANIM_BACKGROUND_WHITE = 1;

    public static final int ANIM_BUTTON_CLIENT_UNPRESSED = 0;
    public static final int ANIM_BUTTON_CLIENT_PRESSED = 1;


    public static final int ANIM_BUTTON_SERVER_UNPRESSED = 0;
    public static final int ANIM_BUTTON_SERVER_PRESSED = 1;

    public static final int ANIM_BUTTON_NORMAL_UNPRESSED = 0;
    public static final int ANIM_BUTTON_NORMAL_PRESSED = 1;

    public static final int ANIM_CIRCLE = 0;
    public static final int ANIM_SQUARE = 0;

    public final static int ANIM_CRATE0 = 0;

    public final static int ANIM_BLOCK0 = 0;

    /* Types of Animation sets*/
    public static final int ANIMSET_FIELD = 0;

    public static final int ANIMSET_BACKGROUND = 20;
    public static final int ANIMSET_BACKGROUND_FIELD = 26;
    public final static int ANIMSET_ROBOT = GOT_ROBOT;
    public final static int ANIMSET_ROBOT_BOMB = GOT_ROBOT_BOMB;
    public final static int ANIMSET_EXPLOSION = GOT_EXPLOSION;
    public final static int ANIMSET_BLOCK = GOT_BLOCK;
    public final static int ANIMSET_CRATE = GOT_CRATE;

    public final static int ANIMSET_BUTTONS_CLIENT = 21;

    public final static int ANIMSET_CIRCLE = 22;

    public final static int ANIMSET_BUTTONS_SERVER = 23;

    public final static int ANIMSET_BUTTONS_NORMAL = 24;

    public final static int ANIMSET_SQUARE = 25;

    /* Types of objects: Extra*/
    public final static int EXTRACT_ID = 0x100fffff;    // TODO if we ever have object overrun then there we need to change this approach
    public final static int BLOCK = 0x01000000;
    public final static int BOMB = 0x02000000;
    public final static int EXPLOSION = 0x04000000;
    public final static int CRATE = 0x08000000;
    public final static int ITEM = 0x10000000;
    public final static int EXTRACT_INFO = 0xff000000;

    //////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /* DEBUG_INFORMATION*/
    public final static boolean DEBUG_DRAW_HITBOXES = true;
    public final static boolean DEBUG_DRAW_DEBUG_INFO = true;

    /* for 1920 x 1080 , field is 1540 x 1034*/
    public final static int FIELD_X1 = 190;
    public final static int FIELD_X2 = 1730;
    public final static int FIELD_Y1 = 36;
    public final static int FIELD_Y2 = 1070;

    public final static int NUMBER_OF_X_CELLS = 11;
    public final static int NUMBER_OF_Y_CELLS = 11;

    // CELLSIZEX = 140    // CELLSIZEY = 94
    public final static int CELLSIZE_X = (FIELD_X2 - FIELD_X1) / NUMBER_OF_X_CELLS;
    public final static int CELLSIZE_Y = (FIELD_Y2 - FIELD_Y1) / NUMBER_OF_Y_CELLS;
    public final static float CELL_RATIO = (float) CELLSIZE_Y / CELLSIZE_X;

    /* Standard Globals for Game*/
    public final static float GAME_WIDTH = 1920;
    public final static float GAME_HEIGHT = 1080;
    public final static float GAME_DPI = 480;

    // if block is 150 x 150
    public final static int BLOCK_BOX_WIDTH = 140;
    public final static int BLOCK_BOX_HEIGHT = 94;
    public final static int BLOCK_BOX_OFFSET_X = 0;
    public final static int BLOCK_BOX_OFFSET_Y = 0;

    // if block is 150 x 150
    public final static int CRATE_BOX_WIDTH = 140;
    public final static int CRATE_BOX_HEIGHT = 94;
    public final static int CRATE_BOX_OFFSET_X = 0;
    public final static int CRATE_BOX_OFFSET_Y = 0;

    // if player sprite is 180 x 180
    public final static int PLAYER_BOX_WIDTH = 60;
    public final static int PLAYER_BOX_HEIGHT = (int) (CELL_RATIO * PLAYER_BOX_WIDTH);
    public final static int PLAYER_BOX_OFFSET_X = 60;
    public final static int PLAYER_BOX_OFFSET_Y = (int) ((PLAYER_BOX_OFFSET_X + 110) * CELL_RATIO);
    public final static float PLAYER_BASE_SPEED_X = 0.5f;
    public final static int PLAYER_BOMB_STARTING_AMOUNT = 10;
    public final static int PLAYER_BOMB_EXPLOSION_STRENGTH = 1;

    public final static int BOMB_BOX_WIDTH = 60;
    public final static int BOMB_BOX_HEIGHT = (int) (CELL_RATIO * BOMB_BOX_WIDTH);
    public final static int BOMB_BOX_OFFSET_X = 40;
    public final static int BOMB_BOX_OFFSET_Y = (int) (BOMB_BOX_OFFSET_X * CELL_RATIO);
    public final static int BOMB_TIMER = 3000;

    public final static long EXPLOSION_TIME = 1000;

    public final static int[][] STARTING_CELL_POSITIONS = new int[][]{{0, 0}, {10, 0}};

    public final static int LEVEL1_MAP = 0;
    public final static int LEVEL2_MAP = 1;
    public final static int[][][] LEVELS = new int[][][]
            {{{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 1, 0, 2, 0, 1, 2, 1, 2},
                    {0, 0, 0, 0, 2, 0, 2, 2, 0, 0, 0},
                    {0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            },
                    {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {0, 1, 0, 0, 0, 2, 0, 0, 2, 1, 2},
                            {0, 0, 0, 0, 2, 0, 2, 2, 0, 0, 0},
                            {0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0},
                            {0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0},
                            {0, 1, 0, 2, 0, 0, 0, 0, 0, 1, 0},
                            {0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0},
                            {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    },

            };

    /* Other Constants */
    public final static int MAX_FRAME_TIME = (int) (1000.0 / 60.0);
    public final static int MAX_TOUCH_INSTANCES = 2;
    public final static int MAX_NUMBER_OF_PLAYERS = 2;
    public final static int MOVEMENT_THRESHOLD = 20;
    public final static int BOUNDS = 20;
    public final static int DISCOVERABLE_REQUEST_CODE = 0x1;
    public final static int BLUETOOTH_ENABLE_REQUEST_CODE = 0x2;
    public final static String SERVER_UUID = "4e5d48e0-75df-11e3-981f-0800200c9a66";
    public final static String SERVER_NAME = "BOMBER";
    public final static int NUM_OF_PACKET_DESCS = 10;
    public final static int MAX_PACKET_SIZE = 30;
    public final static int INPUT_LAG = 75;

}
