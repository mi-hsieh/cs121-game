package com.dagame.example.journeyu;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
//import android.util.Log;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Michael on 4/24/2018.
 */

public class GameView extends SurfaceView implements Runnable{

    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*           Declarations          *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////

    // boolean variable to track if the game is playing or not
    // volatile = a field might be modified by multiple threads that are executing at the same time, for scope
    // in case we're using multiple threads, the variable is visible and able to be updated by other threads
    volatile boolean playing;

    Bitmap background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bg1), getScreenWidth(), getScreenHeight(), true);

    //-------------------------------------------------------------------------------------  Game Thread   ---------------------------//

    // the game thread
    private Thread gameThread = null;

    int time = 17;  // milliseconds

    // media player
    MediaPlayer medPlay;

    //-------------------------------------------------------------------------------------     Player     ----------------------------//

    // adding player to this class
    private Player player;

    // movement smoothing for character
    // is equal to 0 or 1 depending on if touch event occurred
    // used in if-statement functionality in update() to iterate through the movement animation
    int move = 0;
    int moveDown = 0;
    int moveUp = 0;
    int charFrameCount = -1;
    int numStamina;    // goes down on movement touch event

    //player animation
    public Rect PlayerAniFrame = new Rect(0, 0, 370, 236); // sliding window of sprite sheet
    int aniFrameCol = 0;    // coordinates of sliding window
    int aniFrameRow = 0;

    //----------------------------------------------------------------------------------      Buttons      ------------------------------//
    private UpButton upButton;
    private DownButton downButton;

    //----------------------------------------------------------------------------------   Tiles/Stamina   -----------------------------//
    private ArrayList<Tile> tiles = new ArrayList<Tile>();
    private ArrayList<Stamina> stamina = new ArrayList<Stamina>();

    // amount to shift tiles by so they line up
    int shiftY;
    int shiftX;

    // number of tiles
    int numTiles;

    //----------------------------------------------------------------------------------   Obstacles/Power-ups   -----------------------------//
    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    int numObstacles;

    // the current column - includes empty columns
    int currentCol = 0;

    // the current "wave" - segment
    int wave;

    // movement smoothing for obstacle
    // because not based on onTouch event, we use a a timer that activates movement when obsTimer%35
    // similar nested if-statement iteration, as for character
    int obsTimer = 0;
    int obsFrameCount = 0;
    int obsFrameStart = 0;

    // create new power-up
    private SideSmash sideSmash;

    private UpPipe upPipe;

    int numPowerUps;

    private boolean canSmash = false;

    // testing out pipe functionality
    int pipeKey = 0;

    // See Note 1 in Project Notes.

    // for debugging
    // private static final String TAG = "GameView";

    private boolean isHit = false;

    //-----------------------------------------------------------------------------  Setting up drawing construct  --------------//

    // Note 2

    // objects used for drawing
    private Paint paint;
    private Canvas canvas;
    /* We need a SurfaceHolder
    when we use Paint and Canvas in a thread*/
    private SurfaceHolder surfaceHolder;

    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*            GameView             *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////

    // class constructor
    public GameView(Context context) {
        super(context);

        // initialize music
        medPlay = MediaPlayer.create(context, R.raw.rolem_the_white_kitty);
        medPlay.setLooping(true);

        // initialize player object
        player = new Player(context);

        upButton = new UpButton(context);

        downButton = new DownButton(context);

        // initialize drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        itemInitialize(context);

    }

    // Note 3


    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*               Run               *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
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

    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*              Update             *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////

    private void update() {

        playerAnimate();  //update player's animation frame

        // update the player position
        // player.update();

        obstacleUpdate(); //update obstacle position

        obstacleCollisionCheck(); //check for obstacle collision

        if(move==1){
            movePlayer();
        }

        player.update();    // update the player position
    }

    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*              Draw               *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////

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

            canvas.drawBitmap(background, 0, 0, paint);

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

            // draw the obstacles
            for (Obstacle ob : obstacles) {
                    canvas.drawBitmap(
                            ob.getBitmap(),
                            ob.getX(),
                            ob.getY(),
                            paint
                    );
            }

            // draw the power-ups
            if (sideSmash != null) {
                canvas.drawBitmap(
                        sideSmash.getBitmap(),
                        sideSmash.getX(),
                        sideSmash.getY(),
                        paint
                );
            }

            if (upPipe != null) {
                canvas.drawBitmap(
                        upPipe.getBitmap(),
                        upPipe.getX(),
                        upPipe.getY(),
                        paint
                );
            }

            /* collision rectangles for debugging
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.MAGENTA);
            Obstacle ob;
            for (int i = 0; i < numObstacles; i++)
            {
                // draw the corresponding obstacle rectangle
                ob = obstacles.get(i);
                canvas.drawRect(ob.getCollisionRect(), paint);
            }

            // draw the corresponding power-up collision rectangle
            if (sideSmash != null) {
                canvas.drawRect(sideSmash.getCollisionRect(), paint);
            }

            if (upPipe != null) {
                canvas.drawRect(upPipe.getCollisionRect(), paint);
            } */

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

    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*       Pre*** Thread Functions      *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////

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
        medPlay.pause();
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
        // medPlay.start();
        gameThread = new Thread(this);
        gameThread.start();
    }

    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*            TouchEvent           *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (numStamina > 0) {
                    if (pipeKey==0) {
                        if (motionEvent.getX() > 2100 && motionEvent.getY() < GameView.getScreenHeight() - 1200) {
                            // don't move if player would be above top platform, also assuming platforms initialized
                            if (player.getY() > tiles.get(0).getY()) {
                                if (move == 0) {
                                    if ((charFrameCount == -1) && (moveUp == 0)) {
                                        moveUp = 1;
                                        charFrameCount = 0;

                                        numStamina--;
                                        stamina.remove(stamina.size() - 1);
                                    }
                                    move = 1;
                                }
                            }
                        }

                        if (motionEvent.getX() > 2100 && motionEvent.getY() > GameView.getScreenHeight() - 400) {
                            // don't move if player would be below bottom platform
                            if (player.getY() < tiles.get(tiles.size() - 1).getY()) {
                                if (move == 0) {
                                    if ((charFrameCount == -1) && (moveDown == 0)) {
                                        moveDown = 1;
                                        charFrameCount = 0;

                                        numStamina--;
                                        stamina.remove(stamina.size() - 1);
                                    }
                                    move = 1;
                                }
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

    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*              Player             *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////

    public void movePlayer(){
        /*if(moveUp==1){
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
                move=0;
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
                move=0;
            }
        }*/

        //520 - 320, 320 - 120 norm
        //506 - 134 pipe

        if(moveUp==1){
            charFrameCount++;
            if(pipeKey==0) {
                if (charFrameCount <= 5) {
                    player.setY((player.getY() - 14));
                }
                if (charFrameCount > 5 && charFrameCount <= 10) {
                    player.setY((player.getY() - 18));
                }
                if (charFrameCount > 10 && charFrameCount <= 15) {
                    player.setY((player.getY() - 8));
                }
                if (charFrameCount == 16) {
                    charFrameCount = -1;
                    moveUp = 0;
                    move = 0;
                }
            } else {
                if (charFrameCount <= 5) {
                    player.setY((player.getY() - 28));
                }
                if (charFrameCount > 5 && charFrameCount <= 10) {
                    player.setY((player.getY() - 36));
                }
                if (charFrameCount > 10 && charFrameCount <= 15) {
                    player.setY((player.getY() - 16));
                }
                if (charFrameCount == 16) {

                    /*The first movement when pipeKey == 0,
                    player.setY((player.getY() - 14));
                    will still be called the instant before pipeKey == 1,
                    that is, the instant before colliding with the pipe, so reset
                    movement by that same amount */

                    player.setY((player.getY() - 14));

                    charFrameCount = -1;
                    moveUp = 0;
                    move = 0;
                    pipeKey=0;
                }
            }
        }

        if(moveDown==1){
            charFrameCount++;
            if(pipeKey==0) {
                if (charFrameCount <= 5) {
                    player.setY((player.getY() + 14));
                }
                if (charFrameCount > 5 && charFrameCount <= 10) {
                    player.setY((player.getY() + 18));
                }
                if (charFrameCount > 10 && charFrameCount <= 15) {
                    player.setY((player.getY() + 8));
                }
                if (charFrameCount == 16) {
                    charFrameCount = -1;
                    moveDown = 0;
                    move = 0;
                }
            } else {
                if (charFrameCount <= 5) {
                    player.setY((player.getY() + 28));
                }
                if (charFrameCount > 5 && charFrameCount <= 10) {
                    player.setY((player.getY() + 36));
                }
                if (charFrameCount > 10 && charFrameCount <= 15) {
                    player.setY((player.getY() + 16));
                }
                if (charFrameCount == 16) {
                    charFrameCount = -1;
                    moveDown = 0;
                    move = 0;
                    pipeKey=0;
                }
            }
        }

    }

    public void playerAnimate(){
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
    }

    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*            Obstacle             *///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////*                                 *///////////////////////////////////////

    // includes Obstacle, Power-ups, Tile
    public void itemInitialize(Context context) {
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

        numPowerUps = 2;

        // initialize power-ups
        sideSmash = new SideSmash(context);
        sideSmash.setY(pos0);

        upPipe = new UpPipe(context);
        upPipe.setY(pos2);

        wave = 1;

    }

    public void obstacleUpdate(){
        obsTimer++;

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

            // move the obstacles
            for (Obstacle ob : obstacles) {
                /* 3 ifs code moved to the obstacle classes, in update
                */

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

            // move the power-up
            if (currentCol >= 5 && sideSmash != null)
            {
                sideSmash.update(obsFrameCount);
            }
            if (currentCol == 25 && canSmash)
            {
                // lose power-up
                System.out.println("No more smash :(");
                canSmash = false;
            }

            if (currentCol >= 14 && upPipe != null)
            {
                upPipe.update(obsFrameCount);
            }

        }
    }

    // includes Obstacle and power-ups
    public void obstacleCollisionCheck(){
        // obstacle collision
        Obstacle ob;

        for (int i = 0; i < obstacles.size(); i++)
        {
            ob = obstacles.get(i);
            if (Rect.intersects(player.getCollisionRect(), ob.getCollisionRect()) )
            {
                /*if (stamina.size() > 0) {
                        stamina.remove(stamina.get(stamina.size() - 1));
                        numStamina--;
                }

                if(numStamina==0) {
                    playing = false;
                }*/

                // can smash cones from the side
                if (canSmash && ob instanceof Obstacle1 && player.getCollisionRect().top != ob.getCollisionRect().top && player.getCollisionRect().left == ob.getCollisionRect().left)
                {
                    System.out.println("Smash!");
                    System.out.println("Obstacle at index " + i + " collided. Removing.");
                    obstacles.remove(ob);
                    numObstacles--;
                }
                /*else if(player.getCollisionRect().top != ob.getCollisionRect().top && player.getCollisionRect().left == ob.getCollisionRect().left) {
                    pipeKey=1;
                }*/
                else {

                    // isHit stays true after last object is removed
                    System.out.println("Hit!");
                    isHit = true;
                    System.out.println("Obstacle at index " + i + " collided. Removing.");
                    obstacles.remove(ob);
                    numObstacles--;
                    Intent intent = new Intent(getContext(), GameOver.class);
                    getContext().startActivity(intent);
                    // set playing to false, Android should stop thread when required
                    playing = false;
                    medPlay.pause();
                }
            }
            else
            {
                isHit = false;
            }
        }

        // power-up collision
        if (sideSmash != null && Rect.intersects(player.getCollisionRect(), sideSmash.getCollisionRect()) )
        {
            // canSmash stays true after last object is removed
            System.out.println("Smash ready!");
            canSmash = true;
            System.out.println("Power-up collided. Removing.");
            sideSmash = null;
            numPowerUps--;
        }

        // power-up collision
        if (upPipe != null && Rect.intersects(player.getCollisionRect(), upPipe.getCollisionRect()) )
        {
            if(player.getCollisionRect().top != upPipe.getCollisionRect().top && player.getCollisionRect().left == upPipe.getCollisionRect().left) {
                pipeKey=1;
                System.out.println("Pipe. Going up.");
            }
            else {
                System.out.println("Failed to get pipe or pipe out of range.");
                System.out.println("Power-up collided. Removing.");
                upPipe = null;
                numPowerUps--;
            }
        }

        // when obstacles reach the end of the screen, they are destroyed
        // looping backwards because remove() removes object at end of array list
        for (int i=obstacles.size()-1; i>=0; i--)
        {
            // assuming tiles are initialized
            if (obstacles.get(i).getX() > tiles.get(0).getX()-obstacles.get(i).getWidth())
            {
                System.out.println("Tiles reached. Destroying obstacle at index " + i);
                obstacles.remove(i);
                numObstacles--;
            }
        }

        // when power-ups reach the tiles, they are destroyed
        if (sideSmash != null && sideSmash.getX() > tiles.get(0).getX()-sideSmash.getWidth())
        {
            System.out.println("Tiles reached. Destroying power-up.");
            sideSmash = null;
            numPowerUps--;
        }

        if (upPipe != null && upPipe.getX() > tiles.get(0).getX()-upPipe.getWidth())
        {
            System.out.println("Tiles reached. Destroying power-up.");
            upPipe = null;
            numPowerUps--;
        }
    }
}
