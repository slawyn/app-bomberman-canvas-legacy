package main.nongameobject;

import android.graphics.Canvas;
import android.graphics.Paint;

import main.Globals;
import main.buildingblocks.AnimationSet;
import main.buildingblocks.Texture;

public abstract class NonGameObject {
    public AnimationSet animationSet;
    public int posX;
    public int posY;
    public int  state;
    public Texture tex;

    public NonGameObject(int posx, int posy, AnimationSet animset){
        animationSet = animset;
        tex = animationSet.animations[0];
        posX = posx;
        posY = posy;
        state = 0;
    }

    public void updateAnimations(long dt){
        tex = animationSet.animations[state];
        tex.update(dt);
    }

    public void render(Canvas imagebuffer, Paint paint){
        if(tex.visible) {
            float scale = Globals.positionTranslationFactor;
            int texposx = (int) (posX * scale)+ tex.texOffsetX;
            int texposy = (int) (posY * scale)+ tex.texOffsetY;
            imagebuffer.drawBitmap(tex.bmap, texposx, texposy, paint);
        }
    }
}
