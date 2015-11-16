package com.example.zieng.c9asteroids;

import android.content.Context;
import android.graphics.PointF;

/**
 * Created by zieng on 10/18/15.
 */
public class CollisionPackage
{
    public float facingAngle;  //allow to compute current world coordinate of eache vertex
    public PointF[] vertexList;  // model-space coordinates
    public int vertexListLength;  //to avoid continually call vertex.length
    public float radius;  // furthest point from the center,not the real radius

    public PointF worldLocation;

    public PointF currentPoint = new PointF();  // avoid garbage collector to improve efficiency
    public PointF currentPoint2= new PointF();

    public CollisionPackage(PointF[] vertexList,PointF worldLocation,float radius,float facingAngle)
    {
        vertexListLength = vertexList.length;
        this.vertexList = new PointF[vertexListLength];

        //make a copy of the vertex list
        for(int i=0;i<vertexListLength;i++)
        {
            this.vertexList[i] = new PointF();
            this.vertexList[i].x = vertexList[i].x;
            this.vertexList[i].y = vertexList[i].y;
        }
        //  pass value? reference?
        this.worldLocation = new PointF();
        this.worldLocation = worldLocation;

        this.radius = radius;
        this.facingAngle = facingAngle;
    }
}
