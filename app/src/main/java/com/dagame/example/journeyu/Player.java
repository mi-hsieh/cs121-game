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

        // initial coordinates
        x = 75;
        y = 50;
        speed = 1;

        // Getting bitmap from drawable resource
        // currently using default png
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pikachu);

        /*
        Bitmap createScaledBitmap (Bitmap src,
        int dstWidth,
        int dstHeight,
        boolean filter)
        */

        width = 300;
        height = 300;

        // get a scaled bitmap (if the bitmap is too big or small)
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

        // Rectangle object for collision
        rect = new Rect(x, y, x + width, y + height);

    }

    // method to update coordinate of character
    public void update(){
        //updating x coordinate
        x+=10;

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

    public int getSpeed() {
        return speed;
    }

    public Rect getCollisionRect() {
        return rect;
    }

}
