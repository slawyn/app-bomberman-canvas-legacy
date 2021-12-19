package main.gameobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import main.Constants;

public class GameObjectContainer {
    private static int fieldMap[][];
    private static SparseArray<Player> players;
    private static SparseArray<Bomb> bombs;
    private static SparseArray<Crate> crates;
    private static SparseArray<Explosion> explosions;
    private static SparseArray<Block> blocks;
    private static ArrayList<GameObject> renderObjects;
    private byte [][] playerInputs; // movement: 0, action: 1, dirx: 2, diry: 3
    private static int objID;

    public GameObjectContainer() {
        players = new SparseArray<>();
        bombs = new SparseArray<>();
        crates = new SparseArray<>();
        blocks = new SparseArray<>();
        explosions = new SparseArray<>();
        renderObjects = new ArrayList<>();
        fieldMap = new int[Constants.NUMBER_OF_X_CELLS][Constants.NUMBER_OF_Y_CELLS];
        playerInputs = new byte[4][3];
        objID = 200;
    }

    public void updateInput(int i, byte movement, byte action){
        playerInputs[i][0] = movement;
        playerInputs[i][1] = action;
    }

    public void addPlayer(int cellposx, int cellposy, int name) {
        Player player = new Player(cellposx, cellposy, name);
        players.put(name, player);
        renderObjects.add(player);
    }

    /* Add Explosion */
    public void addExplosion(int cellposx, int cellposy, int strength, int owner) {

        int left = 0, up = 0, right = 0, down = 0;
        /* Calculate the spread*/
        int j, a;
        int le = fieldMap[0].length;
        int le2 = fieldMap.length;

        int obj = ++objID;
        players.get(owner).paramNumberOfBombs++;
        fieldMap[cellposx][cellposy] = (obj | Constants.EXPLOSION);
        Vector<Integer> triggeredBombs = new Vector<>();

        boolean dir_right = true;
        boolean dir_left = true;
        boolean dir_up = true;
        boolean dir_down = true;

        for (int i = 1; i <= strength; i++) {
            j = (i - 1);

            /* Explosion spreading down */
            if (dir_down && (cellposy + i) < le2 && (j == down)) {
                a = fieldMap[cellposx][cellposy + i] & Constants.EXTRACT_INFO;
                /* Continue with current Explosion */
                if (a == 0) {
                    fieldMap[cellposx][cellposy + i] = (obj | Constants.EXPLOSION);
                    down++;
                } else if (a == Constants.BOMB) {
                    triggeredBombs.add(fieldMap[cellposx][cellposy + i] & Constants.EXTRACT_ID);
                    dir_down = false;
                    down++;
                } else if (a == Constants.CRATE) {
                    crates.get(fieldMap[cellposx][cellposy + i] & Constants.EXTRACT_ID).state = Constants.DEAD;
                    dir_down = false;
                    down++;

                }
            }

            /* Explosions spreading right */
            if (dir_right && (cellposx + i) < le && (j == right)) {
                a = fieldMap[cellposx + i][cellposy] & Constants.EXTRACT_INFO;

                /* Continue with current Explosion */
                if (a == 0) {
                    fieldMap[cellposx + i][cellposy] = (obj | Constants.EXPLOSION);
                    right++;
                } else if (a == Constants.BOMB) {
                    triggeredBombs.add(fieldMap[cellposx + i][cellposy] & Constants.EXTRACT_ID);
                    dir_right = false;
                    right++;
                } else if (a == Constants.CRATE) {
                    crates.get(fieldMap[cellposx + i][cellposy] & Constants.EXTRACT_ID).state = Constants.DEAD;
                    dir_right = false;
                    right++;
                }

            }
            /* Explosion spreading left */
            if (dir_left && (cellposx - i) >= 0 && (j == left)) {
                a = fieldMap[cellposx - i][cellposy] & Constants.EXTRACT_INFO;

                /* Continue with current Explosion */
                if (a == 0) {
                    fieldMap[cellposx - i][cellposy] = (obj | Constants.EXPLOSION);
                    left++;
                } else if (a == Constants.BOMB) {
                    triggeredBombs.add(fieldMap[cellposx - i][cellposy] & Constants.EXTRACT_ID);
                    dir_left = false;
                    left++;
                } else if (a == Constants.CRATE) {
                    crates.get(fieldMap[cellposx - i][cellposy] & Constants.EXTRACT_ID).state = Constants.DEAD;
                    dir_left = false;
                    left++;
                }

            }

            /* Explosion spreading up */
            if (dir_up && (cellposy - i) >= 0 && (j == up)) {
                a = fieldMap[cellposx][cellposy - i] & Constants.EXTRACT_INFO;

                /* Continue with current Explosion */
                if (a == 0) {
                    fieldMap[cellposx][cellposy - i] = (obj | Constants.EXPLOSION);
                    up++;
                } else if (a == Constants.BOMB) {
                    triggeredBombs.add(fieldMap[cellposx][cellposy - i] & Constants.EXTRACT_ID);
                    up++;
                    dir_up = false;
                } else if (a == Constants.CRATE) {
                    crates.get(fieldMap[cellposx][cellposy - i] & Constants.EXTRACT_ID).state = Constants.DEAD;
                    up++;
                    dir_up = false;
                }
            }
        }

        /* Explode all bombs after the current explosion is done
         Following this idea, the first bomb to explode fills the area*/
        for (Integer key : triggeredBombs) {
            Bomb bomb = bombs.get(key);
            if (!bomb.exploded) {
                bomb.exploded = true;
                addExplosion(bomb.cellposx, bomb.cellposy, bomb.strength, bomb.owner);
            }
        }

        /* Create explosion */
        Explosion explosion = new Explosion(cellposx, cellposy, strength, right, left, up, down, owner);
        explosions.put(obj, explosion);
        renderObjects.add(explosion);
    }

    /* Add normal block */
    public void addBlock(int cellposx, int cellposy) {
        objID++;
        fieldMap[cellposx][cellposy] = (objID | Constants.BLOCK);
        Block block = new Block(cellposx, cellposy);
        blocks.put(objID, block);
        renderObjects.add(block);
    }

    /* Add a player bomb*/
    public void addBomb(int cellposx, int cellposy, long bombtimer, int bombowner, int strength) {
        if (fieldMap[cellposx][cellposy] != 0)
            return;
        objID++;
        fieldMap[cellposx][cellposy] = (objID | Constants.BOMB);
        Bomb bomb = new Bomb(cellposx, cellposy, bombtimer, bombowner, strength);
        bombs.put(objID, bomb);
        renderObjects.add(bomb);
        players.get(bombowner).paramNumberOfBombs--;
    }

    public void addCrate(int cellposx, int cellposy) {
        objID++;
        fieldMap[cellposx][cellposy] = (objID | Constants.CRATE);
        Crate crate = new Crate(cellposx, cellposy);
        crates.put(objID, crate);
        renderObjects.add(crate);
    }


    public SparseArray<Player> getPlayers() {
        return players;
    }


    public void readPlayerPositions(int arr[]){

        for(int i =0; i<players.size();i++) {
            int j = i*3;
            Player p = players.get(i);
            arr[j] = p.paramName;
            arr[j+ 1] = p.posX;
            arr[j + 2] = p.posY;
        }
    }


    public void correctPositions(byte buf [], int offset, long msec){

        for(int i =0; i<buf[offset];i++){
            int j = i*5;
            Player p = players.get(buf[offset+j+1]);
            p.posX =   buf[offset+j+2]&0x00FF |((buf[offset+j+3]&0x00FF)<<8);  //p.posX + (int)(msec * p.directionX*msec);//
            p.posY=    buf[offset+j+4]&0x00FF |((buf[offset+j+5]&0x00FF)<<8);  //p.posX + (int)(msec * p.directionX*msec);//
            Log.e("Positions:", p.posX + " "+p.posY);
        }

    }

    public void fillPositions(byte buf [], int offset){

        buf[offset] = (byte)players.size();

        for(int i =0; i<players.size();i++){
            int  j = i*5;
            Player p = players.get(i);
            buf[offset+j+1] = (byte)p.paramName;
            buf[offset+j+2] = (byte)(p.posX&0xFF);
            buf[offset+j+3] = (byte)(p.posX>>8);
            buf[offset+j+4] = (byte)(p.posY&0xFF);
            buf[offset+j+5] = (byte)(p.posY>>8);
        }

    }

    public void renderAllObjects(Canvas canvas, Paint p) {
        int gs = renderObjects.size();
        for (int i = 0; i < gs; i++) {
            renderObjects.get(i).render(canvas, p);
        }
    }

    public void sort() {
        Collections.sort(renderObjects);
    }

    public void updateAnimations(long dt) {
        int gs = renderObjects.size();
        /* Update Animations TODO, is it better this way? */
        for (int i = 0; i < gs; i++) {
            renderObjects.get(i).updateAnimations(dt);
        }
    }
    public byte[] getInput(int i){
        return playerInputs[i];
    }

    public void updateState(long dt) {

        /* Update Explosions */
        for (int i = 0; i < explosions.size(); i++) {
            int key = explosions.keyAt(i);
            Explosion explosion = explosions.get(key);
            if (explosion.updateState(dt)) {
                renderObjects.remove(explosion);
                explosions.remove(key);
                fieldMap[explosion.cellposx][explosion.cellposy] = 0;

                int p = explosion.strength;
                int r = explosion.right;
                int l = explosion.left;
                int t = explosion.top;
                int b = explosion.bottom;

                for (int o = 1; o <= p; o++) {
                    if (o <= r) {
                        fieldMap[explosion.cellposx + o][explosion.cellposy] = 0;
                    }
                    if (o <= l) {
                        fieldMap[explosion.cellposx - o][explosion.cellposy] = 0;
                    }
                    if (o <= t) {
                        fieldMap[explosion.cellposx][explosion.cellposy - o] = 0;
                    }
                    if (o <= b) {
                        fieldMap[explosion.cellposx][explosion.cellposy + o] = 0;
                    }
                }
            }
        }

        /* Update players */
        for (int i = 0; i < players.size(); i++) {
            int key = players.keyAt(i);
            Player player = players.get(key);
            player.updateState(dt, this);

            //TODO check when we need screenbounds and when field bounds
            player.stayInsideField();   //player.stayInsideScreen();
        }

        // Run updated players against the environment
        for (int i = 0; i < players.size(); i++) {
            int key = players.keyAt(i);
            Player player = players.get(key);

            int x = player.getCellFromCenteredX();
            int y = player.getCellFromCenteredY();

            /**/
            boolean loop = true;

            /* Parse 9 nearest cells against collision */
            for (int b = x - 1; b <= (x + 1) && loop; b++) {
                if (b < 0 || b >= Constants.NUMBER_OF_X_CELLS)
                    continue;

                for (int c = y - 1; c <= (y + 1) && loop; c++) {
                    if (c < 0 || c >= Constants.NUMBER_OF_Y_CELLS)
                        continue;

                    int fiedlval = fieldMap[b][c];
                    switch (fiedlval & Constants.EXTRACT_INFO) {
                        // Collision with a an explosion
                        case Constants.EXPLOSION:
                            Explosion explosion = explosions.get(fiedlval & Constants.EXTRACT_ID);
                            if (player.collisionCheck11(explosion) || player.collisionCheck12(explosion)) {

                                // TODO add Schield Check here
                                player.state = Constants.DYING0;
                                loop = false;
                            }
                            break;
                        // Collision with a crate
                        case Constants.CRATE:
                            Crate crate = crates.get(fiedlval & Constants.EXTRACT_ID);
                            if (player.collisionCheck11(crate)) {
                                player.correctPosition(crate);
                                loop = false;
                            }
                            break;

                        // Collision with a block
                        case Constants.BLOCK:
                            Block block = blocks.get(fiedlval & Constants.EXTRACT_ID);
                            if (player.collisionCheck11(block)) {
                                player.correctPosition(block);
                                loop = false;
                            }
                            break;
                    }
                }
            }

            /* If player is standing on a bomb*/
            if ((fieldMap[x][y] & Constants.EXTRACT_INFO) == Constants.BOMB) {
                Bomb bomb = bombs.get(fieldMap[x][y] & Constants.EXTRACT_ID);
                if (player.collisionCheck11(bomb)) {
                    if (bomb.nonwalkable) {
                        player.correctPosition(bomb);
                    } else
                        bomb.peoplewalking = true;
                }
            }
        }

        // Remove Exploded Bombs
        int bs = bombs.size();
        for (int i = 0; i < bs; i++) {
            int key = bombs.keyAt(i);
            Bomb bomb = bombs.get(key);
            if (bomb.exploded) {
                bombs.remove(key);
                renderObjects.remove(bomb);
            } else
                bomb.updateState(dt, this);
        }

        // Remove Crates
        for (int i = 0; i < crates.size(); i++) {
            int key = crates.keyAt(i);
            Crate crate = crates.get(key);
            if (crate.state == Constants.DEAD) {
                fieldMap[crate.cellposx][crate.cellposy] = 0;
                crates.remove(key);
                renderObjects.remove(crate);
            }
        }
    }

    public void createPlayer(int id){
        /* Create Server Player */
        int x = Constants.STARTING_CELL_POSITIONS[id][0];
        int y = Constants.STARTING_CELL_POSITIONS[id][1];
        addPlayer(x, y, id);
    }

    public void createLevel(int selectlevel ) {
        int level[][] = Constants.LEVELS[selectlevel];
        for (int i = 0; i < Constants.LEVELS[selectlevel][0].length; i++) {
            for (int j = 0; j < Constants.LEVELS[selectlevel].length; j++) {
                if (level[i][j] == 1) {
                    addBlock(j, i);
                } else if (level[i][j] == 2) {
                    addCrate(j, i);
                }
            }
        }
    }

//    /* Parsing server information */
//    public void parseServerState(){
//
//        /*
//        /* no states to parse */
//        if(!Client.isRunning() || Client.receiveStateQueueEmpty())
//            return;
//
//        NetworkData state = (Client.getNextReceivedServerState());
//        int numOfObjects =  state.infoBuffer[0];
//        int numOfPlayers =  state.infoBuffer[1];
//        int numOfBlocks =  state.infoBuffer[2];
//        int numOfBombs =  state.infoBuffer[3];
//        int numOfExplosions =  state.infoBuffer[4];
//        myclientid = state.clientid;
//
//        int i =0;
//        int c;
//        if(numOfObjects>renderObjects.size()){
//            if(numOfPlayers>=players.size()){
//
//                c = players.size();
//                for(int j=0;j<(numOfPlayers-c);j++){
//                    this.addPlayer(state.infoBuffer[10+i],state.infoBuffer[10+i+1],state.infoBuffer[10+i+2]);
//
//                    /*
//                    Player p = getPlayer(state.stateBuffer[10+i+2]);
//                    p.input.currentAction = state.stateBuffer[10+i+3];
//                    p.input.currentMovement = state.stateBuffer[10+i+4];
//                    p.input.dirx = state.stateBuffer[10+i+5];
//                    p.input.diry = state.stateBuffer[10+i+6];
//                    */
//                    i=i+7;
//                }
//            }
//        }
//        else {
//
//            /* Number of objects is less*/
//            i=0;
//            for(int a=0;a<numOfPlayers;a++){
//                int b = state.infoBuffer[10 + i + 2];
//                Player p = getPlayer(b);
//                if(p!=null && b != myclientid){
//                    /*
//                   p.input.currentAction = state.stateBuffer[10+i+3];
//                   p.input.currentMovement = state.stateBuffer[10+i+4];
//                   p.input.dirx = state.stateBuffer[10+i+5];
//                   p.input.diry = state.stateBuffer[10+i+6];
//                   */
//               }
//               i=i+7;
//            }
//        }
//        */
//    }
//
//    /* Parse Information from clients*/
//    public void parseClientStates() {
//
//        Vector<Connection> connections = Server.getConnections();
//        if(connections.isEmpty())
//            return;
//
//        for (Connection connection : connections) {
//
//            /* no states to parse */
//            if (!connection.receiveStateQueueEmpty()) {
//                NetworkData state = connection.getNextReceivedState();
//                int id = connection.getClientId();
//
//                if(players.indexOfKey(id)>=0){
//                    Player p = players.get(id);
//                    int i = 0;//id*7;
//                    // TODO parse positons ??
//                    /*
//                    p.input.currentAction = state.stateBuffer[10 + i + 3];
//                    p.input.currentMovement = state.stateBuffer[10 + i + 4];
//                    p.input.dirx = state.stateBuffer[10 + i + 5];
//                    p.input.diry = state.stateBuffer[10 + i + 6];*/
//                    break;
//                }
//                else  { // doesn't contain then we create a new player, because the connection has just been initiated
//                    int x =Globals.STARTING_CELL_POSITIONS[id][0];
//                    int y =Globals.STARTING_CELL_POSITIONS[id][1];
//                    new Player(x, y,id);
//                }
//            }
//        }
//    }
//    public void captureClientState(long timeStamp){
//
//        if(Client.isRunning() ||!localStateChanged)
//            return;
//
//        /* Capture only local player input*/
//        Player player = players.get(myclientid);
//        //int i = myclientid*7;
//        int i = 0;
//
//        /*
//        GameState state = new GameState();
//        state.stateBuffer[10+i] = (int)player.posX;
//        state.stateBuffer[10+i+1] = (int)player.posY;
//        state.stateBuffer[10+i+2] = (int)player.paramName;
//        state.stateBuffer[10+i+3] = (int)player.input.currentAction;
//        state.stateBuffer[10+i+4] = (int)player.input.currentMovement;
//        state.stateBuffer[10+i+5] = player.input.dirx;
//
//        Client.getInstance().enqueueState(state);
//        localStateChanged = false;        state.stateBuffer[10+i+6] = player.input.diry;
//*/
////    }
//    public void captureServerState(long timeStamp){
//
//        if(Server.hasNoConnections()||!localStateChanged){
//            return;
//        }
//
//        int i=0;
//        NetworkData state = new NetworkData();
//
//        /* Create new state */
//        state.infoBuffer[0] = //blocks.size()+players.size();
//        state.infoBuffer[1] = players.size();
//        state.infoBuffer[2] = //blocks.size();
//        state.infoBuffer[3] = 0;
//        state.infoBuffer[4] = 0;//explosions.size();
//
//        state.clientid = myclientid;
//
//        /* Update players */
//        for(int j = 0; j < players.size(); j++) {
//            int key = players.keyAt(j);
//            Player player= players.get(key);
//
//            /*
//            state.stateBuffer[10+i] = (int)player.posX;
//            state.stateBuffer[10+i+1] = (int)player.posY;
//            state.stateBuffer[10+i+2] = (int)player.paramName;
//            state.stateBuffer[10+i+3] = (int)player.input.currentAction;
//            state.stateBuffer[10+i+4] = (int)player.input.currentMovement;
//            state.stateBuffer[10+i+5] = player.input.dirx;
//            state.stateBuffer[10+i+6] = player.input.diry;
//            i=i+7;*/
//        }
//
//        i=0;
//        /*
//        for(Block block:blocks){
//            state.stateBuffer[50+i] = (int)block.posX;
//            state.stateBuffer[50+i+1] = (int)block.posY;
//            i=i+2;
//        }*/
//
//        Server.enqueueState(state);
//        localStateChanged = false;
//    }
}
