package com.erikminarini.cardgo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.*;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    AsyncHttpClient client = new AsyncHttpClient();
    String TRIGGER = "http://10.5.5.9/gp/gpControl/command/shutter?p=1";
    String STOP = "http://10.5.5.9/gp/gpControl/command/shutter?p=0";
    String START = "http://10.5.5.9/gp/gpControl/execute?p1=gpStream&c1=restart";
    String VIDEO = "rtp://10.5.5.9:8554 ";

    Button trig;
    Button stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayer mediaPlayer = new MediaPlayer();

        trig = (Button) findViewById(R.id.button);
        trig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(START);
            }
        });

        stop = (Button) findViewById(R.id.button2);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startVideo(mediaPlayer, VIDEO);
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

    public void startVideo(MediaPlayer mediaPlayer, String url) throws IOException {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare(); // might take long! (for buffering, etc)
        mediaPlayer.start();
    }
}
