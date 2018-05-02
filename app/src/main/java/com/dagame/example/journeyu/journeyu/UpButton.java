package com.dagame.example.journeyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Michael on 4/24/2018.
 */

public class UpButton {

    //Bitmap to get button from image
    private Bitmap bitmap;

    //desired width and height of Bitmap
    private int width;
    private int height;

    //Rectangle hit box for the button
    private Rect rect;

    //coordinates
    private int x;
    private int y;

    //constructor
    public UpButton(Context context) {

        // Getting bitmap from drawable resource
        // currently using default png
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.up);
        } catch (Exception e)
        {
            System.out.println("Could not get resource");
            e.printStackTrace();
        }

        width = 300;
        height = 300;

        // initial coordinates
        x = 1800  + width;   //75
        y = GameView.getScreenHeight() - height;     //50

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

