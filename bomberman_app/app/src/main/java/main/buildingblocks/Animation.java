package main.buildingblocks;

import android.graphics.Bitmap;

/* Animation class*/
public class Animation extends Texture implements Cloneable {
    private Bitmap[] frames;
    private int[] animsequence;
    private long frametime;
    private long elptime;
    private boolean once;
    private int idx;



    public Animation(Bitmap bigBitmap, int[] animseq, int nmofframes, long timeofframe, int xoffset, int yoffset, boolean runonce) {
        super(xoffset, yoffset);
        animsequence = animseq;
        frametime = timeofframe;
        frames = new Bitmap[nmofframes];
        once = runonce;
        resetAnimation();
        splitBitmap(bigBitmap, nmofframes);
    }

    public void resetAnimation() {
        idx = 0;
        elptime = 0;
    }

    /* Split sprite into single frames and set the references*/
    private void splitBitmap(Bitmap bigBitmap, int nmofframes) {
        int width = bigBitmap.getWidth() / nmofframes;
        int height = bigBitmap.getHeight();
        for (int i = 0; i < nmofframes; i++) {
            frames[i] = Bitmap.createBitmap(bigBitmap, width * i, 0, width, height);
        }
        bmap = frames[0];
    }

    /* Switch frames: true if Animation is done*/
    public void update(long dt) {
        elptime += dt;
        if (elptime >= frametime) {
            if ((idx + 1) != animsequence.length || !once) {
                idx = (idx + 1) % animsequence.length;
                bmap = frames[animsequence[idx]];
                elptime = 0;
            }
        }
    }

    public Texture getCopy() {
        try {
            return (Texture) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}