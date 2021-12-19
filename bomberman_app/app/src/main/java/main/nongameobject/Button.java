package main.nongameobject;

import main.Globals;
import main.ResourceLoader;

public class Button extends NonGameObject {
    private final String name;
    private final int sizeX;
    private final int sizeY;
    private final int buttonposx;
    private final int buttonposy;
    public int buttonid;

    public Button(int posx, int posy, int id) {
        super(posx, posy, ResourceLoader.getInstance().getAnimationSet(id));
        name = "";
        buttonid = id;
        sizeX = tex.bmap.getWidth();
        sizeY = tex.bmap.getHeight();
        buttonposx = (int)((posx)* Globals.positionTranslationFactor);
        buttonposy = (int)((posy)* Globals.positionTranslationFactor);
    }

    public Button(int posx, int posy, int id, String n) {
        super(posx, posy, ResourceLoader.getInstance().getAnimationSet(id));
        name = n;
        buttonid = id;
        sizeX = tex.bmap.getWidth();
        sizeY = tex.bmap.getHeight();
        buttonposx = (int)((posx)* Globals.positionTranslationFactor);
        buttonposy = (int)((posy)* Globals.positionTranslationFactor);
    }

    public String getName(){
        return name;
    }

    public boolean isPressed(int touchx, int touchy) {
        if((touchx >= (buttonposx + tex.texOffsetX) && touchx <= (buttonposx + tex.texOffsetX + sizeX) && touchy >= (buttonposy + tex.texOffsetY) && touchy <= (buttonposy + tex.texOffsetY + sizeY))){
            state = 1;
            return true;
        }
        else{
            state = 0;
            return false;
        }
    }
}