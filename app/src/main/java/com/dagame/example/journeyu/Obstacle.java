package com.dagame.example.journeyu;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by Michael on 5/14/2018.
 */

// abstract class - not fully implemented yet

public abstract class Obstacle {

    public abstract void update(int frame);

    public abstract Bitmap getBitmap();

    public abstract void setX(int a);

    public abstract int getX();

    public abstract int getY();

    public abstract int getSpeed();

    public abstract Rect getCollisionRect();

    public abstract int getWidth();

    public abstract int getHeight();
}
