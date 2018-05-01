package com.dagame.example.journeyu;

//import android.graphics.Rect;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

//for debugging
//import android.util.Log;

//import android.widget.ImageView;

// from https://www.simplifiedcoding.net/android-game-development-tutorial-1/
// http://gamecodeschool.com/android/coding-android-sprite-sheet-animations/

public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "Rectangle";

    int stamina = 5;
    int life = 1;
    int score = 0;

    // declare GameView (a view = base class for widgets and UI, handles drawing and events)
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // no longer using default XML file, activity_main.xml
        //setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //not needed
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // alternative way to hide Action Bar
        // getSupportActionBar().hide();

        // initialize the GameView object
        // our context is this activity (main activity)
        gameView = new GameView(this);
        // add the view to our content view
        setContentView(gameView);

    }

    // pause the game when activity is paused
    @Override
    protected void onPause()
    {
        super.onPause();
        gameView.pause();
    }

    // run the game when the activity is resumed
    @Override
    protected void onResume()
    {
        super.onResume();
        gameView.resume();
    }

}
