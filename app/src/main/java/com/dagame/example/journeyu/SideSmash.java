package com.dagame.example.journeyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;


public class SideSmash {
    //Bitmap to get power-up from image
    private Bitmap bitmap;

    //desired width and height of Bitmap
    private int width;
    private int height;

    //Rectangle hit box for the power-up
    private Rect rect;

    //coordinates
    private int x;
    private int y;

    //sound effect
    private SoundPool sounds;
    private int snddama;

    // whether the obstacle is drawable or not, default false
    private boolean visible;

    //motion speed of the cone
    private int speed = 0;

    private int ID = 0;

    //constructor
    public SideSmash(Context context) {

        // Getting bitmap from drawable resource
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.side_smash);
            sounds = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
            snddama = sounds.load(context,R.raw.damage,1);

        } catch (Exception e)
        {
            System.out.println("Could not get resource");
            e.printStackTrace();
        }

        width = 200;
        height = 200;

        // initial coordinates
        x = -width;
        y = 0;
        speed = 5;

        // set visibility to true
        visible = true;

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
    // new parameter added
    public void update(int obsFrameCount){
        //updating x coordinate
        if(obsFrameCount<=5){
            setX((getX() + 14));
        }
        if(obsFrameCount>5 && obsFrameCount<=10){
            setX((getX() + 20));
        }
        if(obsFrameCount>10 && obsFrameCount<=15){
            setX((getX() + 6));
        }

        //adding top, left, bottom and right to the rect object
        rect.left = x;
        rect.top = y;
        rect.right = x + width;
        rect.bottom = y + height;
    }

    // getter and setter methods

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

    public void setY(int a) { y = a; }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean v)
    {
        visible = v;
    }

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

    public void setID(int id)
    {
        ID = id;
    }

    public int getID()
    {
        return ID;
    }

}







