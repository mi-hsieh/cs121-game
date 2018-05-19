package com.dagame.example.journeyu;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by Michael on 5/14/2018.
 */

// abstract class - not fully implemented yet

public abstract class Obstacle {

    // keeps track of individual objects
    // object position will change if other objects are removed or added

    private int ID = 0;

    public abstract void update(int frame);

    public abstract Bitmap getBitmap();

    public abstract void setX(int a);

    public abstract int getX();

    public abstract int getY();

    public abstract int getSpeed();

    public abstract Rect getCollisionRect();

    public abstract int getWidth();

    public abstract int getHeight();

    public void setID(int id)
    {
        ID = id;
    }

    public int getID()
    {
        return ID;
    }
}
