package com.dagame.example.journeyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Michael on 4/24/2018.
 */

public class Player {

    //Bitmap to get character from image
    private Bitmap bitmap;

    //desired width and height of Bitmap
    private int width;
    private int height;

    //Rectangle hit box for the character
    private Rect rect;

    //coordinates
    private int x;
    private int y;

    //motion speed of the character
    private int speed = 0;

    //constructor
    public Player(Context context) {

        // Getting bitmap from drawable resource
        // currently using default png
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pikachu);
        } catch (Exception e)
        {
            System.out.println("Could not get resource");
            e.printStackTrace();
        }

        width = 200;
        height = 200;

        //System.out.println("Bitmap width and height: " + width + " " + height);

        // initial coordinates
        x = 1600;   //orig 75
        y = GameView.getScreenHeight()/2 - height;     //orig 50
        speed = 1;

        /*
        Bitmap createScaledBitmap (Bitmap src,
        int dstWidth,
        int dstHeight,
        boolean filter)
        */

        // get a scaled bitmap (if the bitmap is too big or small)
        try {
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        catch (Exception e)
        {
            System.out.println("Could not get scaled bitmap");
            e.printStackTrace();
        }

        // Rectangle object for collision
        rect = new Rect(x, y, x + width, y + height);

    }

    // method to update coordinate of character
    public void update(){
        //updating x coordinate
        //x+=10;

        //adding top, left, bottom and right to the rect object
        rect.left = x;
        rect.top = y;
        rect.right = x + width; //bitmap.getWidth();
        rect.bottom = y + height; //bitmap.getHeight();
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

    public void setX(int a) { x = a; }

    public void setY(int b) { y = b; }

    public int getSpeed() {
        return speed;
    }

    public Rect getCollisionRect() {
        return rect;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

}
