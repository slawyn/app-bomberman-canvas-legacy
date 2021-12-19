package main.gameobjects;

import main.Globals;
import main.Constants;
import main.ResourceLoader;
import main.buildingblocks.AnimationSet;
import main.buildingblocks.Texture;

public class Explosion extends GameObject{
    private AnimationSet animationset;
    private long timer = Constants.EXPLOSION_TIME;
    private int owner;
    public int right;
    public int left;
    public int bottom;
    public int top;
    public int strength;

    public Explosion(int cellxpos, int cellypos, int strength_, int r, int l, int t, int b, int owner_){
        super(cellxpos, cellypos);
        animationset = ResourceLoader.getInstance().getCopyOfAnimationSet(Constants.ANIMSET_EXPLOSION);
        strength = strength_;
        owner = owner_;
        right = r;
        left = l;
        bottom = b;
        top = t;


        addTexture(0, animationset.animations[Constants.ANIM_EXPLOSION_MIDDLE]);


        /* Add animations */
        for(int i=1,j =1;i<=strength;i++){
            if(i<=l){
                Texture tex = (animationset.animations[Constants.ANIM_EXPLOSION_HORIZONTAL]).getCopy();
                tex.texOffsetX += (-i*Constants.CELLSIZE_X)* Globals.positionTranslationFactor;
                addTexture(j++, tex);
            }
            if(i<=r){
                Texture tex = (animationset.animations[Constants.ANIM_EXPLOSION_HORIZONTAL]).getCopy();
                tex.texOffsetX += i*Constants.CELLSIZE_X* Globals.positionTranslationFactor;
                addTexture(j++, tex);
            }
            if(i<=t){
                Texture tex = (animationset.animations[Constants.ANIM_EXPLOSION_VERTICAL]).getCopy();
                tex.texOffsetY += -i*Constants.CELLSIZE_Y* Globals.positionTranslationFactor;
                addTexture(j++, tex);
            }
            if(i<=b){
                Texture tex = (animationset.animations[Constants.ANIM_EXPLOSION_VERTICAL]).getCopy();
                tex.texOffsetY += i*Constants.CELLSIZE_Y* Globals.positionTranslationFactor;
                addTexture(j++, tex);
            }
        }

        addBoundingBox1(-l*Constants.CELLSIZE_X,0,(r+l+1)*Constants.CELLSIZE_X, Constants.CELLSIZE_Y);
        addBoundingBox2(0,-t*Constants.CELLSIZE_Y,Constants.CELLSIZE_X,(t+b+1)*Constants.CELLSIZE_Y);

        updateBoundingBox1();
        updateBoundingBox2();
    }

    public boolean updateState(long dt) {
        timer -= dt;
        if (timer <= 0) {
            return true;
        }
        return false;
    }
}
