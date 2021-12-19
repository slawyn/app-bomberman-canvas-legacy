package main.buildingblocks;

import android.graphics.Bitmap;

/* Texture class for layers*/
public abstract class Texture{
    public boolean visible;
    public int texOffsetX;
    public int texOffsetY;
    public Bitmap bmap;


    public Texture(int xoffset, int yoffset){
        texOffsetX = xoffset;
        texOffsetY = yoffset;
        visible = true;
    }

    public void update(long dt){}
}
