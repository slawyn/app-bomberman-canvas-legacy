package main.gameobjects;


import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

import main.Globals;
import main.Constants;
import main.buildingblocks.Texture;

public abstract class GameObject implements Comparable<GameObject> {
    public int state;
    public int posX;
    public int posY;
    public Hitbox box1;
    public Hitbox box2;
    public int cellposx;
    public int cellposy;
    private ArrayList<Texture> textures;
    private final static int cellxconstant = (Constants.FIELD_X2 - Constants.FIELD_X1) / Constants.NUMBER_OF_X_CELLS;
    private final static int cellyconstant = (Constants.FIELD_Y2 - Constants.FIELD_Y1) / Constants.NUMBER_OF_Y_CELLS;

    public GameObject(int cellx, int celly) {
        cellposx = cellx;
        cellposy = celly;
        posX = getPositionXFromCell();
        posY = getPositionYFromCell();
        textures = new ArrayList<>();
    }

    public void addBoundingBox1(int xoffset, int yoffset, int xsize, int ysize) {
        box1 = new Hitbox(xoffset, yoffset, xsize, ysize);
    }

    public void addBoundingBox2(int xoffset, int yoffset, int xsize, int ysize) {
        box2 = new Hitbox(xoffset, yoffset, xsize, ysize);
    }

    public void addTexture(int num, Texture tex) {
        if (num < textures.size())
            textures.set(num, tex);
        else
            textures.add(tex);
    }

    public void removeTexture(int num) {
        if (num < textures.size())
            textures.remove(num);
    }

    public int getPositionXFromCell() {
        return Constants.FIELD_X1 + (cellxconstant * cellposx);
    }

    public int getPositionYFromCell() {
        return Constants.FIELD_Y1 + (cellyconstant * cellposy);
    }

    public int getCellFromX() {
        return (posX - Constants.FIELD_X1) / Constants.CELLSIZE_X;
    }

    public int getCellFromY() {
        return (posY - Constants.FIELD_Y1) / Constants.CELLSIZE_Y;
    }

    public int getCellFromCenteredX() {
        return ((box1.left + box1.sizeX / 2) - Constants.FIELD_X1) / Constants.CELLSIZE_X;
    }

    public int getCellFromCenteredY() {
        return ((box1.top + box1.sizeY / 2) - Constants.FIELD_Y1) / Constants.CELLSIZE_Y;
    }


    public boolean collisionCheck11(GameObject col) {
        if (col == null || this == null) {
            return false;
        }
        return box1.intersects(col.box1);
    }

    public boolean collisionCheck12(GameObject col) {
        return box1.intersects(col.box2);
    }

    public void correctPosition(GameObject col) {

        Hitbox box2 = col.box1;

        /* Minkowski sum, simplified */
        int w = ((box1.sizeX + box2.sizeX));
        int h = ((box1.sizeY + box2.sizeY));
        int dx = ((box2.left + box2.right) - (box1.left + box1.right));
        int dy = ((box2.bottom + box2.top) - (box1.top + box1.bottom));

        /* Collision! */
        int wy = w * dy;
        int hx = h * dx;

        if (wy > hx)
            if (wy > -hx)    /* collision at the top */
                posY = box2.top - (box1.sizeY + box1.offsetY);
            else  /* on the right */
                posX = box2.right - box1.offsetX;
        else if (wy > -hx)    /* on the left */
            posX = box2.left - (box1.sizeX + box1.offsetX);
        else    /* at the bottom */
            posY = box2.bottom - box1.offsetY;

        updateBoundingBox1();
    }

    public void updateBoundingBox1() {
        box1.updateEdges(posX, posY);
    }

    public void updateBoundingBox2() {
        box2.updateEdges(posX, posY);
    }

    public void updateAnimations(long dt) {
        int ts = textures.size();
        for (int i = 0; i < ts; i++) {
            textures.get(i).update(dt);
        }
    }

    public void render(Canvas imagebuffer, Paint paint) {
        float scale = Globals.positionTranslationFactor;
        int texposx = (int) (posX * scale);
        int texposy = (int) (posY * scale);
        int ts = textures.size();

        /* Textures */
        for (int i = 0; i < ts; i++) {
            Texture tex = textures.get(i);
            if (tex.visible)
                imagebuffer.drawBitmap(tex.bmap, texposx + tex.texOffsetX, texposy + tex.texOffsetY, paint);
        }

        /* Bounding Box */
        if (Constants.DEBUG_DRAW_HITBOXES) {
            if (box1 != null) {
                imagebuffer.drawRect(box1.left * scale + Globals.gamePortXOffset, box1.top * scale + Globals.gamePortYOffset, box1.right * scale + Globals.gamePortXOffset, box1.bottom * scale + Globals.gamePortYOffset, paint);
            }
            if (box2 != null) {
                imagebuffer.drawRect(box2.left * scale + Globals.gamePortXOffset, box2.top * scale + Globals.gamePortYOffset, box2.right * scale + Globals.gamePortXOffset, box2.bottom * scale + Globals.gamePortYOffset, paint);
            }
        }
    }

    @Override
    public int compareTo(GameObject gobj) {
        return box1.top - gobj.box1.top;
    }
}
