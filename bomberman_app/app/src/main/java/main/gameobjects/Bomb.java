package main.gameobjects;

import main.Constants;
import main.ResourceLoader;
import main.buildingblocks.AnimationSet;

public class Bomb extends GameObject {
    public AnimationSet animationset;
    public boolean exploded;
    public long timer;
    public int owner;
    public boolean peoplewalking;
    public boolean nonwalkable;
    public int strength;

    public Bomb(int cellxpos, int cellypos, long timer_, int owner_, int strength_){
        super(cellxpos, cellypos);
        strength = strength_;
        init(timer_,owner_);
    }

    private void init(long timer_, int owner_){
        animationset = ResourceLoader.getInstance().getCopyOfAnimationSet(Constants.GOT_ROBOT_BOMB);
        exploded = false;
        owner = owner_;
        timer = timer_;
        nonwalkable = false;
        peoplewalking = true;
        addTexture(0, animationset.animations[0]);
        addBoundingBox1(Constants.BOMB_BOX_OFFSET_X, Constants.BOMB_BOX_OFFSET_Y, Constants.BOMB_BOX_WIDTH,Constants.BOMB_BOX_HEIGHT);
        updateBoundingBox1();
    }

    public void updateState(long dt, GameObjectContainer gameObjectContainer){

        /* Update bounding box1 */
        timer-=dt;
        if(!peoplewalking)
            nonwalkable = true;
        else
            peoplewalking = false;

        /* Bomb timer */
        if(timer<=0){
            exploded = true;
            gameObjectContainer.addExplosion(cellposx, cellposy, strength, owner);

        }
    }
}
