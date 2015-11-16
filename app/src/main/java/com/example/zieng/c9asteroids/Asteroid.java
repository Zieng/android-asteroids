package com.example.zieng.c9asteroids;

import android.graphics.PointF;

import java.util.Random;

/**
 * Created by zieng on 10/18/15.
 */
public class Asteroid extends GameObject
{
    PointF [] points;
    CollisionPackage cp;

    public Asteroid(int levelNumber,int mapWidth,int mapHeight)
    {
        super();
        setType(Type.ASTEROID);

        Random r = new Random();
        setRotationRate(r.nextInt(50*levelNumber)+10);
        setTravellingAngle(r.nextInt(360));

        int x=r.nextInt(mapWidth - 100)+50;
        int y=r.nextInt(mapHeight - 100) + 50;

        if(x>250 && x<350)   // avoid the asteroid spawn near the center
            x+=100;
        if(y>250 && y<350)
            y+=100;
        setWorldLocation(x,y);

        setSpeed(r.nextInt(25*levelNumber)+1);  //avoid the speed = 0
        setMaxSpeed(140);
        if(getSpeed()>getMaxSpeed())
            setSpeed(getMaxSpeed());

        generatePoints();       // call the parent setVertices , define random asteroid shape

        //initialize the collision package
        cp = new CollisionPackage(points,getWorldLocation(),25,getFacingAngle());
    }

    public void update(float fps)
    {
        setxVelocity((float)(getSpeed()*Math.cos(Math.toRadians(getTravellingAngle() + 90))));
        setyVelocity((float)(getSpeed()*Math.sin(Math.toRadians(getTravellingAngle() + 90))));
        move(fps);
        //update the collision package
        cp.facingAngle = getFacingAngle();
        cp.worldLocation = getWorldLocation();
    }

    public void generatePoints()
    {
        points = new PointF[7];
        Random r = new Random();
        int i;

        //First a point roughly centre below 0
        points[0]=new PointF();
        i=r.nextInt(10)+1;
        if(i%2==0)
            i=-i;
        points[0].x=i;
        i=-(r.nextInt(20)+5);
        points[0].y=i;

        //now a point below centre but to the right and up a bit
        points[1]=new PointF();
        i=r.nextInt(14)+11;
        points[1].x=i;
        i=-(r.nextInt(12)+1);
        points[1].y=i;

        //above 0 to the right
        points[2]=new PointF();
        i=r.nextInt(14)+11;
        points[2].x=i;
        i=r.nextInt(12)+1;
        points[2].y=i;

        //roughly center above 0
        points[3]=new PointF();
        i=r.nextInt(10)+1;
        if(i%2==0)
            i=-i;
        points[3].x=i;
        i=(r.nextInt(20)+5);
        points[3].y=i;

        //left above 0
        points[4] = new PointF();
        i=-(r.nextInt(14)+11);
        points[4].x = i;
        i=r.nextInt(12)+1;
        points[4].y=i;

        //left below 0
        points[5] =new PointF();
        i=-(r.nextInt(14)+11);
        points[5].x =i;
        i=-(r.nextInt(12)+1);
        points[5].y=i;

        // the seventh point is used for crossing number algorithm to collision test
        points[6] = new PointF();
        points[6].x = points[0].x;
        points[6].y = points[0].y;

        float [] asteroidVertices = new float[]{
                points[0].x,points[0].y,0,
                points[1].x,points[1].y,0,

                points[1].x,points[1].y,0,
                points[2].x,points[2].y,0,

                points[2].x,points[2].y,0,
                points[3].x,points[3].y,0,

                points[3].x,points[3].y,0,
                points[4].x,points[4].y,0,

                points[4].x,points[4].y,0,
                points[5].x,points[5].y,0,

                points[5].x,points[5].y,0,
                points[0].x,points[0].y,0,
        };

        setVertices(asteroidVertices);
    }

    public void bounce()
    {
        //reverse the travelling angle
        if(getTravellingAngle() >= 180)
            setTravellingAngle(getTravellingAngle()-180);
        else
            setTravellingAngle(getTravellingAngle()+180);

        //reverse the velocity for occasionally they get stuck
        setWorldLocation(getWorldLocation().x - getxVelocity() / 3, getWorldLocation().y - getyVelocity() / 3);

        setSpeed(getSpeed() * 1.1f);   // speed up 10%
        if(getSpeed() > getMaxSpeed())
            setSpeed(getMaxSpeed());
    }
}
