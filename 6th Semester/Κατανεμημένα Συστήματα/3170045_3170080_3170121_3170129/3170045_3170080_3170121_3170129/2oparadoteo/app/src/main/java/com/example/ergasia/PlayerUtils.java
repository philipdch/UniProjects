package com.example.ergasia;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

/*This class is a wrapper for android's built-in MediaPlayer
  It allows MediaPlayer's methods to be called safely by handling any exceptions
  as well as providing an accurate representation of the player's state at any given moment.
  This class uses MediaPlayer's class diagram found at
  https://developer.android.com/reference/android/media/MediaPlayer#state-diagram
 */
public class PlayerUtils {
    public static final String[] STATES = {"CREATED", "PREPARED", "PLAYING", "PAUSED", "STOPPED"};
    private MediaPlayer player;
    private String state;
    private Context context;

    public PlayerUtils(MediaPlayer player, Context context){
        this.player = player;
        state = STATES[0];
        this.context = context;
    }

    public PlayerUtils(Context context){
        this(new MediaPlayer(), context);
    }

    public MediaPlayer getPlayer(){
        return player;
    }

    public String getState(){
        return state;
    }

    public void setState(String newState){
        this.state = newState;
    }

    public void initialise(Uri uri){
        Log.d(PlayerUtils.class.getName(), "State before Initialise(): " + state);
        try{
            if(state.equals(STATES[0])) {
                player.setDataSource(context, uri);
                player.prepare();
                state = STATES[1];
                Log.e(PlayerUtils.class.getName(), "Player is initialised");
            }
        }catch(IOException e){
            Log.e(PlayerUtils.class.getName(), "Failed to prepare MediaPlayer");
            e.printStackTrace();
        }
        Log.d(PlayerUtils.class.getName(), "State after Initialise(): " + state);
    }

    public void initialise(String path){
        Log.d(PlayerUtils.class.getName(), "State before Initialise(): " + state);
        try{
            if(state.equals(STATES[0])) {
                player.setDataSource(path);
                player.prepare();
                state = STATES[1];
                Log.d(PlayerUtils.class.getName(), "Player is initialised (string path)");
            }
        }catch(IOException e){
            Log.e(PlayerUtils.class.getName(), "Failed to prepare MediaPlayer");
            e.printStackTrace();
        }
        Log.d(PlayerUtils.class.getName(), "State after Initialise(): " + state);
    }

    public void start(){
        Log.d(PlayerUtils.class.getName(), "State before start(): " + state);
        try{
            if(state.equals(STATES[2]) || state.equals(STATES[1])) {
                player.start();
                state = STATES[2];
            }
        }catch(IllegalStateException e){
            Log.e(PlayerUtils.class.getName(), "Failed to start MediaPlayer");
        }
        Log.d(PlayerUtils.class.getName(), "State after start(): " + state);
    }

    public void pause(){
        Log.d(PlayerUtils.class.getName(), "State before pause: " + state);
        try {
            if (state.equals(STATES[2])) {
                player.pause();
            }
        }catch(IllegalStateException e){
            Log.e(PlayerUtils.class.getName(), "Failed to pause MediaPlayer");
        }
        Log.d(PlayerUtils.class.getName(), "State after pause(): " + state);
    }

    public void stop(){
        if(state.equals(STATES[2]) || state.equals(STATES[3])){
            try{
                player.stop();
                state = STATES[4];
            }catch(IllegalStateException e){
                Log.e(PlayerUtils.class.getName(), "Failed to stop MediaPlayer");
            }
        }
    }

    public void setNextPlayer(PlayerUtils next){
        Log.d(PlayerUtils.class.getName(), "State before setNext: " + state);
        if(next.getState() == null) {
            Log.d(PlayerUtils.class.getName(), "Next player might not have been initialised");
            return;
        }
        while(!next.getState().equals(STATES[1])){}
        try {
            Log.d(PlayerUtils.class.getName(), "PLAYER IS PREPARED. SETTING NEXT");
            player.setNextMediaPlayer(next.getPlayer());
        }catch(IllegalStateException e){
            Log.e(PlayerUtils.class.getName(), "Error setting next player");
            e.printStackTrace();
        }
    }

    public void reset(){
        Log.d(PlayerUtils.class.getName(), "State before reset(): " + state);
        player.reset();
        state = STATES[0];
        Log.d(PlayerUtils.class.getName(), "State after reset(): " + state);
    }

    public void destroy(){
        if(player != null) {
            player.release();
        }
        player = null;
        state = null;
    }
}