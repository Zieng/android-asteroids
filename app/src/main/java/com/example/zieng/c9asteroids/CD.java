package com.example.zieng.c9asteroids;

import android.graphics.PointF;
import android.util.Log;

import java.util.Map;

/**
 * Created by zieng on 10/18/15.
 */
public class CD
{
    private static PointF rotatedPoint = new PointF();

    // check collision between two GameObject
    public static boolean detect(CollisionPackage cp1,CollisionPackage cp2)
    {
        boolean collided = false;

        float distanceX = (cp1.worldLocation.x) -(cp2.worldLocation.x);
        float distanceY = (cp1.worldLocation.y) -(cp2.worldLocation.y);
        double distance = Math.sqrt(distanceX*distanceX + distanceY*distanceY);

        if(distance < cp1.radius+cp2.radius)
        {
            Log.e("Circle collision:","true");

            double raidianAngle1 = (cp1.facingAngle/180)*Math.PI;
            double cosAngle1 = Math.cos(raidianAngle1);
            double sinAngle1 = Math.sin(raidianAngle1);

            double radianAngle2 = (cp2.facingAngle/180)* Math.PI;
            double cosAngle2 = Math.cos(radianAngle2);
            double sinAngle2 = Math.sin(radianAngle2);

            int numCrosses = 0;   // the number of times we cross a side
            float worldUnrotatedX,worldUnrotatedY;

            // loop through all the vertices from cp2,test each in turn with all the sides from cp1
            for(int i=0;i<cp1.vertexListLength;i++)
            {
                worldUnrotatedX=cp1.worldLocation.x+cp1.vertexList[i].x;
                worldUnrotatedY=cp1.worldLocation.y+cp1.vertexList[i].y;
                // rotate the newly updated point , stored in currentPoint
                cp1.currentPoint.x=cp1.currentPoint.x+(int)( (worldUnrotatedX-cp1.worldLocation.x)*cosAngle1 -(worldUnrotatedY-cp1.worldLocation.y)*sinAngle1);
                cp1.currentPoint.y=cp1.currentPoint.y+(int)( (worldUnrotatedX-cp1.worldLocation.x)*sinAngle1+(worldUnrotatedY-cp1.worldLocation.y)*cosAngle1);

                for(int j=0;j<cp2.vertexListLength-1;j++)
                {
                    worldUnrotatedX = cp2.worldLocation.x+cp2.vertexList[j].x;
                    worldUnrotatedY = cp2.worldLocation.y+cp2.vertexList[j].y;

                    cp2.currentPoint.x=cp2.currentPoint.x+(int)( (worldUnrotatedX-cp2.worldLocation.x)*cosAngle2 -(worldUnrotatedY-cp2.worldLocation.y)*sinAngle2);
                    cp2.currentPoint.y=cp2.currentPoint.y+(int)( (worldUnrotatedX-cp2.worldLocation.x)*sinAngle2+(worldUnrotatedY-cp2.worldLocation.y)*cosAngle2);

                    worldUnrotatedX = cp2.worldLocation.x+cp2.vertexList[j+1].x;   // the code in the book is [i+1], which I think it may be wrong
                    worldUnrotatedY = cp2.worldLocation.y+cp2.vertexList[j+1].y;

                    cp2.currentPoint2.x=cp2.currentPoint2.x+(int)( (worldUnrotatedX-cp2.worldLocation.x)*cosAngle2 -(worldUnrotatedY-cp2.worldLocation.y)*sinAngle2);
                    cp2.currentPoint2.y=cp2.currentPoint2.y+(int)( (worldUnrotatedX-cp2.worldLocation.x)*sinAngle2+(worldUnrotatedY-cp2.worldLocation.y)*cosAngle2);

                    if( ( (cp2.currentPoint.y > cp1.currentPoint.y)!=(cp2.currentPoint2.y>cp1.currentPoint.y)  ) &&
                            (cp1.currentPoint.x < (cp2.currentPoint2.x - cp2.currentPoint2.x) * (cp1.currentPoint.y - cp2.currentPoint.y) /(cp2.currentPoint2.y -cp2.currentPoint.y) + cp2.currentPoint.x)  )
                    {
                        numCrosses++;
                    }
                }

                // check the numCrosses to determine if collision occurred
                if(numCrosses % 2==0)
                    collided = false;
                else
                    return true;
            }
        }

        return collided;
    }

    // check if the GameObject is in the border or not.
    public static boolean contain(float mapWidth,float mapHeight,CollisionPackage cp)
    {
        boolean possibleCollision = false;

        if(cp.worldLocation.x - cp.radius <  0)
        {
            possibleCollision = true;
        }
        else if(cp.worldLocation.x + cp.radius > mapWidth)
        {
            possibleCollision = true;
        }
        else if(cp.worldLocation.y - cp.radius < 0)
        {
            possibleCollision = true;
        }
        else if(cp.worldLocation.y + cp.radius > mapHeight)
        {
            possibleCollision = true;
        }

        if(possibleCollision)   // precise collision for the border and other GameObject
        {
            double radianAngle = (cp.facingAngle/180)*Math.PI;
            double cosAngle = Math.cos(radianAngle);
            double sinAngle = Math.sin(radianAngle);

            for(int i=0;i<cp.vertexListLength;i++)
            {
                float worldUnrotatedX = cp.worldLocation.x+cp.vertexList[i].x;
                float worldUnrotatedY = cp.worldLocation.y+cp.vertexList[i].y;
                //rotate the newly updated point,stored in currentPoint
                cp.currentPoint.x=cp.currentPoint.x+(int)( (worldUnrotatedX-cp.worldLocation.x)*cosAngle -(worldUnrotatedY-cp.worldLocation.y)*sinAngle);
                cp.currentPoint.y=cp.currentPoint.y+(int)( (worldUnrotatedX-cp.worldLocation.x)*sinAngle+(worldUnrotatedY-cp.worldLocation.y)*cosAngle);

                // check the rotated vertex for coliision
                if(cp.currentPoint.x < 0)
                    return true;
                else if(cp.currentPoint.x > mapWidth)
                    return true;
                else if(cp.currentPoint.y < 0)
                    return true;
                else if(cp.currentPoint.y > mapHeight)
                    return true;

            }
        }


        return false;
    }
}
