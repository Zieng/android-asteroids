package com.example.zieng.c9asteroids;

/**
 * Created by zieng on 10/17/15.
 */

import android.graphics.PointF;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

import static android.opengl.Matrix.*;

public class GameObject
{
    boolean isActive;
    public enum Type{SHIP,ASTEROID,BORDER,BULLET,STAR}
    private Type type;
    private static int glProgram = -1;
    private int numElements;   //
    private int numVertices;   //

    private float [] modelVertices;  // hold the coordinates of vertices that define the model

    private float xVelocity = 0;
    private float yVelocity = 0;
    private float speed = 0;
    private float maxSpeed = 200;

    private PointF worldLocation = new PointF();  // where is the object in the game world

    private FloatBuffer vertices;  // hold vertex data passed into OpenGL glProgram;

    private final float [] modelMatrix = new float[16];  // to translate model to the game world

    float [] viewportModelMatrix = new float[16];
    float [] rotateViewportModelMatrix = new float[16];

    private float facingAngle = 90f;   // where is the game object facing
    private float rotationRate = 0f;   // how fast is it rotate
    private float travellingAngle = 0f;   // which direction it heads to

    private float length;    // how long and wide the object is
    private float width;

    public GameObject()
    {
        if(glProgram == -1)
        {
            setGLProgram();
            glUseProgram(glProgram);   //tell opengl to use the glProgram
            GLManager.uMatrixLocation = glGetUniformLocation(glProgram,GLManager.U_MATRIX);
            GLManager.aPositionLocation = glGetAttribLocation(glProgram,GLManager.A_POSITION);
            GLManager.uColorLocation = glGetUniformLocation(glProgram,GLManager.U_COLOR);
        }

        isActive =true;

    }

    public boolean isActive()
    {
        return isActive;
    }

    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    public void setGLProgram()
    {
        glProgram = GLManager.getGLProgram();
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type t)
    {
        type = t;
    }

    public void setSize(float w,float l)
    {
        width = w;
        length = l;
    }

    public PointF getWorldLocation()
    {
        return worldLocation;
    }

    public void setWorldLocation(float x, float y)
    {
        this.worldLocation.x = x;
        this.worldLocation.y = y;
    }

    public void setVertices(float [] objectVertices)
    {
        modelVertices = new float[objectVertices.length];
        modelVertices = objectVertices;

        numElements = modelVertices.length;
        numVertices = numElements/GLManager.ELEMENTS_PER_VERTEX;

        vertices = ByteBuffer.allocateDirect(numElements*GLManager.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertices.put(modelVertices);

    }

    public void draw(float [] viewportMatrix)
    {
        glUseProgram(glProgram);

        vertices.position(0);   // set vertices to the first byte

        glVertexAttribPointer(
                GLManager.aPositionLocation,
                GLManager.COMPONENTS_PER_VERTEX,
                GL_FLOAT,
                false,
                GLManager.STRIDE,
                vertices);

        glEnableVertexAttribArray(GLManager.aPositionLocation);

        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, worldLocation.x, worldLocation.y, 0);   // model coordinate to world coordinate

        multiplyMM(viewportModelMatrix, 0, viewportMatrix, 0, modelMatrix, 0);  //viewportModelMatrix = viewportMatrix * modelMatrix

        setRotateM(modelMatrix, 0, facingAngle, 0, 0, 1.0f);
        multiplyMM(rotateViewportModelMatrix, 0, viewportModelMatrix, 0, modelMatrix, 0);

        glUniformMatrix4fv(GLManager.uMatrixLocation, 1, false, rotateViewportModelMatrix, 0);
        glUniform4f(GLManager.uColorLocation,1.0f,1.0f,1.0f,1.0f);

        switch (type)
        {
            case SHIP:
                glDrawArrays(GL_TRIANGLES,0,numVertices);
                break;
            case ASTEROID:
                glDrawArrays(GL_LINES,0,numVertices);
                break;
            case BORDER:
                glDrawArrays(GL_LINES,0,numVertices);
                break;
            case STAR:
                glDrawArrays(GL_POINTS,0,numVertices);
                break;
            case BULLET:
                glDrawArrays(GL_POINTS,0,numVertices);
                break;
        }
    }

    public void setRotationRate(float rotationRate)
    {
        this.rotationRate = rotationRate;
    }
    public float getTravellingAngle()
    {
        return travellingAngle;
    }
    public void setTravellingAngle(float travellingAngle)
    {
        this.travellingAngle = travellingAngle;
    }
    public float getFacingAngle()
    {
        return facingAngle;
    }
    public void setFacingAngle(float facingAngle)
    {
        this.facingAngle = facingAngle;
    }
    public float getxVelocity()
    {
        return xVelocity;
    }
    public float getyVelocity()
    {
        return yVelocity;
    }
    public void setxVelocity(float xVelocity)
    {
        this.xVelocity = xVelocity;
    }
    public void setyVelocity(float yVelocity)
    {
        this.yVelocity = yVelocity;
    }
    public float getSpeed()
    {
        return speed;
    }
    public void setSpeed(float speed)
    {
        this.speed = speed ;
    }
    public float getMaxSpeed()
    {
        return maxSpeed;
    }
    public void setMaxSpeed(float maxSpeed)
    {
        this.maxSpeed = maxSpeed;
    }

    void move(float fps)
    {
        if(xVelocity != 0)
        {
            worldLocation.x += xVelocity/fps;
        }
        if(yVelocity != 0)
        {
            worldLocation.y += yVelocity/fps;
        }
        if(rotationRate != 0)
        {
            facingAngle += rotationRate/fps;
        }
    }


}
