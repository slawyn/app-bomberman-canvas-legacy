package main.gameobjects;

import main.Constants;
import main.ResourceLoader;
import main.buildingblocks.AnimationSet;

public class Block extends GameObject {
    private AnimationSet animationSet;
    public Block(int cellxpos, int cellypos){
        super(cellxpos, cellypos);
        init();
    }

    private void init(){
        animationSet = ResourceLoader.getInstance().getCopyOfAnimationSet(Constants.ANIMSET_BLOCK);
        addTexture(0, animationSet.animations[Constants.ANIM_BLOCK0]);
        addBoundingBox1((Constants.BLOCK_BOX_OFFSET_X),(Constants.BLOCK_BOX_OFFSET_Y),(Constants.BLOCK_BOX_WIDTH), (Constants.BLOCK_BOX_HEIGHT));
        updateBoundingBox1();
    }
}
