package main.buildingblocks;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.am.bluetooth.bluetooth.R;

/* Class for sounds*/
public class Sound{
    final MediaPlayer sfx;
    public Sound(Context context, int soundable){
      sfx =  MediaPlayer.create(context, soundable );
      // TODO
       //sfx.start();
    }
}
