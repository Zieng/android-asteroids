package com.example.zieng.c9asteroids;

import android.graphics.PointF;

/**
 * Created by zieng on 10/17/15.
 */
public class Bullet extends GameObject
{
    private boolean inFlight = false;
    CollisionPackage cp;

    public Bullet(float shipX,float shipY)
    {
        super();
        setType(Type.BULLET);
        setWorldLocation(shipX,shipY);

        float [] bulletVertices = new float[]{0,0,0};
        setVertices(bulletVertices);

        //initialize the collisionpackage
        PointF point = new PointF(0,0);
        PointF[] points = new PointF[1];
        points[0] = point;

        cp = new CollisionPackage(points,getWorldLocation(),1.0f,getFacingAngle());
    }

    public void shoot(float shipFacingAngle)
    {
        setFacingAngle(shipFacingAngle);
        inFlight = true;
        setSpeed(300);
    }

    public void resetBullet(PointF shipLocation)
    {
        inFlight = false;
        setxVelocity(0);
        setyVelocity(0);
        setSpeed(0);
        setWorldLocation(shipLocation.x, shipLocation.y);
    }

    public boolean isInFlight()
    {
        return inFlight;
    }

    public void update(long fps, PointF shipLocation)
    {
        if(inFlight)
        {
            setxVelocity((float) (getSpeed() * Math.cos(Math.toRadians(getFacingAngle() + 90))));
            setyVelocity((float)(getSpeed() * Math.sin(Math.toRadians(getFacingAngle()+90))));
        }
        else
        {
            setWorldLocation(shipLocation.x,shipLocation.y);
        }
        move(fps);

        //update the collision package
        cp.facingAngle = getFacingAngle();
        cp.worldLocation = getWorldLocation();
    }
}
