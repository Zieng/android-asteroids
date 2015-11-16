package com.example.zieng.c9asteroids;

import android.graphics.PointF;
import java.nio.*;
import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

/**
 * Created by zieng on 10/18/15.
 */
public class TallyIcon
{
    private final float[] viewportMatrix = new float[16];
    private static int glProgram;
    private int numVertices;
    private FloatBuffer vertices;

    public TallyIcon(GameManager gm,int nthIcon)
    {
        orthoM(viewportMatrix,0,0,gm.screenWidth,gm.screenHeight,0,0f,1.0f);

        float padding = gm.screenWidth / 160;
        float iconWidth = 1;
        float iconHeight = gm.screenHeight/15;
        float startX = 10 + (padding + iconWidth) *nthIcon;
        float startY = iconHeight*2 + padding;

        PointF p1 = new PointF();
        p1.x = startX;
        p1.y = startY;

        PointF p2 =new PointF();
        p2.x = startX;
        p2.y = startY - iconHeight;

        float[] modelVertices = new float[]{
                p1.x,p1.y,0,
                p2.x,p2.y,0,
        };

        final int ELEMENTS_PER_VERTEX =3;  // x,y,z
        int numElements = modelVertices.length;
        numVertices = numElements / ELEMENTS_PER_VERTEX;

        vertices = ByteBuffer.allocateDirect(numElements * GLManager.FLOAT_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();

        vertices.put(modelVertices);

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
        glUniform4f(uColorLocation, 1.0f, 0.0f, 1.0f, 1.0f);

        glDrawArrays(GL_LINES,0,numVertices);
    }
}
