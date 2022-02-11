package com.example.ergasia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.io.FileUtils;

import sharedResources.Value;

public class Fetch_Activity extends AppCompatActivity {

    Queue<Value> toPlay=new LinkedList<Value>();
    MediaPlayer[] players;
    static int chunksNo;
    String song;
    PlayerUtils player;
    PlayerUtils buffer;
    final static int MILLISECONDS_TO_SKIP=5000;

    AudioMetaData audioMetaData;
    MediaMetadataRetriever metadata;

    TextView songName;
    TextView artist;
    TextView albumArtist;
    TextView albumInf;
    TextView genr;
    TextView comp;
    TextView songDuration;
    TextView songYear;
    TextView songNumber;
    ImageView songImage;
    SeekBar seekBar;

    String trackName;
    String artistName;
    String albumArtistName;
    String albumInfo;
    String genre;
    String composer;
    String trackDuration;
    String year;
    String trackNumber;
    String seconds;
    String minutes;
    byte[] image;
    private PlayAudioAsyncTask task2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        ActivityCompat.requestPermissions(Fetch_Activity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},23);

        songName=(TextView)findViewById(R.id.trackName);
        artist=(TextView)findViewById(R.id.artistName);
        albumArtist=(TextView)findViewById(R.id.albumArtistName);
        albumInf=(TextView)findViewById(R.id.albumInfo);
        genr=(TextView)findViewById(R.id.genre);
        comp=(TextView)findViewById(R.id.composer);
        songDuration=(TextView)findViewById(R.id.duration);
        songYear=(TextView)findViewById(R.id.year);
        songNumber=(TextView)findViewById(R.id.trackNumber);
        songImage=(ImageView)findViewById(R.id.songImage);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("tag", "On start");

        Intent previousIntent=getIntent();
        song=previousIntent.getStringExtra("songName");

        Fetch_Activity.FetchAsyncTask task = new Fetch_Activity.FetchAsyncTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,song);

        task2=new Fetch_Activity.PlayAudioAsyncTask();
        task2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }




    private class FetchAsyncTask extends AsyncTask<String, String, Queue<Value>> {
        ProgressDialog progressDialog;
        Socket socketClient;
        ObjectOutputStream output;
        ObjectInputStream input;

        @Override
        protected Queue<Value> doInBackground(String... song) {
            Log.e("FetchAsyncTask", "In doInBackground");
            try {

                try {
                    socketClient=SocketHandler.getSocket();
                    output=SocketHandler.getOutput();
                    input=SocketHandler.getInput();
                    Log.e("Connection:","continues, socket ip:"+socketClient.getInetAddress().toString()+" port:"+socketClient.getPort());

                    output.writeObject(song[0]);
                    output.flush();
                    boolean answer = input.readBoolean();
                    String received = (String) input.readObject();
                    //server not available, ERROR
                    if (!answer) {
                        Log.e("Broker answer", received);
                    }
                    //server available, fetching chunks
                    else {
                        Log.d("Broker answer", received);

                        chunksNo = (int) input.readObject();
                        players = new MediaPlayer[chunksNo];
                        for (int i=0; i<chunksNo; i++) {
                            toPlay.add((Value) input.readObject());
                            Log.d("Broker sends chunk no", String.valueOf(i+1));
                        }

                        if(SocketHandler.isOffline()) {
                            seekBar=(SeekBar)findViewById(R.id.seekBar);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                            player.getPlayer().seekTo(progress);
                                        }

                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar) {
                                        }

                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar) {
                                        }
                                    });
                                }
                            });

                            offlinemode();
                            audioMetaData=fetchMetaData(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+ song[0] +"_1.mp3");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setViewMetaData(audioMetaData,SocketHandler.isOffline());//true
                                }
                            });


                        }
                    }
                    Thread.sleep(1000);
                    publishProgress("Fetching song");

                    input.readObject();
                    output.writeObject("exit");

                } catch (InterruptedException | ClassNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    socketClient.close();
                    input.close();
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return toPlay;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Queue<Value> list) {
            Toast.makeText(Fetch_Activity.this,  chunksNo  + " Chunks Arrived", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

    }
    private class PlayAudioAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("PlayAudioAsyncTask", "In doInBackground");

            while(toPlay.isEmpty()){}


            if (!SocketHandler.isOffline()) {

                player = new PlayerUtils(Fetch_Activity.this);
                buffer = new PlayerUtils(Fetch_Activity.this);
                for(int i=0; i<=chunksNo; i++) {
                    Log.d("In playAudio", "In while, queue is NOT EMPTY");

                    if(i%2==0) {
                        while (player.getState().equals(PlayerUtils.STATES[2]) || player.getState().equals(PlayerUtils.STATES[3])) {
                        }
                        while (toPlay.isEmpty()) {
                            //Log.e("Streaming error", "Connection too slow");
                        }
                        File file = new File(getCacheDir() + "temp");
                        try {
                            Log.d("In playAudio, even No", "Writing track on temp: " + toPlay.peek().getMusicFile().getTrackName());
                            FileUtils.writeByteArrayToFile(file, toPlay.remove().getMusicFile().getMusicFileExtract());

                            if(i==0) {
                                Log.d("In playAudio", "Fetching metadata from first chunk");

                                audioMetaData=fetchMetaData(getCacheDir()+"temp");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setViewMetaData(audioMetaData,SocketHandler.isOffline());// false
                                    }
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Uri uri = Uri.fromFile(file);
                        player.initialise(uri);
                        player.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (player.getPlayer() != null) {
                                    player.reset();
                                    Log.d("player state", " reset");
                                    buffer.setState(PlayerUtils.STATES[2]);
                                }
                            }
                        });
                        if (i == 0) player.start();
                        else buffer.setNextPlayer(player);
                    }
                    else {
                        while (buffer.getState().equals(PlayerUtils.STATES[2]) || buffer.getState().equals(PlayerUtils.STATES[3])) {
                        }
                        while (toPlay.isEmpty()) {
                            //Log.e("Streaming error", "Connection too slow");
                        }
                        File file = new File(getCacheDir() + "temp1");
                        try {
                            Log.d("In playAudio, odd No", "Writing track on temp: " + toPlay.peek().getMusicFile().getTrackName());
                            FileUtils.writeByteArrayToFile(file, toPlay.remove().getMusicFile().getMusicFileExtract());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Uri uri1 = Uri.fromFile(file);
                        buffer.initialise(uri1);
                        buffer.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (buffer.getPlayer() != null) {
                                    buffer.reset();
                                    Log.d("buffer state", "reset");
                                    player.setState(PlayerUtils.STATES[2]);
                                }
                            }
                        });
                        player.setNextPlayer(buffer);
                    }
                }

                Log.d("In playAudio", "End of song");

                while(player.getState().equals(PlayerUtils.STATES[2]) || player.getState().equals(PlayerUtils.STATES[3]) || buffer.getState().equals(PlayerUtils.STATES[2]) || buffer.getState().equals(PlayerUtils.STATES[3])){}
                player.destroy();
                Log.d("Media Player: player", "released");
                buffer.destroy();
                Log.d("Media Player: buffer", "released");
            }
            return null;
        }
    }
    /*
        creates MediaMetaDataRetriever, calls for it setDataSource method with the path of the file of the song,
       creates AudioMetaData object and sets its fields with the metadata that MediaMetaDataRetriever object(metadata) extracts from
       the mp3.Finally, it releases the MediaMetaDataRetriever and returns AudioMetaData object
   */
    private AudioMetaData fetchMetaData(String filePath){
        metadata=new MediaMetadataRetriever();
        metadata.setDataSource(filePath);
        AudioMetaData audioMetaData=new AudioMetaData();

        Log.d("Audio info "," follows");
        trackName = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        audioMetaData.setTrackName(trackName);
        Log.d("TrackName",audioMetaData.getTrackName());
        artistName = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        audioMetaData.setArtistName(artistName);
        Log.d("ArtistName",audioMetaData.getArtistName());
        albumArtistName = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        audioMetaData.setAlbumArtistName(albumArtistName);
        Log.d("AlbumArtist",audioMetaData.getAlbumArtistName());
        albumInfo = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        audioMetaData.setAlbumInfo(albumInfo);
        Log.d("AlbumInfo",audioMetaData.getAlbumInfo());
        genre = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
        audioMetaData.setGenre(genre);
        Log.d("Genre",audioMetaData.getGenre());
        composer = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
        audioMetaData.setComposer(composer);
        Log.d("Composer",audioMetaData.getComposer());
        year=metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
        audioMetaData.setYear(year);
        Log.d("Year",audioMetaData.getYear());
        trackNumber=metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
        audioMetaData.setTrackNumber(trackNumber);
        Log.d("TrackNumber",audioMetaData.getTrackNumber());
        trackDuration=metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        audioMetaData.setTrackDuration(trackDuration);
        Log.d("time", audioMetaData.getTrackDuration());

        if(trackDuration!=null) {
            long dur = Long.parseLong(audioMetaData.getTrackDuration());
            Log.d("MilliSeconds", String.valueOf(dur));
            seconds = String.valueOf((dur % 60000) / 1000);
            audioMetaData.setSeconds(seconds);
            Log.d("Seconds", audioMetaData.getSeconds());
            minutes = String.valueOf(dur / 60000);
            audioMetaData.setMinutes(minutes);
            Log.d("Minutes", audioMetaData.getMinutes());
            String out = minutes + ":" + seconds;
            Log.d("Duration", out);
        }

        image = metadata.getEmbeddedPicture();
        audioMetaData.setImage(image);

        metadata.release();

        return audioMetaData;
    }

    //calls setText method for each TextView in order to fill it with the values of AudioMetaData object
    private void setViewMetaData(AudioMetaData audioMetaData,boolean mode){
        songName.setText(audioMetaData.getTrackName());
        artist.setText(String.format("Artist: %s",audioMetaData.getArtistName()));
        albumArtist.setText(String.format("AlbumArtist: %s",audioMetaData.getAlbumArtistName()));
        albumInf.setText(String.format("Album: %s",audioMetaData.getAlbumInfo()));
        genr.setText(String.format("Genre: %s",audioMetaData.getGenre()));
        comp.setText(String.format("Composer: %s",audioMetaData.getComposer()));
        songYear.setText(String.format("Year: %s",audioMetaData.getYear()));
        songNumber.setText(String.format("TrackNumber: %s",audioMetaData.getTrackNumber()));

        if(mode) {
            if (seconds.length() == 1) {
                songDuration.setText("0" + audioMetaData.getMinutes() + ":0" + audioMetaData.getSeconds());
            } else {
                songDuration.setText("0" + audioMetaData.getMinutes() + ":" + audioMetaData.getSeconds());
            }
        }

        if( audioMetaData.getImage() != null ){
            songImage.setImageBitmap( BitmapFactory.decodeByteArray(audioMetaData.getImage(), 0, audioMetaData.getImage().length));
        }else{
            songImage.setImageResource(R.drawable.defaultimage1);
        }
    }

    private void offlinemode(){
        //count each chunk's byte array elements and add to fileSize
        int fileSize=0;
        for (Value val: toPlay){
            Log.d("OfflineMode "," ****Chunk " + val.getMusicFile().getTrackName() + " has " + val.getMusicFile().getMusicFileExtract().length +" bytes.");
            fileSize = fileSize + val.getMusicFile().getMusicFileExtract().length;
        }

        //create byte array for final file
        byte [] toSave = new byte[fileSize];
        int j=0; //pointer to toSave array
        for (Value v: toPlay){
            for (int i=0; i<v.getMusicFile().getMusicFileExtract().length; i++){
                toSave[j] = v.getMusicFile().getMusicFileExtract()[i];
                j++;
            }
        }
        Log.d("OfflineMode "," ****Total size of song " + toSave.length + " bytes.");

        writeMp3File(toPlay,toSave);

    }
    private void writeMp3File(Queue<Value> toPlay, byte [] toSave){
        //write byte array in mp3File
        try {
            ActivityCompat.requestPermissions(Fetch_Activity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},23);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), toPlay.peek().getMusicFile().getTrackName()+".mp3");
            FileUtils.writeByteArrayToFile(file, toSave);
            Log.d("File progress", "Successfully written file "+toPlay.peek().getMusicFile().getTrackName()+".mp3"+" to external storage. Path: " + Environment.DIRECTORY_DOWNLOADS);

        }catch(IOException e){
            Log.e("writeMpe3File","Error creating file");
        }
    }
    /*
        Methods for handling the MediaPlayers.
     */

    public void play(View v){
        if(SocketHandler.isOffline()){
            if (player == null || (player.getState() == null)) {
                player = new PlayerUtils(Fetch_Activity.this);
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+ song +"_1.mp3";
                Log.e("Player state", (player == null) +" media: " );
                try {
                    player.initialise(path);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Exception of type : " + e.toString());
                    e.printStackTrace();
                }
                player.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlayer();
                    }
                });
            }
            seekBar.setMax(player.getPlayer().getDuration());
            player.start();
        }
        else {
            if (player.getPlayer() != null && player.getState().equals(PlayerUtils.STATES[2])) player.start();
            else if (buffer.getPlayer() != null && buffer.getState().equals(PlayerUtils.STATES[2])) buffer.start();
        }
    }
    public void pause(View v) {
        if(SocketHandler.isOffline()){
            if (player.getPlayer() != null) {
                player.pause();
            }
        }
        else {
            if (player.getPlayer() != null && player.getState().equals(PlayerUtils.STATES[2])) {
                player.pause();
            } else if (buffer.getPlayer() != null && buffer.getState().equals(PlayerUtils.STATES[2])) {
                buffer.pause();
            }
        }
    }

    public void stop(View v) {

        if(SocketHandler.isOffline()) { stopPlayer(); }
        else{ Toast.makeText(this, "Can't stop player in online mode", Toast.LENGTH_SHORT).show(); }
    }

    private void stopPlayer() {
        if (player!= null) {
            if(player.getPlayer()!=null){
                player.destroy();
                Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void stopBufferPlayer() {
        if (buffer!= null) {
            if(buffer.getPlayer()!=null) {
                buffer.destroy();
                Toast.makeText(this, "BufferMediaPlayer released", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onStop() {
        if(SocketHandler.isOffline()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + song + "_1.mp3");
            boolean delete = file.delete();
            if (delete)
                Log.d("File", "Temp file deleted successfully");
            else
                Log.d("File", "Failed to delete file");
        }
        super.onStop();
        stopPlayer();
        stopBufferPlayer();
    }

    public void skipForward(View view) {
        if (SocketHandler.isOffline()) {
            if (player.getPlayer() != null) {
                if (player.getPlayer().getDuration() - player.getPlayer().getCurrentPosition() >= MILLISECONDS_TO_SKIP)
                    player.getPlayer().seekTo(player.getPlayer().getCurrentPosition() + MILLISECONDS_TO_SKIP);
                else
                    player.getPlayer().seekTo(player.getPlayer().getDuration());
            }
        } else {
            if (player.getPlayer() != null && player.getState().equals(PlayerUtils.STATES[2])) {
                if (player.getPlayer().getDuration() - player.getPlayer().getCurrentPosition() >= MILLISECONDS_TO_SKIP)
                    player.getPlayer().seekTo(player.getPlayer().getCurrentPosition() + MILLISECONDS_TO_SKIP);
                else
                    player.getPlayer().seekTo(player.getPlayer().getDuration());
            } else if (buffer.getPlayer() != null && buffer.getState().equals(PlayerUtils.STATES[2])) {
                if (buffer.getPlayer().getDuration() - buffer.getPlayer().getCurrentPosition() >= MILLISECONDS_TO_SKIP)
                    buffer.getPlayer().seekTo(buffer.getPlayer().getCurrentPosition() + MILLISECONDS_TO_SKIP);
                else
                    buffer.getPlayer().seekTo(buffer.getPlayer().getDuration());
            }
        }
    }

    public void goBackwards(View view){
        if(SocketHandler.isOffline()) {
            if (player.getPlayer() != null) {
                if(player.getPlayer().getCurrentPosition()>=MILLISECONDS_TO_SKIP)
                    player.getPlayer().seekTo(player.getPlayer().getCurrentPosition() - MILLISECONDS_TO_SKIP);
                else
                    player.getPlayer().seekTo(0);
            }
        }
        else{
            if (player.getPlayer() != null && player.getState().equals(PlayerUtils.STATES[2])) {
                if(player.getPlayer().getCurrentPosition()>=MILLISECONDS_TO_SKIP)
                    player.getPlayer().seekTo(player.getPlayer().getCurrentPosition() - MILLISECONDS_TO_SKIP);
                else
                    player.getPlayer().seekTo(0);
            } else if (buffer.getPlayer() != null && buffer.getState().equals(PlayerUtils.STATES[2])) {
                if(buffer.getPlayer().getCurrentPosition()>=MILLISECONDS_TO_SKIP)
                    buffer.getPlayer().seekTo(buffer.getPlayer().getCurrentPosition() - MILLISECONDS_TO_SKIP);
                else
                    buffer.getPlayer().seekTo(0);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        task2.cancel(true);
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(player != null)
                player.destroy();
            if(buffer != null)
                buffer.destroy();
        }
        return super.onKeyDown(keyCode, event);
    }
}