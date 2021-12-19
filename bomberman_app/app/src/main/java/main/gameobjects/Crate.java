package main.gameobjects;

import main.Constants;
import main.ResourceLoader;
import main.buildingblocks.AnimationSet;

public class Crate extends GameObject {
    private AnimationSet animationSet;
    public Crate(int cellxpos, int cellypos){
        super(cellxpos, cellypos);
        init();
    }

    private void init(){
        animationSet = ResourceLoader.getInstance().getAnimationSet(Constants.ANIMSET_CRATE);
        posX = getPositionXFromCell();
        posY = getPositionYFromCell();

        addTexture(0, animationSet.animations[Constants.ANIM_CRATE0]);
        addBoundingBox1((Constants.CRATE_BOX_OFFSET_X),(Constants.CRATE_BOX_OFFSET_Y),(Constants.CRATE_BOX_WIDTH), (Constants.CRATE_BOX_HEIGHT));
        updateBoundingBox1();

    }
}
