package com.dagame.example.journeyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Stamina {
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
    public Stamina(Context context) {

        // Getting bitmap from drawable resource
        // currently using default png
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ice_cube);
        } catch (Exception e)
        {
            System.out.println("Could not get resource");
            e.printStackTrace();
        }

        width = 50;
        height = 50;

        // initial coordinates
        // x + player width
        x = GameView.getScreenWidth()/2;    //1200  + (300);
        // we want the middle tile centered with the player
        // so the top tile is 2*height above the player
        y = (GameView.getScreenWidth()/2) - (23*height);    // 75;

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

    // move tiles to initial positions (right X )
    // when first made
    // spacing is the space between staminas
    public void shiftRight(int shift)
    {
        x += shift;
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