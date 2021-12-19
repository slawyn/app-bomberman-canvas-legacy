package main.gameobjects;

import main.Constants;
import main.buildingblocks.AnimationSet;
import main.ResourceLoader;


public class Player extends GameObject {
    private AnimationSet animationSet;
    public int paramNumberOfBombs;
    public int paramExplosionStrength;
    public int statNumberOfKills;
    public long paramBombtimer;
    private int movementState;
    private long elapsedTime;
    private int actionState;
    public int paramName;
    private float speedx;
    private float speedy;
    public int directionX;
    public int directionY;

    public Player(int cellxpos, int cellypos, int name_) {
        super(cellxpos, cellypos);
        init(name_);
    }

    public void init(int name_) {
        animationSet = ResourceLoader.getInstance().getCopyOfAnimationSet(Constants.ANIMSET_ROBOT);
        speedx = Constants.PLAYER_BASE_SPEED_X;
        speedy = speedx * Constants.CELL_RATIO;
        paramBombtimer = Constants.BOMB_TIMER;
        paramNumberOfBombs = Constants.PLAYER_BOMB_STARTING_AMOUNT;
        paramExplosionStrength = Constants.PLAYER_BOMB_EXPLOSION_STRENGTH;
        paramName = name_;
        statNumberOfKills = 0;
        state = Constants.ALIVE;

        addTexture(0, animationSet.animations[Constants.ANIM_STAND_DOWN]);
        addBoundingBox1((Constants.PLAYER_BOX_OFFSET_X), (Constants.PLAYER_BOX_OFFSET_Y), (Constants.PLAYER_BOX_WIDTH), (Constants.PLAYER_BOX_HEIGHT));
        updateBoundingBox1();
    }

    public void updateState(long dt, GameObjectContainer gameObjectContainer) {

        byte input[] = gameObjectContainer.getInput(paramName);
        switch (state) {
            case Constants.ALIVE:

                /* In here the position has already been corrected by the previous iteration */
                if (actionState != input[1]) {
                    actionState = input[1];
                    if (actionState == Constants.PLACE_BOMB) {

                        /* if there are bombs */
                        if (paramNumberOfBombs > 0) {
                            gameObjectContainer.addBomb(getCellFromCenteredX(), getCellFromCenteredY(), paramBombtimer, paramName, paramExplosionStrength);
                        }
                    }
                }

                /* Change movement and animation*/
                if (movementState != input[0]) {
                    movementState = input[0];
                    directionX = 0;
                    directionY = 0;
                    switch(movementState){
                        case Constants.WALK_DOWN:
                            directionY=1;
                            break;
                        case Constants.WALK_UP:
                            directionY=-1;
                            break;
                        case Constants.WALK_LEFT:
                            directionX=-1;
                            break;
                        case Constants.WALK_RIGHT:
                            directionX=1;
                            break;
                            /* Not needed
                        case Constants.STAND_DOWN:
                        case Constants.STAND_UP:
                        case Constants.STAND_LEFT:
                        case Constants.STAND_RIGHT:
                            directionX = 0;
                            directionY = 0;
                            break;*/

                    }

                    addTexture(0, animationSet.animations[movementState]);
                }

                /* Update position */
                if (directionX != 0)
                    posX += (directionX * dt * speedx);
                else if (directionY != 0)
                    posY += (directionY * dt * speedy);

                /* Update bounding box1 */
                updateBoundingBox1();
                break;
            case Constants.DYING0:
                addTexture(0, animationSet.animations[Constants.ANIM_DYING]);
                state = Constants.DYING1;
                break;
            case Constants.DYING1:
                if (elapsedTime > 2000) {
                    state = Constants.DEAD;
                }
                elapsedTime++;
                break;
            case Constants.DEAD:
                //TODO
                break;
        }
    }

    public void stayInsideField() {
        /* Horizontally */
        if ((box1.right) > (Constants.FIELD_X2))
            posX = (Constants.FIELD_X2 - (box1.offsetX + box1.sizeX));
        else if (box1.left < Constants.FIELD_X1)
            posX = Constants.FIELD_X1 - box1.offsetX;

        /* vertically */
        if (box1.top < Constants.FIELD_Y1)
            posY = Constants.FIELD_Y1 - box1.offsetY;
        else if ((box1.bottom) > (Constants.FIELD_Y2))
            posY = (Constants.FIELD_Y2 - (box1.offsetY + box1.sizeY));
    }

    public void stayInsideScreen() {

        /* Horizontally */
        if ((box1.right) > (Constants.GAME_WIDTH - Constants.BOUNDS))
            posX = (int) (Constants.GAME_WIDTH - (box1.offsetX + box1.sizeX + Constants.BOUNDS));
        else if (box1.left < Constants.BOUNDS)
            posX = Constants.BOUNDS - box1.offsetX;

        /* vertically */
        if (box1.top < Constants.BOUNDS)
            posY = Constants.BOUNDS - box1.offsetY;
        else if ((box1.bottom) > (Constants.GAME_HEIGHT - Constants.BOUNDS))
            posY = (int) (Constants.GAME_HEIGHT - (box1.offsetY + box1.sizeY + Constants.BOUNDS));
    }

}
