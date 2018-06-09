package com.dagame.example.journeyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

/**
 * Created by Michael on 4/24/2018.
 */

public class Player {

    //Bitmap to get character from image
    private Bitmap bitmap;

    //desired width and height of Bitmap
    private int width;
    private int height;

    //Rectangle animation box for the character
    private Rect rect;

    /*added a new rectangle for smaller collision area*/
    //Rectangle hit box for the character
    private Rect collisonRect;

    //coordinates
    private int x;
    private int y;

    //motion speed of the character
    private int speed = 0;

    //constructor
    public Player(Context context) {

        // Getting bitmap from drawable resource
        try {
            //BitmapDrawable drawable = (BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.icecream_ball_mintchoco, null);
            /*BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.icecream_ball_mintchoco);
            bitmap = drawable.getBitmap();*/
            // bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icecream_ball_mintchoco);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icecream_flying_03_spritesheet, options);
        } catch (Exception e)
        {
            System.out.println("Could not get resource");
            e.printStackTrace();
        }

        width = 200;
        height = 200;

        //System.out.println("Bitmap width and height: " + width + " " + height);

        // initial coordinates
        x = 1600;
        y = GameView.getScreenHeight()/2 - height;
        speed = 1;

        /*
        Bitmap createScaledBitmap (Bitmap src,
        int dstWidth,
        int dstHeight,
        boolean filter)
        */

        // get a scaled bitmap (if the bitmap is too big or small)
        /*try {
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        catch (Exception e)
        {
            System.out.println("Could not get scaled bitmap");
            e.printStackTrace();
        }*/

        // Rectangle object for animation
        rect = new Rect(x, y, x + 314, y + height);

        // Rectangle object for collision
        collisonRect = new Rect(x, y, x + width, y + height);

    }

    // method to update coordinate of character
    public void update(){
        //updating x coordinate
        //x+=10;

        //adding top, left, bottom and right to the rect object
        rect.left = x;
        rect.top = y;
        rect.right = x + 314; //bitmap.getWidth();
        rect.bottom = y + height; //bitmap.getHeight();

        //adding top, left, bottom and right to the collision rect object
        collisonRect.left = x;
        collisonRect.top = y;
        collisonRect.right = x + width;
        collisonRect.bottom = y + height;
    }

    // access methods

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int a) { x = rect.left = a; rect.right = x + width; }

    public void setY(int b) { y = rect.top = b; rect.bottom = y + height; }

    public int getSpeed() {
        return speed;
    }

    public Rect getAnimationRect() {
        return rect;
    }

    public Rect getCollisionRect() { return collisonRect; }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

}
