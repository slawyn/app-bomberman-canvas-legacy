package main.buildingblocks;

/* Clonable Animation set */
public class AnimationSet{
    public Animation animations[];

    public AnimationSet(int i) {
        animations = new Animation[i];
    }
    public AnimationSet(AnimationSet set){
        animations = new Animation[set.animations.length];
        for(int i=0;i<animations.length;i++) {
            try {
                animations[i] =(Animation)set.animations[i].clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }
}
