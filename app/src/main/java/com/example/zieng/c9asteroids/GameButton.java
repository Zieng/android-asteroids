package com.example.zieng.c9asteroids;

import android.graphics.PointF;
import java.nio.*;
import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;


/**
 * Created by zieng on 10/18/15.
 */
public class GameButton
{
    private final float[] viewportMatrix = new float[16];
    private static int glProgram;  // handle to the GL glProgram
    private int numVertices;  // how many vertices to make the button
    private FloatBuffer vertices;  // hold vertices data

    public GameButton(int top,int left,int bottom,int right,GameManager gm)
    {
        orthoM(viewportMatrix,0,0,gm.screenWidth,gm.screenHeight,0,0,1f);

        //shrink the button visuals to make them less obtrusive while leaving the screen area they represent the same
        int width = (right-left)/2;
        int height = (top - bottom)/2;
        left += width/2;
        right -= width/2;
        top -= height/2;
        bottom += height/2;

        PointF p1 = new PointF();
        p1.x = left;
        p1.y = top;

        PointF p2 = new PointF();
        p2.x = right;
        p2.y = top;

        PointF p3 = new PointF();
        p3.x = right;
        p3.y = bottom;

        PointF p4 = new PointF();
        p4.x = left;
        p4.y = bottom;

        // add 4 points to an array
        float[] modelVertices = new float[]{
                p1.x,p1.y,0,
                p2.x,p2.y,0,

                p2.x,p2.y,0,
                p3.x,p3.y,0,

                p3.x,p3.y,0,
                p4.x,p4.y,0,

                p4.x,p4.y,0,
                p1.x,p1.y,0
        };

        final int ELEMENTS_PER_VERTEX=3;   //x,y,z
        int numElements = modelVertices.length;
        numVertices = numElements/ELEMENTS_PER_VERTEX;

        // initialize the vertices bytebuffer based on the ....
        vertices = ByteBuffer.allocateDirect(numElements * GLManager.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertices.put(modelVertices);   // add the button into the ByteBuffer object

        glProgram = GLManager.getGLProgram();
    }

    public void draw()
    {
        glUseProgram(glProgram);

        int uMatrixLocation = glGetUniformLocation(glProgram,GLManager.U_MATRIX);
        int aPositionLocation = glGetAttribLocation(glProgram, GLManager.A_POSITION);
        int uColorLocation = glGetUniformLocation(glProgram,GLManager.U_COLOR);

        vertices.position(0);

        glVertexAttribPointer(
                aPositionLocation,
                GLManager.COMPONENTS_PER_VERTEX,
                GL_FLOAT,
                false,
                GLManager.STRIDE,
                vertices
        );

        glEnableVertexAttribArray(aPositionLocation);

        glUniformMatrix4fv(uMatrixLocation,1,false,viewportMatrix,0);
        glUniform4f(uColorLocation,0.0f,0.0f,1.0f,1.0f);

        glDrawArrays(GL_LINES,0,numVertices);
    }
}
