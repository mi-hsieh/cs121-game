package com.dagame.example.journeyu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    // volatile = a field might be modified by multiple threads that are executing at the same time, for scope
    // in case we're using multiple threads, the variable is visible and able to be updated by other threads
    volatile boolean playing;

    Bitmap background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bg1), getScreenWidth(), getScreenHeight(), true);

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

    // adding obstacles
    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

    int numObstacles;

    // the current column - includes empty columns
    int currentCol = 0;

    // the current "wave" - segment
    int wave;

    int numStamina;

    // movement smoothing for character
    // is equal to 0 or 1 depending on if touch event occurred
    // used in if-statement functionality to trigger the movement animation in update()
    int moveDown = 0;
    int moveUp = 0;
    int charFrameCount = -1;

    // animation count for character
    int aniFrameCol = 0;
    int aniFrameRow = 0;
    int aniFrameDelay = 0;

    // movement smoothing for obstacle
    // because not based on onTouch event, we use a a timer that activates movement when obsTimer%35
    int obsTimer = 0;
    int obsFrameCount = 0;
    int obsFrameStart = 0;

    public Rect PlayerAniFrame = new Rect(0, 0, 370, 236);

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
    // private Rect rect2 = new Rect(1000, 50, 1300, 350);

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
            //spacing between stamina
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

        // positions of obstacles based on tile positions
        // 0 - 4 for the 5 tiles, from top to bottom
        int pos0 = tiles.get(0).getY();
        int pos1 = tiles.get(1).getY();
        int pos2 = tiles.get(2).getY();
        int pos3 = tiles.get(3).getY();
        int pos4 = tiles.get(4).getY();

        numObstacles = 6;

        // initialize wave 1

        // IDs start from 1

        Obstacle1 ob1;  // cone
        ob1 = new Obstacle1(context);
        ob1.setY(pos2);
        obstacles.add(ob1);
        obstacles.get(0).setID(1);

        Obstacle2 ob2;  // brick
        ob2 = new Obstacle2(context);
        ob2.setY(pos1);
        obstacles.add(ob2);
        obstacles.get(1).setID(2);
        // add another brick
        ob2 = new Obstacle2(context);
        ob2.setY(pos3);
        obstacles.add(ob2);
        obstacles.get(2).setID(3);

        // add 2 more cones
        ob1 = new Obstacle1(context);
        ob1.setY(pos0);
        obstacles.add(ob1);
        obstacles.get(3).setID(4);
        ob1 = new Obstacle1(context);
        ob1.setY(pos4);
        obstacles.add(ob1);
        obstacles.get(4).setID(5);

        Obstacle3 ob3;  // wall
        ob3 = new Obstacle3(context);
        ob3.setY(pos1);
        obstacles.add(ob3);
        obstacles.get(5).setID(6);

        wave = 1;


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

        obsTimer++;

        // iterates through frames of sprite sheet
        if (aniFrameRow == 0) {
            aniFrameCol++;
            aniFrameRow++;
        } else {
            aniFrameCol++;
        }

        // check if sprite location is within bounds of sprite sheet
        if (aniFrameCol > 5) {
            aniFrameRow++;
            aniFrameCol = 1;
            PlayerAniFrame.top += 236;
            PlayerAniFrame.bottom += 236;
        }
        if (aniFrameRow == 9 && aniFrameCol == 5) {
            aniFrameRow = 1;
            aniFrameCol = 1;
        }
        if (aniFrameRow == 1) {
            PlayerAniFrame.top = 0;
            PlayerAniFrame.bottom = 236;
        }
        if (aniFrameCol == 1) {
            PlayerAniFrame.left = 0;
            PlayerAniFrame.right = 370;
        }
        if (aniFrameCol > 1) {
            PlayerAniFrame.left += 370;
            PlayerAniFrame.right += 370;
        }

        // update obstacle position
        if(obsTimer%35==0){
            obsFrameStart=1;
            obsFrameCount=0;

            // we've reached the next column
            currentCol++;
        }
        if(obsFrameStart==1){
            if(obsTimer==25){
                obsTimer=0;
            }
            obsFrameCount++;
            for (Obstacle ob : obstacles) {
                /* moved to the obstacle classes, in update
                if(obsFrameCount<=5){
                    ob.setX((ob.getX() + 14));
                }
                if(obsFrameCount>5 && obsFrameCount<=10){
                    ob.setX((ob.getX() + 20));
                }
                if(obsFrameCount>10 && obsFrameCount<=15){
                    ob.setX((ob.getX() + 6));
                }*/

                // Note the extra parameter
                if (currentCol >= 0 && ob.getID() == 1)
                {
                    ob.update(obsFrameCount);
                }
                if (currentCol >= 4 && ob.getID() == 2)
                {
                    ob.update(obsFrameCount);
                }
                if (currentCol >= 4 && ob.getID() == 3)
                {
                    ob.update(obsFrameCount);
                }
                if (currentCol >= 7 && ob.getID() == 4)
                {
                    ob.update(obsFrameCount);
                }
                if (currentCol >= 7 && ob.getID() == 5)
                {
                    ob.update(obsFrameCount);
                }
                if (currentCol >= 10 && ob.getID() == 6)
                {
                    ob.update(obsFrameCount);
                }

                if(obsFrameCount==16){
                    obsFrameStart=0;
                }
            }

        }

        // DETECT COLLISIONS HERE

        /* Old code for test rectangle
        if (Rect.intersects(player.getCollisionRect(), rect2)) {

            // Intersects player bounding rectangle
            isHit = true;

        }
        else
        {
            isHit = false;
        }
        */

        Obstacle ob;

        for (int i = 0; i < obstacles.size(); i++)
        {
            ob = obstacles.get(i);
            if (Rect.intersects(player.getCollisionRect(), ob.getCollisionRect()) )
            {
                /*if (stamina.size() > 0) {
                        stamina.remove(stamina.get(stamina.size() - 1));
                        numStamina--;
                }*/

                // test rectangle, if drawn, should flash green briefly
                // isHit stays true after last object is removed
                System.out.println("Hit!");
                isHit = true;
                System.out.println("Obstacle at index " + i + " collided. Removing.");
                obstacles.remove(ob);
                numObstacles--;
            }
            else
            {
                isHit = false;
            }
        }

        // when obstacles reach the end of the screen, they are destroyed
        // looping backwards because remove() removes object at end of array list
        for (int i=obstacles.size()-1; i>=0; i--)
        {
            if (obstacles.get(i).getX() >= GameView.getScreenWidth())
            {
                //obstacles.get(i).setVisible(false);
                System.out.println("End of screen reached. Destroying obstacle at index " + i);
                obstacles.remove(i);
                numObstacles--;
            }
        }

        if(moveUp==1){
            charFrameCount++;
            if(charFrameCount<=5){
                player.setY((player.getY() - 14));
            }
            if(charFrameCount>5 && charFrameCount<=10){
                player.setY((player.getY() - 18));
            }
            if(charFrameCount>10 && charFrameCount<=15){
                player.setY((player.getY() - 8));
            }
            if(charFrameCount==16){
                charFrameCount=-1;
                moveUp=0;
            }
        }

        if(moveDown==1){
            charFrameCount++;
            if(charFrameCount<=5){
                player.setY((player.getY() + 14));
            }
            if(charFrameCount>5 && charFrameCount<=10){
                player.setY((player.getY() + 18));
            }
            if(charFrameCount>10 && charFrameCount<=15){
                player.setY((player.getY() + 8));
            }
            if(charFrameCount==16){
                charFrameCount=-1;
                moveDown=0;
            }
        }

        player.update();
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

            // random colors for background
            //canvas.drawColor(Color.argb(255, 255,20,147));    // hot pink
            //canvas.drawColor(Color.argb(255, 255,218,185));   // peach
            //canvas.drawColor(Color.argb(255, 0,191,255));   // deep sky blue
            //canvas.drawColor(Color.BLACK);    // black
            //canvas.drawColor(Color.argb(255, 0,0,128));   // navy blue

            canvas.drawBitmap(background, 0, 0, paint);

            // we want the moving rectangle and bitmap to overlap the
            // static one, so draw static test rect first

            /*
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
            */

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
                    PlayerAniFrame,
                    player.getCollisionRect(),
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

            for (Obstacle ob : obstacles) {
                    canvas.drawBitmap(
                            ob.getBitmap(),
                            ob.getX(),
                            ob.getY(),
                            paint
                    );
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.MAGENTA);
            Obstacle ob;
            for (int i = 0; i < numObstacles; i++)
            {
                // draw the corresponding obstacle rectangle
                ob = obstacles.get(i);
                canvas.drawRect(ob.getCollisionRect(), paint);
            }

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
                    // don't move if player would be above top platform, also assuming platforms initialized
                    if (player.getY() > tiles.get(0).getY()) {
                        if (numStamina > 0) {
                            //player.setY(player.getY() - 200); // old code for movement
                            if((charFrameCount==-1)&&(moveUp==0)){
                                moveUp = 1;
                                charFrameCount = 0;

                                numStamina--;
                                stamina.remove(stamina.size() - 1);
                            }
                        }
                    }
                }

                if(motionEvent.getX() > 2100 && motionEvent.getY() > GameView.getScreenHeight() - 400) {
                    // don't move if player would be below bottom platform
                    if (player.getY() < tiles.get(tiles.size()-1).getY()) {
                        if (numStamina > 0) {
                            //player.setY(player.getY() + 200);
                            if((charFrameCount==-1)&&(moveDown==0)){
                                moveDown = 1;
                                charFrameCount = 0;

                                numStamina--;
                                stamina.remove(stamina.size() - 1);
                            }

                        }
                    }
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
