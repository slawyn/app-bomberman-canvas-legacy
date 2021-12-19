package main.Essentials;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;

import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.Globals;
import main.Constants;
import main.ResourceLoader;
import main.gameobjects.GameObjectContainer;
import main.nongameobject.Background;
import main.nongameobject.Button;
import main.nongameobject.NonGameObjectContainer;
import main.nongameobject.Touch;
import main.wrapper.Connector;


public class App extends Pipe {

    /* STATES */
    private static int gameState;
    private static final int S0_LOADING = 0;
    private static final int S0_SELECTION_SCREEN = S0_LOADING + 1;
    private static final int S0_CLIENT_DISCOVER = S0_SELECTION_SCREEN + 1;
    private static final int S0_CLIENT_SELECT_SERVER = S0_CLIENT_DISCOVER + 1;
    private static final int S0_CLIENT_CREATE_GAME = S0_CLIENT_SELECT_SERVER + 1;
    private static final int S0_CLIENT_GAME_RUNNING = S0_CLIENT_CREATE_GAME + 1;
    private static final int S0_SERVER_ENABLE_BLUETOOTH = S0_CLIENT_GAME_RUNNING + 1;
    private static final int S0_SERVER_CREATE_GAME = S0_SERVER_ENABLE_BLUETOOTH + 1;
    private static final int S0_SERVER_WAIT_FOR_CLIENTS = S0_SERVER_CREATE_GAME + 1;
    private static final int S0_SERVER_GAME_RUNNING = S0_SERVER_WAIT_FOR_CLIENTS + 1;
    private static final int S0_OFFLINE_START = S0_SERVER_GAME_RUNNING + 1;
    private static final int S0_OFFLINE_GAME_RUNNING = S0_OFFLINE_START + 1;
    private static final int S0_SERVER_DISCOVERABLE = S0_OFFLINE_GAME_RUNNING + 1;
    private static final int S0_IS_BLUETOOTH_ENABLED = S0_SERVER_DISCOVERABLE + 1;
    private static final int S0_SERVER_WAIT_FOR_DISCOVERABLE = S0_IS_BLUETOOTH_ENABLED + 1;
    private static final int S0_CLIENT_SYNC = S0_SERVER_WAIT_FOR_DISCOVERABLE + 1;


    private final ResourceLoader rcloader;
    private final GameObjectContainer gameObjectContainer;
    private final NonGameObjectContainer foregroundObjectContainer;
    private final NonGameObjectContainer backgroundObjectContainer;
    private final NonGameObjectContainer touchground;
    private final Vector<Button> buttonObjects;
    private final Connector connector;
    private final float scale;
    private Touch[] touch;
    private char[] debug_frametime;
    private char[] debug_touchpos;
    private char[] debug_fps;
    private char[] debug_resolution;
    private char[] debug_version;
    private char[] debug_memory;
    private char[] debug_sent_packets;
    private char[] debug_received_packets;
    private long debug_ping;
    private char[] debug_players;
    private String debug_player_positions="";
    private int temp[];
    private long debug_runtime;

    private static long servermsec = 0;
    private static long diffServerToClient;
    private static long diffClientToServer;
    private static long clientmsec_end;
    private static long offset;
    private static long mytime = 0;
    private static long mytimeWithOffset=0;
    private static long clientmsec_start = 0;

    private static int timer;
    private static int timer100ms;
    private boolean passed100ms;
    private boolean passed200ms;
    private boolean passed500ms;
    private boolean passed1s;
    private static int numberOfFrames;

    private long last_switch;
    private int fps;
    private int idx = 100;
    private int selectbuffer;
    private byte[][] sync;

    private byte[] data;
    private byte[] offlineInput;


    public App(Connector con) {
        connector = con;
        last_switch = 0;
        fps = 0;

        rcloader = ResourceLoader.getInstance();
        scale = Globals.positionTranslationFactor;

        /* Layers*/
        touchground = new NonGameObjectContainer();
        gameObjectContainer = new GameObjectContainer();
        foregroundObjectContainer = new NonGameObjectContainer();
        backgroundObjectContainer = new NonGameObjectContainer();

        /* Buttons */
        buttonObjects = new Vector<>();
        pipeIn = new ConcurrentLinkedQueue<>();
        pipeOut = new ConcurrentLinkedQueue<>();

        /* Init debug Info*/
        if (Constants.DEBUG_DRAW_DEBUG_INFO)
            debugInitInformation();

        sync = new byte[10][Constants.MAX_PACKET_SIZE];
        offlineInput = new byte[5];
        temp = new int[20];
    }

    public void loadResources() {
        /* Load resources */          // TODO need to add threading to load stuff asynchronously
        rcloader.loadAnimations();
        rcloader.loadSounds();

        /* Touch marker */
        touch = new Touch[Constants.MAX_TOUCH_INSTANCES];
        touch[0] = new Touch(ResourceLoader.getInstance().getAnimationSet(Constants.ANIMSET_CIRCLE));
        touch[1] = new Touch(ResourceLoader.getInstance().getAnimationSet(Constants.ANIMSET_SQUARE));

        touchground.addObject(touch[0], 0);
        touchground.addObject(touch[1], 0);
        gameState = S0_LOADING;
    }

    private void debugInitInformation() {

        int width = Globals.gamePortWidth;
        int height = Globals.gamePortHeight;

        debug_frametime = new char[10];
        Arrays.fill(debug_frametime, ' ');
        debug_frametime[0] = 'd';
        debug_frametime[1] = 't';
        debug_frametime[2] = ':';

        debug_touchpos = new char[20];
        debug_touchpos[0] = 'x';
        debug_touchpos[1] = ':';

        debug_touchpos[6] = ' ';
        debug_touchpos[7] = 'y';
        debug_touchpos[8] = ':';

        debug_fps = new char[20];
        Arrays.fill(debug_fps, ' ');
        debug_fps[0] = 'f';
        debug_fps[1] = 'p';
        debug_fps[2] = 's';
        debug_fps[3] = ':';

        debug_resolution = new char[20];
        Arrays.fill(debug_resolution, ' ');
        debug_resolution[0] = 'r';
        debug_resolution[1] = 'e';
        debug_resolution[2] = 's';
        debug_resolution[3] = ':';

        debug_resolution[4] = (char) (width / 1000 % 10 + 0x30);
        debug_resolution[5] = (char) (width / 100 % 10 + 0x30);
        debug_resolution[6] = (char) (width / 10 % 10 + 0x30);
        debug_resolution[7] = (char) (width % 10 + 0x30);

        debug_resolution[8] = 'x';
        debug_resolution[9] = (char) (height / 1000 % 10 + 0x30);
        debug_resolution[10] = (char) (height / 100 % 10 + 0x30);
        debug_resolution[11] = (char) (height / 10 % 10 + 0x30);
        debug_resolution[12] = (char) (height % 10 + 0x30);

        debug_memory = new char[13];
        Arrays.fill(debug_memory, ' ');
        debug_memory[0] = 'm';
        debug_memory[1] = 'e';
        debug_memory[2] = 'm';
        debug_memory[3] = 'o';
        debug_memory[4] = 'r';
        debug_memory[5] = 'y';
        debug_memory[6] = ':';
        debug_memory[10] = ' ';
        debug_memory[11] = 'M';
        debug_memory[12] = 'B';

        debug_version = Globals.version.toCharArray();

        debug_sent_packets = new char[10];
        Arrays.fill(debug_sent_packets, ' ');
        debug_sent_packets[0] = 't';
        debug_sent_packets[1] = 'x';
        debug_sent_packets[2] = ':';

        debug_received_packets = new char[10];
        Arrays.fill(debug_received_packets, ' ');
        debug_received_packets[0] = 'r';
        debug_received_packets[1] = 'x';
        debug_received_packets[2] = ':';


        debug_players = new char[10];
        Arrays.fill(debug_players, ' ');
        debug_players[0] = 'p';
        debug_players[1] = 'l';
        debug_players[2] = 'a';
        debug_players[3] = 'y';
        debug_players[4] = 'e';
        debug_players[5] = 'r';
        debug_players[6] = 's';
        debug_players[7] = ':';




    }

    /* Update values*/
    private void debugPrint(long dt) {

        if (Constants.DEBUG_DRAW_DEBUG_INFO) {
            if (passed1s) {
                fps = numberOfFrames;
                numberOfFrames = 0;

                // Memory left
                int memoryleft = connector.getAvailableMemory();
                debug_memory[7] = (char) (memoryleft / 100 % 10 + 0x30);
                debug_memory[8] = (char) (memoryleft / 10 % 10 + 0x30);
                debug_memory[9] = (char) (memoryleft % 10 + 0x30);
            } else {
                numberOfFrames++;
            }

            debug_frametime[3] = (char) (dt / 10 % 10 + 0x30);
            debug_frametime[4] = (char) (dt % 10 + 0x30);

            debug_fps[4] = (char) (fps / 10 % 10 + 0x30);
            debug_fps[5] = (char) (fps % 10 + 0x30);


            int last_x = touch[0].last_x;
            int last_y = touch[0].last_y;

            debug_touchpos[2] = (char) (last_x / 1000 % 10 + 0x30);
            debug_touchpos[3] = (char) (last_x / 100 % 10 + 0x30);
            debug_touchpos[4] = (char) (last_x / 10 % 10 + 0x30);
            debug_touchpos[5] = (char) (last_x % 10 + 0x30);

            debug_touchpos[9] = (char) (last_y / 1000 % 10 + 0x30);
            debug_touchpos[10] = (char) (last_y / 100 % 10 + 0x30);
            debug_touchpos[11] = (char) (last_y / 10 % 10 + 0x30);
            debug_touchpos[12] = (char) (last_y % 10 + 0x30);

            int nm = Globals.numberOfPlayers;
            debug_players[8] = (char) (nm / 10 % 10 + 0x30);
            debug_players[9] = (char) (nm % 10 + 0x30);

            debug_sent_packets[3] = (char) (Globals.numberOfSentPackets / 100 % 10 + 0x30);
            debug_sent_packets[4] = (char) (Globals.numberOfSentPackets / 10 % 10 + 0x30);
            debug_sent_packets[5] = (char) (Globals.numberOfSentPackets % 10 + 0x30);

            debug_received_packets[3] = (char) (Globals.numberOfReceivedPackets / 100 % 10 + 0x30);
            debug_received_packets[4] = (char) (Globals.numberOfReceivedPackets / 10 % 10 + 0x30);
            debug_received_packets[5] = (char) (Globals.numberOfReceivedPackets % 10 + 0x30);

            gameObjectContainer.readPlayerPositions(temp);
            debug_player_positions = " ";
            for(int i=0;i<nm;i++) {
                int j = i*3;
                debug_player_positions = debug_player_positions + " p:" + temp[j] + " X:" + temp[j+1] + " Y:" + temp[j+2];
            }
        }

    }

    /* Render: Draw Layers */
    public void draw(Canvas canvas, Paint black, Paint red) {
        long a=System.nanoTime();
        backgroundObjectContainer.renderAllObjects(canvas, black);
        gameObjectContainer.renderAllObjects(canvas, black);
        foregroundObjectContainer.renderAllObjects(canvas, black);
        touchground.renderAllObjects(canvas, black);

        /* Draw black borders */
        if (Globals.gamePortYOffset > 0) {
            canvas.drawRect(0, 0, Globals.gamePortWidth, Globals.gamePortYOffset, black);
            canvas.drawRect(0, (Globals.gamePortYOffset + Globals.gamePortHeight), Globals.gamePortWidth, Globals.measuredHeight, black);

        } else if (Globals.gamePortXOffset > 0) {
            canvas.drawRect(0, 0, Globals.gamePortXOffset, Globals.gamePortHeight, black);
            canvas.drawRect(Globals.gamePortWidth + Globals.gamePortXOffset, 0, Globals.measuredWidth, Globals.gamePortHeight, black);
        }

        /* Draw status */
        if (Constants.DEBUG_DRAW_DEBUG_INFO) {
            int x = (int) (10 * scale);
            canvas.drawText(debug_version, 0, debug_version.length, x, 50 * scale, red);
            canvas.drawText(debug_frametime, 0, debug_frametime.length, x, 100 * scale, red);
            canvas.drawText(debug_fps, 0, debug_fps.length, x, 150 * scale, red);
            canvas.drawText(debug_resolution, 0, debug_resolution.length, x, 200 * scale, red);
            canvas.drawText(debug_touchpos, 0, debug_touchpos.length, x, 250 * scale, red);
            canvas.drawText("ping:"+debug_ping,  x, 300 * scale, red);
            canvas.drawText(debug_memory, 0, debug_memory.length, x, 350 * scale, red);
            canvas.drawText(debug_sent_packets, 0, debug_sent_packets.length, x, 400 * scale, red);
            canvas.drawText(debug_received_packets, 0, debug_received_packets.length, x + 75, 400 * scale, red);
            canvas.drawText("clock:"+servermsec , x, 450 * scale, red);
            canvas.drawText("State:" + gameState, x, 550 * scale, red);
            canvas.drawText(debug_runtime + " us", x, 600 * scale, red);
            canvas.drawText(debug_players, 0, debug_players.length, x, 650 * scale, red);
            canvas.drawText((System.nanoTime()-a)/1000+" us",  x, 700 * scale, red);
            canvas.drawText(debug_player_positions,x,750*scale,red);
            canvas.drawText("diffC:"+ diffServerToClient, x, 800 * scale, red);
            canvas.drawText("diffS:"+ diffClientToServer, x, 850 * scale, red);
            canvas.drawText("offst:"+offset, x, 900 * scale, red);
            canvas.drawText("time :"+mytime, x, 950 * scale, red);
            canvas.drawText("time+:"+mytimeWithOffset, x, 1000 * scale, red);
        }
    }

    public void passInput(int id, int x, int y, boolean touched, boolean moved) {
        touch[id].recalculate(touched, x, y, moved);
    }

    /* Calculate Circle/Square Position and update visibility */
    private void updateTouchAnimations() {
        touch[0].updateTouch();
        touch[1].updateTouch();
    }

    /* Calculate player movement */
    private void calculateOfflineInput(long dt) {
        Touch circle = touch[0];
        Touch square = touch[1];

        byte mv = offlineInput[0];
        byte ac = offlineInput[1];

        /* Player 1 Movement */
        if (circle.isTouched()) {
            int threshold = Constants.MOVEMENT_THRESHOLD;

            // TODO need to improve this, because delays aren't cool!! Or could use this as ping
            if ((last_switch > Constants.INPUT_LAG)) {
                int dirx = circle.dx;
                int diry = circle.dy;
                if (Math.abs(dirx) > Math.abs(diry)) {
                    diry = 0;               // Y
                    if (dirx > threshold) {
                        offlineInput[0] = Constants.WALK_RIGHT;
                        dirx = 1;
                    } else if (dirx < -threshold) {
                        offlineInput[0] = Constants.WALK_LEFT;
                        dirx = -1;
                    }
                } else {

                    dirx = 0;               // X
                    if (diry > threshold) {
                        offlineInput[0] = Constants.WALK_DOWN;
                        diry = 1;
                    } else if (diry < -threshold) {
                        offlineInput[0] = Constants.WALK_UP;
                        diry = -1;
                    }
                }

                circle.dx = dirx;
                circle.dy = diry;
                last_switch = 1;
            }
            last_switch += (dt);
        } else {
            circle.dx = 0;
            circle.dy = 0;
            circle.last_x = 0;
            circle.last_y = 0;
            offlineInput[0] = Constants.STAND_DOWN;
            last_switch = 1;
        }

        /* Player 1 Action */
        if (square.isReleased()) {
            offlineInput[1] = Constants.PLACE_BOMB;
        } else {
            offlineInput[1] = Constants.NO_ACTION;
        }

        offlineInput[2] = (byte) (offlineInput[0] ^ mv | (offlineInput[1] ^ ac));
    }
    /////////////////////
    // All Game States //
    /////////////////////
    public void updateLogic(long dt) {

        mytime=SystemClock.elapsedRealtime();
        mytimeWithOffset=mytime + offset;
        timer += dt;
        passed100ms = false;
        passed200ms = false;
        passed500ms = false;
        passed1s = false;

        // Time
        if (timer > 100) {
            timer %= 100;
            timer100ms++;
            passed100ms = true;

            switch (timer100ms) {
                case 1:
                    passed100ms = true;
                    break;
                case 2:
                    passed100ms = true;
                    passed200ms = true;
                    break;
                case 4:
                    passed100ms = true;
                    passed200ms = true;
                    break;
                case 5:
                    passed100ms = true;
                    passed500ms = true;
                    break;
                case 6:
                    passed100ms = true;
                    passed200ms = true;
                    break;
                case 8:
                    passed100ms = true;
                    passed200ms = true;
                    break;
                case 10:
                    passed100ms = true;
                    passed500ms = true;
                    passed1s = true;
                    timer100ms = 0;
                    break;

            }
        }

        debugPrint(dt);
        updateTouchAnimations();
        foregroundObjectContainer.updateAnimations(dt);
        backgroundObjectContainer.updateAnimations(dt);

        long a = SystemClock.elapsedRealtimeNanos();

        switch (gameState) {
            // First scene
            case S0_LOADING:
                // Create selection screen
                backgroundObjectContainer.addObject(new Background(0, 0, rcloader.getAnimationSet(Constants.ANIMSET_BACKGROUND)), 0);
                buttonObjects.add(new Button((700), (250), Constants.ANIMSET_BUTTONS_CLIENT));
                buttonObjects.add(new Button((700), (500), Constants.ANIMSET_BUTTONS_SERVER));
                buttonObjects.add(new Button((700), (750), Constants.ANIMSET_BUTTONS_NORMAL));

                // Add Buttons to foreground
                for (Button button : buttonObjects) {
                    foregroundObjectContainer.addObject(button, 0);
                }
                gameState = S0_IS_BLUETOOTH_ENABLED;
                break;
            // Wait for bluetooth
            case S0_IS_BLUETOOTH_ENABLED:
                if (connector.isBluetoothEnabled()) {
                    gameState = S0_SELECTION_SCREEN;//S0_CLIENT_DISCOVER;//S0_SERVER_DISCOVERABLE;//S0_SELECTION_SCREEN;
                }
                break;
            // Select server
            case S0_SELECTION_SCREEN:
                Touch touch = this.touch[0];
                final int s = buttonObjects.size();
                for (int i = 0; i < s; i++) {
                    Button button = buttonObjects.get(i);
                    if (button.isPressed(touch.last_x, touch.last_y)) {
                        if (touch.isReleased()) {
                            if (button.buttonid == Constants.ANIMSET_BUTTONS_SERVER) {
                                gameState = S0_SERVER_DISCOVERABLE;
                            } else if (button.buttonid == Constants.ANIMSET_BUTTONS_CLIENT) {
                                gameState = S0_CLIENT_DISCOVER;
                            } else if (button.buttonid == Constants.ANIMSET_BUTTONS_NORMAL) {
                                gameState = S0_OFFLINE_START;
                            }
                            break;
                        }
                    }
                }
                break;

            //////////////////////////////////////////////////
            ////////////////////   CLIENT  ///////////////////
            //////////////////////////////////////////////////
            // Look for devices
            case S0_CLIENT_DISCOVER:
                connector.discoverDevices();
                foregroundObjectContainer.wipe();
                buttonObjects.clear();
                gameState = S0_CLIENT_SELECT_SERVER;
                break;
            // Select server from list
            case S0_CLIENT_SELECT_SERVER:
                if (connector.hasDevices()) {
                    // DEBUG
                    String t = connector.discoveredDevice();
                    if (t.contains(Constants.SERVER_NAME)) {
                        if (connector.connectPipeToClient(t, this)) {
                            gameState = S0_CLIENT_SYNC;
                        }
                    }


                    /*
                    /// real code
                    String text = connector.discoveredDevice();

                    Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                    Bitmap bitmap_clicked = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(bitmap);
                    Canvas c2 = new Canvas(bitmap_clicked);
                    Paint paint = new Paint();

                    paint.setTextSize(20f);
                    paint.setColor(Color.BLACK);

                    c.drawColor(Color.CYAN);
                    c.drawText(text, 1, 40, paint);
                    c2.drawColor(Color.RED);

                    int seq[] = {0};

                    // Animation
                    AnimationSet animset = new AnimationSet(2);
                    animset.animations[0] = new Animation(bitmap, seq, 1, 0, (int) (20 * Globals.positionTranslationFactor), (int) (20 * Globals.positionTranslationFactor), false);
                    animset.animations[1] = new Animation(bitmap_clicked, seq, 1, 0, (int) (20 * Globals.positionTranslationFactor), (int) (20 * Globals.positionTranslationFactor), false);

                    rcloader.animations.put(idx, animset);

                    Button btn1 = new Button((int) (700 * scale), (int) ((buttonObjects.size()) * 300 * scale), idx, text);
                    buttonObjects.add(btn1);
                    foregroundObjectContainer.addObject(btn1, 0);
                    idx++;
                }


                touch = this.touch[0];
                int sz = buttonObjects.size();
                for (int i = 0; i < sz; i++) {
                    Button button = buttonObjects.get(i);
                    if (button.isPressed(touch.last_x, touch.last_y)) {
                        if (touch.isReleased()) {
                            if(connector.connectPipeToClient(button.getName(), this)) { // connect to selected device
                                gameState = S0_CLIENT_SYNC;
                        }
                            break;
                        }
                    }*/
                }
                break;
            // Wait for ID
            case S0_CLIENT_SYNC:
                if (Globals.clientID != -1)
                    gameState = S0_CLIENT_CREATE_GAME;
                break;
            // Connected to select server
            case S0_CLIENT_CREATE_GAME:
                connector.stopDiscovery();
                buttonObjects.clear();
                foregroundObjectContainer.wipe();
                backgroundObjectContainer.wipe();
                backgroundObjectContainer.addObject(new Background(0, 0, rcloader.getAnimationSet(Constants.ANIMSET_BACKGROUND_FIELD)), 1);
                gameObjectContainer.createLevel(Constants.LEVEL1_MAP);

                // Add players
                // TODO add other stages between
                for (int i = 0; i < Globals.numberOfPlayers; i++) {
                    gameObjectContainer.createPlayer(i);
                }
                gameState = S0_CLIENT_GAME_RUNNING;
                break;
            // Game from client perspective
            case S0_CLIENT_GAME_RUNNING:
                // Parse server packets
                while(!pipeIn.isEmpty()) {
                    data = pipeIn.remove();
                    switch (data[1]) {
                        case Constants.PROTOCOL_TIME_SYNC:
                            servermsec= ((data[9] & 0xFFL) << 56) |
                                    ((data[8] & 0xFFL) << 48) |
                                    ((data[7] & 0xFFL) << 40) |
                                    ((data[6] & 0xFFL) << 32) |
                                    ((data[5] & 0xFFL) << 24) |
                                    ((data[4] & 0xFFL) << 16) |
                                    ((data[3] & 0xFFL) << 8) |
                                    ((data[2] & 0xFFL) << 0) ;
                            long t =((data[17] & 0xFFL) << 56) |
                                    ((data[16] & 0xFFL) << 48) |
                                    ((data[15] & 0xFFL) << 40) |
                                    ((data[14] & 0xFFL) << 32) |
                                    ((data[13] & 0xFFL) << 24) |
                                    ((data[12] & 0xFFL) << 16) |
                                    ((data[11] & 0xFFL) << 8) |
                                    ((data[10] & 0xFFL) << 0) ;
                            clientmsec_end = SystemClock.elapsedRealtime();
                            diffClientToServer = servermsec - clientmsec_start;
                            diffServerToClient = t - clientmsec_end;
                            debug_ping = (clientmsec_end-clientmsec_start);
                            offset =  ((diffClientToServer)+(diffServerToClient))/2;    // not going to work with bluetooths assymetric delay
                            Log.e("PING:","ping:;"+debug_ping);
                            Log.e("FFST:","ffst:;"+offset%1000);
                            break;
                        case Constants.PROTOCOL_GAME_STATE:
                            break;
                        case Constants.PROTOCOL_GAME_INPUT:
                            //gameObjectContainer.correctPositions(data,12, clientmsec_start-servermsec);
                            gameObjectContainer.updateInput(data[0], data[10], data[11]);
                            break;
                    }
                }

                calculateOfflineInput(dt);
                gameObjectContainer.updateState(dt);
                gameObjectContainer.updateAnimations(dt);
                gameObjectContainer.sort();


                /*

                if (offlineInput[2] > 0) {
                    sync[selectbuffer][0] = Globals.clientID;
                    sync[selectbuffer][1] = Constants.PROTOCOL_GAME_INPUT;
                    sync[selectbuffer][2] = (byte) (clientmsec_start);
                    sync[selectbuffer][3] = (byte) (clientmsec_start >> 8);
                    sync[selectbuffer][4] = (byte) (clientmsec_start >> 16);
                    sync[selectbuffer][5] = (byte) (clientmsec_start >> 24);
                    sync[selectbuffer][6] = (byte) (clientmsec_start >> 32);
                    sync[selectbuffer][7] = (byte) (clientmsec_start >> 40);
                    sync[selectbuffer][8] = (byte) (clientmsec_start >> 48);
                    sync[selectbuffer][9] = (byte) (clientmsec_start >> 56);
                    sync[selectbuffer][10] = offlineInput[0];
                    sync[selectbuffer][11] = offlineInput[1];
                    pipeOut.add(sync[selectbuffer]);
                    selectbuffer = (selectbuffer + 1) % sync.length;

                }
*/

                if(passed500ms){
                    clientmsec_start = SystemClock.elapsedRealtime();
                    sync[selectbuffer][0] = Globals.clientID;
                    sync[selectbuffer][1] = Constants.PROTOCOL_TIME_SYNC;
                    sync[selectbuffer][2] = (byte) (clientmsec_start);
                    sync[selectbuffer][3] = (byte) (clientmsec_start >> 8);
                    sync[selectbuffer][4] = (byte) (clientmsec_start >> 16);
                    sync[selectbuffer][5] = (byte) (clientmsec_start >> 24);
                    sync[selectbuffer][6] = (byte) (clientmsec_start >> 32);
                    sync[selectbuffer][7] = (byte) (clientmsec_start >> 40);
                    sync[selectbuffer][8] = (byte) (clientmsec_start >> 48);
                    sync[selectbuffer][9] = (byte) (clientmsec_start >> 56);
                    pipeOut.add(sync[selectbuffer]);
                    selectbuffer = (selectbuffer+1)%sync.length;
                }
                break;
            //////////////////////////////////////////////////
            ////////////////////   SERVER  ///////////////////
            //////////////////////////////////////////////////
            // Is Bluetooth enabled
            case S0_SERVER_DISCOVERABLE:
                buttonObjects.clear();
                foregroundObjectContainer.wipe();
                connector.makeBluetoothDiscoverable();
                gameState = S0_SERVER_WAIT_FOR_DISCOVERABLE;
                break;
            // Wait for device to become discoverable
            case S0_SERVER_WAIT_FOR_DISCOVERABLE:
                if (connector.connectPipeToServer(this)) {
                    gameState = S0_SERVER_WAIT_FOR_CLIENTS;
                }
                break;
            // Show clients and wait for start game
            case S0_SERVER_WAIT_FOR_CLIENTS:
                if (Globals.numberOfPlayers == (Constants.MAX_NUMBER_OF_PLAYERS)) {
                    gameState = S0_SERVER_CREATE_GAME;
                }
                break;
            // Create Server game
            case S0_SERVER_CREATE_GAME:
                buttonObjects.clear();
                foregroundObjectContainer.wipe();
                backgroundObjectContainer.wipe();
                backgroundObjectContainer.addObject(new Background(0, 0, rcloader.getAnimationSet(Constants.ANIMSET_BACKGROUND_FIELD)), 1);
                gameObjectContainer.createLevel(Constants.LEVEL1_MAP);

                // Add players
                for (int i = 0; i < Globals.numberOfPlayers; i++) {
                    gameObjectContainer.createPlayer(i);
                }

                gameState = S0_SERVER_GAME_RUNNING;
                break;
            // Server game
            case S0_SERVER_GAME_RUNNING:
                while (!pipeIn.isEmpty()) {
                    data = pipeIn.remove();
                    switch (data[1]) {
                        case Constants.PROTOCOL_TIME_SYNC:

                            clientmsec_start = ((data[9] & 0xFFL) << 56) |
                                    ((data[8] & 0xFFL) << 48) |
                                    ((data[7] & 0xFFL) << 40) |
                                    ((data[6] & 0xFFL) << 32) |
                                    ((data[5] & 0xFFL) << 24) |
                                    ((data[4] & 0xFFL) << 16) |
                                    ((data[3] & 0xFFL) << 8) |
                                    ((data[2] & 0xFFL) << 0);

                            servermsec = SystemClock.elapsedRealtime();
                            data[2] = (byte) (servermsec);
                            data[3] = (byte) (servermsec >> 8);
                            data[4] = (byte) (servermsec >> 16);
                            data[5] = (byte) (servermsec >> 24);
                            data[6] = (byte) (servermsec >> 32);
                            data[7] = (byte) (servermsec >> 40);
                            data[8] = (byte) (servermsec >> 48);
                            data[9] = (byte) (servermsec >> 56);
                            pipeOut.add(data);
                            break;
                        case Constants.PROTOCOL_GAME_STATE:
                            break;
                        case Constants.PROTOCOL_GAME_INPUT:
                            gameObjectContainer.updateInput(data[0], data[10], data[11]);   // TODO to check that not more than 1 Input per Player gets consumed
                            gameObjectContainer.fillPositions(data, 12);
                            pipeOut.add(data);
                            break;
                    }
                }

                calculateOfflineInput(dt);
                gameObjectContainer.updateInput(0, offlineInput[0], offlineInput[1]);
                gameObjectContainer.updateState(dt);
                gameObjectContainer.updateAnimations(dt);
                gameObjectContainer.sort();


/*
                if (offlineInput[2] > 0) {
                    sync[selectbuffer][0] = 0;
                    sync[selectbuffer][1] = Constants.PROTOCOL_GAME_INPUT;
                    sync[selectbuffer][2] = (byte) (servermsec);
                    sync[selectbuffer][3] = (byte) (servermsec >> 8);
                    sync[selectbuffer][4] = (byte) (servermsec >> 16);
                    sync[selectbuffer][5] = (byte) (servermsec >> 24);
                    sync[selectbuffer][6] = (byte) (servermsec >> 32);
                    sync[selectbuffer][7] = (byte) (servermsec >> 40);
                    sync[selectbuffer][8] = (byte) (servermsec >> 48);
                    sync[selectbuffer][9] = (byte) (servermsec >> 56);
                    sync[selectbuffer][10] = offlineInput[0];
                    sync[selectbuffer][11] = offlineInput[1];

                    // get state of players
                    gameObjectContainer.fillPositions(sync[selectbuffer], 12);
                    pipeOut.add(sync[selectbuffer]);
                    selectbuffer = (selectbuffer + 1) % sync.length;
                }   */
                break;
            //////////////////////////////////////////////////
            ///////////////////   OFFLINE  ///////////////////
            //////////////////////////////////////////////////
            // No Bluetooth routines
            case S0_OFFLINE_START:
                buttonObjects.clear();
                foregroundObjectContainer.wipe();
                backgroundObjectContainer.wipe();
                backgroundObjectContainer.addObject(new Background(0, 0, rcloader.getAnimationSet(Constants.ANIMSET_BACKGROUND_FIELD)), 1);
                gameObjectContainer.createLevel(Constants.LEVEL1_MAP);
                gameObjectContainer.createPlayer(0);
                gameState = S0_OFFLINE_GAME_RUNNING;
                break;
            // Running offline game with local input
            case S0_OFFLINE_GAME_RUNNING:
                calculateOfflineInput(dt);
                gameObjectContainer.updateInput(0, offlineInput[0], offlineInput[1]);
                gameObjectContainer.updateState(dt);
                gameObjectContainer.updateAnimations(dt);
                gameObjectContainer.sort();
                break;
            default:
                break;
        }
        debug_runtime = (SystemClock.elapsedRealtimeNanos() - a)/1000;
    }


}
