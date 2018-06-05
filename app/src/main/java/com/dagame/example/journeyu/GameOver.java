package com.dagame.example.journeyu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.media.MediaPlayer;


public class GameOver extends AppCompatActivity {

    //add gameover music
    MediaPlayer sndgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        sndgo = MediaPlayer.create(GameOver.this, R.raw.gameover);
        sndgo.setLooping(true);
        sndgo.start();

    }

    @Override
    protected void onPause(){
        super.onPause();
        sndgo.release();
        finish();

    }
    /* Called when the user taps the Send button */
    public void onReplayClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        // replay = 0 false, replay = 1 true
        int replaying = 1;
        intent.putExtra("replaying", replaying);
        //startActivity(intent);

        //pop the activity off the stack
        Intent i = new Intent(GameOver.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        /*Toast.makeText(GameOver.this, "Replaying.",
                Toast.LENGTH_LONG).show();*/
    }
}
