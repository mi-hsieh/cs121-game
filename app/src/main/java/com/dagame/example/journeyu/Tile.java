package com.dagame.example.journeyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by Michael on 4/24/2018.
 */

public class Tile {

    //Bitmap to get tile (platform) from image
    private Bitmap bitmap;

    //desired width and height of Bitmap
    private int width;
    private int height;

    //Rectangle hit box for the tile
    private Rect rect;

    //coordinates
    private int x;
    private int y;

    //constructor
    public Tile(Context context) {

        // Getting bitmap from drawable resource
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cookie);
        } catch (Exception e)
        {
            System.out.println("Could not get resource");
            e.printStackTrace();
        }

        width = 200;
        height = 200;

        // initial coordinates
        // x + player width
        x = 1500  + (300);
        // we want the middle tile centered with the player
        // so the top tile is 3*height above the player
        y = (GameView.getScreenHeight()/2) - (3*height);

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

    // platforms don't move

    // method to update coordinate of tile
    public void update(){

        //adding top, left, bottom and right to the rect object
        rect.left = x;
        rect.top = y;
        rect.right = x + width;
        rect.bottom = y + height;
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

    // move tiles to initial positions (down y axis)
    // when first made
    // spacing is the space between tiles
    public void shiftDown(int shift)
    {
        y += shift;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public Rect getCollisionRect() {
        return rect;
    }

}

