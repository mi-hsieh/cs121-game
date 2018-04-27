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


        /*
         public Rect (int left, int top, int right, int bottom)
         Create a new rectangle with the specified coordinates. Note: no range
         checking is performed, so the caller must ensure that left <= right and
         top <= bottom.

         Parameters
         left : The X coordinate of the left side of the rectangle
         top : The Y coordinate of the top of the rectangle
         right : The X coordinate of the right side of the rectangle
         bottom : The Y coordinate of the bottom of the rectangle

                */

        /*ImageView iv = findViewById(R.id.imageViewPika);
        ImageView iv2 = findViewById(R.id.imageViewSaur);

        System.out.println("Running.");

        Rect myViewRect = new Rect();
        myViewRect.set((int) iv.getX(), (int) iv.getY(),
                (int) (iv.getX() + iv.getWidth()), (int) (iv.getY() - iv.getHeight()));
        System.out.println((int) iv.getX() + " " +  (int) iv.getY() + " " +
                (int) (iv.getX() + iv.getWidth()) + " " +  (int) (iv.getY() - iv.getHeight()));
        iv.getDrawingRect(myViewRect);
        Log.d(TAG, "iv height: " + myViewRect.height());
        Log.d(TAG, "iv width: " + myViewRect.width());

        Rect otherViewRect1 = new Rect();
        iv2.getDrawingRect(otherViewRect1);

        Log.d(TAG, "iv2 height: " + otherViewRect1.height());
        Log.d(TAG, "iv2 width: " + otherViewRect1.width());

       if (Rect.intersects(myViewRect, otherViewRect1)) {
            // Intersects otherView1
            Log.d(TAG, "Intersection successful.");
        }*/

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
