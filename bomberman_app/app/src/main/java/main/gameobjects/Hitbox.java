package main.gameobjects;

public class Hitbox {
    public final int sizeX;
    public final int sizeY;
    public final int offsetX;
    public final int offsetY;
    public int top;
    public int left;
    public int bottom;
    public int right;

    public Hitbox(int xoffset, int yoffset, int xsize, int ysize) {
        sizeX = xsize;
        sizeY = ysize;
        offsetX = xoffset;
        offsetY = yoffset;
    }

    public void updateEdges(int x, int y){
        left = x + offsetX;
        right = left + sizeX;
        top = y + offsetY;
        bottom = top + sizeY;
    }
    public boolean intersects(Hitbox box){
        return (left<box.right && right>box.left && top<box.bottom && bottom>box.top);
    }
}
