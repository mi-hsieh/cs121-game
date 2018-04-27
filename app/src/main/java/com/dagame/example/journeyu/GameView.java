package com.dagame.example.journeyu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
//import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

        // initialize drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

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

            isHit = true;

            // Intersects player bounding rectangle
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
            // right now using transparent, a solid color example is Color.argb(255, 26, 128, 182)
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

            // draw the player
            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint
            );


            paint.setStyle(Paint.Style.STROKE);
            // previously Color.CYAN
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
            // stopping the thread
            gameThread.join();
        } catch (InterruptedException e) {
            // nothing here
        }
    }

    public void resume() {
        /* when the game is resumed
        start the thread again */
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
