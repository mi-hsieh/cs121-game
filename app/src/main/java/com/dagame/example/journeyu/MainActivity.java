package com.dagame.example.journeyu;

//import android.graphics.Rect;
import android.app.Activity;
import android.content.Intent;
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

    int replaying = 0;

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
    // is also called when the activity is first started
    @Override
    protected void onResume()
    {

        /* int getIntExtra (String name,
                int defaultValue)

        Parameters

        name - The name of the desired item.

        defaultValue - the value to be returned if no value of the desired type is stored with the given name.

        Returns

        the value of an item that previously added with putExtra() or the default value if none was found. */

        Intent intent = getIntent();
        replaying = intent.getIntExtra("replaying", 0);
        System.out.println("Replaying is " + replaying);

        // replay = 0 false, replay = 1 true
        // by default replaying should be false
        if (replaying == 0) {
            super.onResume();
            gameView.resume();
        }
        else
        {
            /*gameView = new GameView(this);
            System.out.println("Started new game");
            setContentView(gameView);
            System.out.println("set new game view");
            replaying = 0;*/

            // this works, for some reason
            // same code as above
            super.onResume();
            gameView.resume();
        }
    }

}
