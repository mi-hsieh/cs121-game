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
import java.util.Random;

/**
 * Created by Michael on 4/24/2018.
 */

public class GameView extends SurfaceView implements Runnable{

    // boolean variable to track if the game is playing or not
    // volatile = a field might be modified by multiple threads that are executing at the same time, for scope
    // in case we're using multiple threads, the variable is visible and able to be updated by other threads
    volatile boolean playing;

    // the game thread
    private Thread gameThread = null;

    int time = 17;  // milliseconds

    // adding player to this class
    private Player player;

    // upButton
    private UpButton upButton;

    // downButton
    private DownButton downButton;

    // array list of platforms, platforms = tiles
    private ArrayList<Tile> tiles = new ArrayList<Tile>();

    private ArrayList<Stamina> stamina = new ArrayList<Stamina>();

    // amount to shift tiles by so they line up vertically
    int shiftY;

    int shiftX;

    // number of tiles
    int numTiles;

    // adding Obstacle
    private ArrayList<Obstacle1> obstacles = new ArrayList<Obstacle1>();

    int numObstacles;

    int numStamina;


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
    // a test rectangle
    private Rect rect2 = new Rect(1000, 50, 1300, 350);
    private boolean isHit = false;

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

        downButton = new DownButton(context);

        // initialize drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        numTiles = 5;

        numStamina = 10;

        shiftX = 0;
        for (int i = 0; i < numStamina; i++)
        {
            Stamina s = new Stamina(context);
            stamina.add(s);
            s.shiftRight(shiftX);
            shiftX += s.getWidth();
            //spacing between staminas
            //shiftX += 10;
        }

        shiftY = 0;
        for (int i = 0; i < numTiles; i++)
        {
            Tile t = new Tile(context);
            tiles.add(t);
            t.shiftDown(shiftY);
            shiftY += t.getWidth();
            //spacing between tiles
            //shiftY += 10;
        }

        Random r = new Random();
        int randInt = 0;

        numObstacles = 5;
        for (int i = 0; i < numObstacles; i++)
        {
            Obstacle1 ob = new Obstacle1(context);

            // option 1
            // random obstacle y positions
            /* gives a random integer between min (inclusive) and max (exclusive)
            // int i1 = r.nextInt(max - min + 1) + min; */
            // ex. randInt = r.nextInt(80 - 65) + 65;
            // randInt = r.nextInt((GameView.getScreenHeight()-ob.getHeight()) - 0 + 1) + 0;

            // option 2
            // the obstacles are aligned with the tiles (tile 0 is the top tile)
            randInt = tiles.get(i).getY();
            ob.setY(randInt);
            obstacles.add(ob);
        }

    }


    /* A class that implements Runnable can run without subclassing Thread by
    instantiating a Thread instance and passing itself in as the target. In most cases, the
    Runnable interface should be used if you are only planning to
    override the run() method and no other Thread methods.
     */

    /* When an object implementing interface Runnable is used to create a thread,
    starting the thread causes the object's run method to be called
    in that separately executing thread.
     */

    /* run()
    If this thread was constructed using a separate Runnable run object, then
    that Runnable object's run method is called; otherwise, this method does
    nothing and returns.
     */

    // Using Runnable, so need to implement run()
    // From documentation: The class must define a method of no arguments called run.
    // calls three methods also defined in this class
    @Override
    public void run() {
        while (playing) {
            // update the frame
            update();

            // draw the frame
            draw();

            // control the frame rate
            control();
        }
    }

    private void update() {
        // update the player position
        player.update();

        // update obstacle position
        for (Obstacle1 ob : obstacles) {
            if (ob.isVisible()) {
                ob.update();
            }
        }

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

        for (Obstacle1 ob : obstacles)
        {
            if (Rect.intersects(player.getCollisionRect(), ob.getCollisionRect()) && ob.isVisible())
            {
                isHit = true;
            }
        }

        // when obstacles reach the end of the screen, they are destroyed
        // looping backwards because remove() removes object at end of array list
        for (int i=obstacles.size()-1; i>=0; i--)
        {
            if (obstacles.get(i).getX() >= GameView.getScreenWidth())
            {
                obstacles.get(i).setVisible(false);
                obstacles.remove(i);
            }
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
            // paint.reset();

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

            //draw downButton
            canvas.drawBitmap(
                    downButton.getBitmap(),
                    downButton.getX(),
                    downButton.getY(),
                    paint
            );
            //draw stamina
            for (Stamina s : stamina)
            canvas.drawBitmap(
                    s.getBitmap(),
                    s.getX(),
                    s.getY(),
                    paint
            );


            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.argb(255, 26, 128, 182));
            // draw the corresponding player rectangle
            canvas.drawRect(player.getCollisionRect(), paint);

            for (Obstacle1 ob : obstacles) {
                if (ob.isVisible()) {
                    canvas.drawBitmap(
                            ob.getBitmap(),
                            ob.getX(),
                            ob.getY(),
                            paint
                    );
                }
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.MAGENTA);
            for (Obstacle1 ob : obstacles) {
                // draw the corresponding obstacle rectangle
                if (ob.isVisible()) {
                    canvas.drawRect(ob.getCollisionRect(), paint);
                }
            }

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
                if(motionEvent.getX() > 2100 && motionEvent.getY() < GameView.getScreenHeight() - 1200) {
                    player.setY(player.getY() - 200);
                }
                if(motionEvent.getX() > 2100 && motionEvent.getY() > GameView.getScreenHeight() - 400) {
                    player.setY(player.getY() + 200);
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
