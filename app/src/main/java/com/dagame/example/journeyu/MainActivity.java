package com.dagame.example.journeyu;

//import android.graphics.Rect;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;


//for debugging
//import android.util.Log;

//import android.widget.ImageView;

// from https://www.simplifiedcoding.net/android-game-development-tutorial-1/
// http://gamecodeschool.com/android/coding-android-sprite-sheet-animations/

public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "Rectangle";

    int replaying = 0;

    // declare GameView (a view = base class for widgets and UI, handles drawing and events)
    private GameView gameView;

    // boolean shouldn't be needed since gameView always null when game over, kept it just in case
    boolean playingGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.playButton);
        b.setVisibility(View.INVISIBLE);*/

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //not needed
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // alternative way to hide Action Bar
        // getSupportActionBar().hide();

        // initialize the GameView object
        // our context is this activity (main activity)
        /*gameView = new GameView(this);

        // add the view to our content view
        setContentView(gameView);*/
        setContentView(R.layout.activity_main);

    }

    // pause the game when activity is paused
    @Override
    protected void onPause()
    {
        super.onPause();
        if (gameView != null) {

            gameView.pause();
        }
    }

    // run the game when the activity is resumed
    // is also called when the activity is first started
    @Override
    protected void onResume() {
        // Note 4

        /*Intent intent = getIntent();
        replaying = intent.getIntExtra("replaying", 0);
        System.out.println("Replaying is " + replaying);

        // replay = 0 false, replay = 1 true
        // by default replaying should be false
        if (replaying == 0) {
                super.onResume();
                if (gameView != null) {
                    gameView.resume();
                }
        }
        else
        {
            // trying to create a new game view will crash the game

            // this works for some reason
            // same code as above
            //* super.onResume();
            //* gameView.resume();
            // setContentView(R.layout.activity_main);
        }*/
        super.onResume();
        System.out.println("onResume()");
        // setContentView(R.layout.activity_main);
        if (gameView != null && playingGame) {
            gameView.resume();
            System.out.println("resuming game");
        }
    }

    // Called when the user taps the Play button
    public void onPlayClicked(View view) {

        Intent intent = getIntent();
        replaying = intent.getIntExtra("replaying", 0);
        System.out.println("Replaying is " + replaying);

        if (replaying == 0) {
            // initialize the GameView object
            // our context is this activity (main activity)
            gameView = new GameView(this);

            // add the view to our content view
            setContentView(gameView);

            //super.onResume();
            gameView.resume();
            System.out.println("starting game");
            playingGame = true;
        }
        else
        {
            //super.onResume();
                gameView = new GameView(this);
                setContentView(gameView);
                gameView.resume();
                System.out.println("creating new game");
                playingGame = true;
        }

    }
}
