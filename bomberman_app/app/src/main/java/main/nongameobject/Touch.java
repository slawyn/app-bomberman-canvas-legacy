package main.nongameobject;


import main.Globals;
import main.buildingblocks.AnimationSet;

public class Touch extends NonGameObject {
    private boolean touchstate;
    private boolean touchstate_prevstate;
    public int dx;
    public int dy;
    public int last_x;
    public int last_y;

    public Touch(AnimationSet animset){
        super(0,0, animset);
        touchstate = false;
        dx = 0;
        dy = 0;
    }

    public void updateTouch(){
        posX = (int)(last_x/ Globals.positionTranslationFactor);
        posY = (int)(last_y/ Globals.positionTranslationFactor);
        tex.visible = touchstate;
    }

    public boolean isTouched(){
        return touchstate;
    }

    public boolean isReleased(){
        if(touchstate_prevstate && !touchstate){
            touchstate_prevstate = false;
            return true;
        }
        return false;
    }

    public void recalculate(boolean touched, int x, int y, boolean move){
        touchstate_prevstate = touchstate;
        touchstate = touched;

        if(move) {
            dx = (x- last_x) +dx;
            dy = (y- last_y) +dy;
        }
        else{
            dx = 0;
            dy = 0;
        }

        last_x = x;
        last_y = y;
    }

}