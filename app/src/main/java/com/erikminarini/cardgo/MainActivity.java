package com.erikminarini.cardgo;

import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import cz.msebera.android.httpclient.Header;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    AsyncHttpClient client = new AsyncHttpClient();
    String TRIGGER = "http://10.5.5.9/gp/gpControl/command/shutter?p=1";
    String STOP = "http://10.5.5.9/gp/gpControl/command/shutter?p=0";
    String START = "http://10.5.5.9/gp/gpControl/execute?p1=gpStream&c1=restart";
    String VIDEO = "udp://10.5.5.100:8554";

    AliveTask t;


    //private String pathToFileOrUrl= "rtmp://204.107.26.252:8086/live/796.high.stream";
    private String pathToFileOrUrl= VIDEO;
    private VideoView mVideoView;

    Button trig;
    Button stop;
    Button go;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this))
            return;

        setContentView(R.layout.activity_main);

        mVideoView = (VideoView) findViewById(R.id.surface_view);

        if (pathToFileOrUrl == "") {
            Toast.makeText(this, "Please set the video path for your media file", Toast.LENGTH_LONG).show();
            return;
        } else {

            /*
             * Alternatively,for streaming media you can use
             * mVideoView.setVideoURI(Uri.parse(URLstring));
             */
            mVideoView.setVideoURI(Uri.parse(pathToFileOrUrl));
            //mVideoView.setVideoPath(pathToFileOrUrl);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }

        //final MediaPlayer mediaPlayer = new MediaPlayer();

        trig = (Button) findViewById(R.id.button2);
        trig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(START);

            }
        });

        stop = (Button) findViewById(R.id.button);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(START);

            }
        });

        go = (Button) findViewById(R.id.button3);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    startVideo(mediaPlayer, VIDEO);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                try {
                    keepAlive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void request(String target){
        client.get(target, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"

                Log.v("STATUS",String.valueOf(statusCode));
                Log.v("RESPONSE",responseBody.toString());
                Log.v("CASE","SUCCESS");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.v("CASE","FAILURE");
                Log.v("STATUS",String.valueOf(statusCode));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    /*public void startVideo(MediaPlayer mediaPlayer, String url) throws IOException {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        //mediaPlayer.setDisplay(<surface>);
        mediaPlayer.prepare(); // might take long! (for buffering, etc)
        mediaPlayer.start();
    }*/

    /*private void playVideo(byte[] vid) {
        try {
            // create temp file that will hold byte array
            File tempVid = File.createTempFile("live", "mp4", getCacheDir());
            tempVid.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempVid);
            fos.write(vid);
            fos.close();

            // Tried reusing instance of media player
            // but that resulted in system crashes...
            MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempVid);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }*/
    public void keepAlive() throws IOException {
        t = new AliveTask();
        t.execute();
    }
}
