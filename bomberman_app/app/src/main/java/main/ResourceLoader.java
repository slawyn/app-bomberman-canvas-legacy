package main;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.am.bluetooth.bluetooth.R;

import java.util.HashMap;

import main.buildingblocks.Animation;
import main.buildingblocks.Sound;
import main.buildingblocks.AnimationSet;


public class ResourceLoader {
        public HashMap<Integer,AnimationSet> animations;
        private HashMap<Integer,Sound> sounds;
        private static ResourceLoader rcloader;
        private Resources resource;

        /* HashMap uses character types as keys*/
        private ResourceLoader(){
            animations = new HashMap<>(); // animations
            sounds = new HashMap<>();     // sounds
        }

        public static ResourceLoader getInstance(){
            if(rcloader == null)
                rcloader = new ResourceLoader();
            return rcloader;
        }
        /*
            36x36 (0.75x) for low-density (ldpi)
            48x48 (1.0x baseline) for medium-density (mdpi)
            72x72 (1.5x) for high-density (hdpi)
            96x96 (2.0x) for extra-high-density (xhdpi)
            144x144 (3.0x) for extra-extra-high-density (xxhdpi)
            192x192 (4.0x) for extra-extra-extra-high-density (xxxhdpi)
        */

        // Optimization load parts that are needed on the fly
        public void setResources(Resources resource){this.resource = resource;}
        public void calculateTextureOffsets(int dpi, int measuredScreenWidth, int measuredScreenHeight){

            Globals.measuredWidth = measuredScreenWidth;
            Globals.measuredHeight= measuredScreenHeight;

            float ratioPhysicScreen = (float)measuredScreenWidth/(float)measuredScreenHeight;
            float ratioWanted = Constants.GAME_WIDTH/Constants.GAME_HEIGHT;

            float scale = dpi/Constants.GAME_DPI;
            int newScreenWidth, newScreenHeight;
            if(ratioWanted>ratioPhysicScreen) {
                newScreenWidth = (measuredScreenWidth);
                newScreenHeight = (int) (measuredScreenWidth * (Constants.GAME_HEIGHT * scale) / (Constants.GAME_WIDTH * scale));
            }
            else{
                newScreenWidth = (int) (measuredScreenHeight / (Constants.GAME_HEIGHT * scale) * (Constants.GAME_WIDTH * scale));
                newScreenHeight = (measuredScreenHeight);
            }

            Globals.gamePortWidth = newScreenWidth;
            Globals.gamePortHeight = newScreenHeight;
            Globals.positionTranslationFactor = (float)newScreenWidth/Constants.GAME_WIDTH;

            Globals.gamePortXOffset = (measuredScreenWidth - newScreenWidth)/2;
            Globals.gamePortYOffset = (measuredScreenHeight - newScreenHeight)/2;

        }

        public void loadSounds(){//TODO
        }

        public void loadAnimations(){
            float scale =  Globals.positionTranslationFactor;
            int offsetx = (int) Globals.gamePortXOffset;
            int offsety = (int) Globals.gamePortYOffset;

            /* Background: grass */
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = true;
            opts.inDensity = (int)Constants.GAME_WIDTH;
            opts.inTargetDensity =  (int)(Constants.GAME_WIDTH* scale);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;


            /* Fields */
            int field_seq [] = {0};
            Animation field0 = new Animation(BitmapFactory.decodeResource(resource, R.drawable.field, opts), field_seq, 1, 0, offsetx + (int)(Constants.FIELD_X1*scale) ,offsety+(int)(Constants.FIELD_Y1*scale), false);
            AnimationSet animset = new AnimationSet(1);
            animset.animations[Constants.ANIM_FIELD0] = field0;
            animations.put(Constants.ANIMSET_FIELD,animset);



            /* Backgrounds */
            int background_seq [] = {0};
            animset = new AnimationSet(2);
            animset.animations[Constants.ANIM_BACKGROUND] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.background, opts), background_seq, 1, 0, offsetx  ,offsety, false);
            animset.animations[Constants.ANIM_BACKGROUND_WHITE] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.background_white, opts), background_seq, 1, 0, offsetx  ,offsety, false);;
            animations.put(Constants.ANIMSET_BACKGROUND, animset);

            /* Test Field background*/
            animset = new AnimationSet(1);
            animset.animations[Constants.ANIM_BACKGROUND] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.fiedlbackground, opts), background_seq, 1, 0, offsetx  ,offsety, false);
            animations.put(Constants.ANIMSET_BACKGROUND_FIELD, animset);



            /* Buttons */
            int button_seq [] = {0};
            animset = new AnimationSet(2);
            animset.animations[Constants.ANIM_BUTTON_CLIENT_UNPRESSED] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.button_connect, opts), button_seq, 1, 0, offsetx  ,offsety, false);
            animset.animations[Constants.ANIM_BUTTON_CLIENT_PRESSED] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.button_connect_pressed, opts), button_seq, 1, 0, offsetx  ,offsety, false);;
            animations.put(Constants.ANIMSET_BUTTONS_CLIENT, animset);

            /* Buttons */
            animset = new AnimationSet(2);
            animset.animations[Constants.ANIM_BUTTON_SERVER_UNPRESSED] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.button_createserver, opts), button_seq, 1, 0, offsetx  ,offsety, false);
            animset.animations[Constants.ANIM_BUTTON_SERVER_PRESSED] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.button_createserver_pressed, opts), button_seq, 1, 0, offsetx  ,offsety, false);;
            animations.put(Constants.ANIMSET_BUTTONS_SERVER, animset);

            /* Buttons */
            animset = new AnimationSet(2);
            animset.animations[Constants.ANIM_BUTTON_NORMAL_UNPRESSED] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.button_normal, opts), button_seq, 1, 0, offsetx  ,offsety, false);
            animset.animations[Constants.ANIM_BUTTON_NORMAL_PRESSED] = new Animation(BitmapFactory.decodeResource(resource, R.drawable.button_normal_pressed, opts), button_seq, 1, 0, offsetx  ,offsety, false);;
            animations.put(Constants.ANIMSET_BUTTONS_NORMAL, animset);

            /* D-PAD  */
            int touch_fig_seq [] = {0};
            Bitmap b = BitmapFactory.decodeResource(resource, R.drawable.touch_circle, opts);
            animset = new AnimationSet(1);
            animset.animations[Constants.ANIM_CIRCLE] = new Animation(b, touch_fig_seq, 1, 0, offsetx-b.getWidth()/2  ,offsety-b.getHeight()/2, false);
            animations.put(Constants.ANIMSET_CIRCLE, animset);

            animset = new AnimationSet(1);
            b = BitmapFactory.decodeResource(resource, R.drawable.touch_square, opts);
            animset.animations[Constants.ANIM_SQUARE] = new Animation(b, touch_fig_seq, 1, 0, offsetx-b.getWidth()/2  ,offsety-b.getHeight()/2, false);
            animations.put(Constants.ANIMSET_SQUARE, animset);


            /* Block */
            int block_seq [] = {0};
            animset = new AnimationSet(1);
            b = BitmapFactory.decodeResource(resource, R.drawable.block, opts);
            animset.animations[Constants.ANIM_BLOCK0] = new Animation(b, block_seq, 1, 0, offsetx  ,(int)(offsety+(Constants.CELLSIZE_Y*scale-b.getHeight())), false);
            animations.put(Constants.ANIMSET_BLOCK, animset);

            /* Crate */
            int crate_seq [] = {0};
            animset = new AnimationSet(1);
            b = BitmapFactory.decodeResource(resource, R.drawable.crate, opts);
            animset.animations[Constants.ANIM_CRATE0] = new Animation(b, crate_seq, 1, 0, offsetx,(int)(offsety+(Constants.CELLSIZE_Y*scale-b.getHeight())), false);
            animations.put(Constants.ANIMSET_CRATE, animset);

            /* Walk left*/
            int w_left_seq[] = {0,1,2,3,4,5,6,7,8,9,10,11};
            Animation w_left_anim = new Animation(BitmapFactory.decodeResource(resource, R.drawable.walk_left,opts),w_left_seq,12,50,offsetx,offsety,false);

            /* Walk right */
            int w_right_seq[] = {0,1,2,3,4,5,6,7,8,9,10,11};
            Animation w_right_anim = new Animation(BitmapFactory.decodeResource(resource, R.drawable.walk_right,opts),w_right_seq,12,50,offsetx,offsety,false);

            /* Walk up*/
            int w_up_seq[] = {0,1,2,3,4,5,6,7,8,9,10};
            Animation w_up_anim = new Animation( BitmapFactory.decodeResource(resource, R.drawable.walk_up,opts),w_up_seq,11,50,offsetx,offsety,false);

            /* Walk down*/
            int w_down_seq[] = {0,1,2,3,4,5,6,7,8,9,10};
            Animation w_down_anim = new Animation(BitmapFactory.decodeResource(resource, R.drawable.walk_down,opts),w_down_seq,11,50,offsetx,offsety,false);

            /* Idle down*/
            int i_down_seq[] = {0};
            Animation i_down_anim = new Animation(BitmapFactory.decodeResource(resource, R.drawable.idle_down, opts),i_down_seq,1,500,offsetx,offsety,false);

            /* Idle down*/
            int dying_seq[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13};
            Animation dying_anim = new Animation(BitmapFactory.decodeResource(resource, R.drawable.dying, opts),dying_seq,14,50 ,offsetx,offsety,true);

            /* Animations for Player*/
            animset = new AnimationSet(9);
            animset.animations[Constants.ANIM_STAND_LEFT]    = i_down_anim;
            animset.animations[Constants.ANIM_STAND_RIGHT]   = i_down_anim;
            animset.animations[Constants.ANIM_STAND_DOWN]    = i_down_anim;
            animset.animations[Constants.ANIM_STAND_UP]      = i_down_anim;
            animset.animations[Constants.ANIM_WALK_LEFT]     = w_left_anim;
            animset.animations[Constants.ANIM_WALK_RIGHT]    = w_right_anim;
            animset.animations[Constants.ANIM_WALK_DOWN]     = w_down_anim;
            animset.animations[Constants.ANIM_WALK_UP]       = w_up_anim;
            animset.animations[Constants.ANIM_DYING]       =   dying_anim;

            animations.put(Constants.ANIMSET_ROBOT,animset);


            /* Bomb */
            int bomb_seq[] = {0,1,2,1};
            Animation bomb_anim = new Animation(BitmapFactory.decodeResource(resource, R.drawable.bomb,opts),bomb_seq,3,150, offsetx, offsety+(int)(Constants.CELLSIZE_Y*scale-b.getHeight()),false);

            /* Bomb animations */
            animset = new AnimationSet(1);
            animset.animations[0] = bomb_anim;
            animations.put(Constants.ANIMSET_ROBOT_BOMB, animset);

            /* Explosion anims */
            int explosion_seq[] = {0,1,2};
            Animation explosion_anim_middle = new Animation(BitmapFactory.decodeResource(resource, R.drawable.explosion_middle,opts), explosion_seq,3,50, offsetx, offsety+(int)(Constants.CELLSIZE_Y*scale-b.getHeight()),false);
            Animation explosion_anim_horizontal = new Animation(BitmapFactory.decodeResource(resource, R.drawable.explosion_horizontal,opts), explosion_seq,3,50, offsetx, offsety+(int)(Constants.CELLSIZE_Y*scale-b.getHeight()),false);
            Animation explosion_anim_vertical = new Animation(BitmapFactory.decodeResource(resource, R.drawable.explosion_vertical,opts), explosion_seq,3,50, offsetx, offsety+(int)(Constants.CELLSIZE_Y*scale-b.getHeight()),false);

            animset = new AnimationSet(3);
            animset.animations[Constants.ANIM_EXPLOSION_MIDDLE] = explosion_anim_middle;
            animset.animations[Constants.ANIM_EXPLOSION_HORIZONTAL] = explosion_anim_horizontal;
            animset.animations[Constants.ANIM_EXPLOSION_VERTICAL] = explosion_anim_vertical;

            animations.put(Constants.ANIMSET_EXPLOSION, animset);
        }

        public AnimationSet getAnimationSet(int type){ return this.animations.get(type);}
        public AnimationSet getCopyOfAnimationSet(int type){ return new AnimationSet(this.animations.get(type)); }



}





