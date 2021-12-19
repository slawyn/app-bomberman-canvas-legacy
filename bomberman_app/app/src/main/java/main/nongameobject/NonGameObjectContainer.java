package main.nongameobject;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;


public class NonGameObjectContainer {
    private ArrayList<NonGameObject> layer0;

    public NonGameObjectContainer(){
        layer0 = new ArrayList<>();
    }

    public void addObject(NonGameObject nongo, int layer){
        layer0.add(nongo);
    }
    public void updateAnimations(long dt){
        int b = layer0.size();
        for (int a=0;a<b;a++)
            layer0.get(a).updateAnimations(dt);

    }

   public void renderAllObjects(Canvas canvas, Paint p) {
       int b = layer0.size();
       for (int  a=0;a< b;a++) {
           layer0.get(a).render(canvas, p);
       }
   }

   public void wipe(){
        layer0.clear();
   }
}
