package com.dagame.example.journeyu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Michael on 4/24/2018.
 */

public class GameView extends SurfaceView implements Runnable{

    // boolean variable to track if the game is playing or not
    // volatile = a field might be modified by multiple threads that are executing at the same time
    volatile boolean playing;

    // the game thread
    private Thread gameThread = null;

    // adding player to this class
    private Player player;

    // upButton
    private UpButton upButton;

    // array list of platforms
    private ArrayList<Tile> tiles = new ArrayList<Tile>();

    // amount to shift tiles by so they line up vertically
    int shiftY;

    // number of tiles
    int numTiles;

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

    //private static final String TAG = "Rectangle";
    private Rect rect2 = new Rect(1000, 50, 1300, 350);
    private boolean isHit = false;

    int time = 17;  // milliseconds

    /* The Canvas class holds the "draw" calls.
    To draw something, you need 4 basic components: A Bitmap to hold the pixels,
    a Canvas to host the draw calls (writing into the bitmap),
    a drawing primitive (e.g. Rect, Path, text, Bitmap),
    and a paint (to describe the colors and styles for the drawing).
     */

    // objects used for drawing
    private Paint paint;
    private Canvas canvas;
    /* We need a SurfaceHolder
    when we use Paint and Canvas in a thread*/
    private SurfaceHolder surfaceHolder;

    // class constructor
    public GameView(Context context) {
        super(context);

        // initialize player object
               player = new Player(context);

               upButton = new UpButton(context);
        // initialize drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        numTiles = 5;

        shiftY = 0;
        for (int i = 0; i < numTiles; i++)
        {
            Tile t = new Tile(context);
            tiles.add(t);
            t.shiftDown(shiftY);
            shiftY += t.getWidth();
            //spacing between tiles
            shiftY += 10;
        }

    }

    // Using Runnable, so need to implement run()
    @Override
    public void run() {
        while (playing) {
            // update the frame
            update();

            // draw the frame
            draw();

            // control
            control();
        }
    }

    private void update() {
        // update the player position
        player.update();

        // DETECT COLLISIONS HERE
        if (Rect.intersects(player.getCollisionRect(), rect2)) {

            // Intersects player bounding rectangle
            isHit = true;

            /*Log.d(TAG, "Intersection successful.");
            System.out.println("Player rect x and y: " + player.getCollisionRect().left
                    + " " + player.getCollisionRect().top);
            System.out.println("Test rect x and y: " + rect2.left
                    + " " + rect2.top);*/
        }
        else
        {
            isHit = false;
        }
    }

    private void draw() {
        // check if the drawing surface is valid
        // or we crash
        if (surfaceHolder.getSurface().isValid())
        {
            // lock the canvas ready to draw
            canvas = surfaceHolder.lockCanvas();
            // draw a background color for canvas
            // right now using transparent
            // we need PorterDuff.Mode.CLEAR to clear old bitmap drawings (will leave a streak otherwise)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            // we want the moving rectangle and bitmap to overlap the
            // static one, so draw static test rect first

            // draw test rectangle
            if (isHit)
            {
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
            }
            else
            {
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
            }
            canvas.drawRect(rect2, paint);
            // restore paint to default settings for bitmap
            paint.reset();

            // draw the tiles
            for (Tile t : tiles)
            {
                canvas.drawBitmap(
                        t.getBitmap(),
                        t.getX(),
                        t.getY(),
                        paint
                );
            }

            // draw the player
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint
            );

            //draw upButton
            canvas.drawBitmap(
                    upButton.getBitmap(),
                    upButton.getX(),
                    upButton.getY(),
                    paint
            );

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.argb(255, 26, 128, 182));
            // draw the corresponding player rectangle
            canvas.drawRect(player.getCollisionRect(), paint);

            // restore paint to default settings for next frame
            // paint.reset();

            // unlock the canvas = draw everything to the screen
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    /* This method will control the frames per seconds drawn.
    Here we are calling the delay method of Thread.
    This is currently making our frame rate around 60fps.
     */

    private void control() {
        try {
            // time in milliseconds, used to delay movement
            gameThread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* if the game is paused we shut
    the Thread down */

    public void pause() {
        /* when the game is paused
           set the variable to false */
        playing = false;
        try {
            // Parent thread must wait until the end of gameThread
            // stopping the thread
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        /* when the game is resumed
        start the thread again */
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if(motionEvent.getX() > 2100 && motionEvent.getY() > GameView.getScreenHeight() - 1200) {
                    player.setX(1300);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                //When the user releases the screen
                //do something here
                break;
            case MotionEvent.ACTION_BUTTON_PRESS:
                // when the screen is pressed
                // do something here
                break;
        }
        return true;
    }

    // public methods to get phone screen's width and height

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
