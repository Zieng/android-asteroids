package com.example.zieng.c9asteroids;

/**
 * Created by zieng on 10/17/15.
 */

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

public class GLManager
{
    public static final int COMPONENTS_PER_VERTEX = 3;
    public static final int FLOAT_SIZE = 4;
    public static final int STRIDE = (COMPONENTS_PER_VERTEX) *(FLOAT_SIZE);
    public static final int ELEMENTS_PER_VERTEX = 3; //x,y,z

    //constants to represent glsl types
    public static final String U_MATRIX = "u_Matrix";
    public static final String A_POSITION = "a_Position";
    public static final String U_COLOR = "u_Color";

    // match the string above
    public static int uMatrixLocation;
    public static int aPositionLocation;
    public static int uColorLocation;

    // vertex shader program packed in the string
    private static String vertexShader=
            "uniform mat4 u_Matrix;"+
                    "attribute vec4 a_Position;"+
                    "void main()"+
                    "{"+
                    "gl_Position = u_Matrix * a_Position;"+
                    "gl_PointSize = 3.0;"+
                    "}";
    //fragment shader packed in string(no source code in the text,so I write it below)
    private static String fragmentShader=
            "precision mediump float;"+
            "uniform vec4 u_Color;"+
                    "void main()"+
                    "{"+
                    "gl_FragColor = u_Color;"+
                    "}";

    private static int program;   // handle to our GL program

    public static int getGLProgram()
    {
        return program;
    }

    public static int buildProgram()
    {
        return linkProgram(compileVertexShader(),compileFragmentShader());
    }

    private static int compileVertexShader()
    {
        return compileShader(GL_VERTEX_SHADER,vertexShader);
    }

    private static int compileFragmentShader()
    {
        return compileShader(GL_FRAGMENT_SHADER,fragmentShader);
    }

    private static int compileShader(int type,String shaderCode)
    {
        final int shader = glCreateShader(type);  // create a shader object and store its ID
        glShaderSource(shader,shaderCode);     // pass the shadercode to shader
        glCompileShader(shader);  // compile specified shader

        return shader;
    }

    private static int linkProgram(int vertexShader,int fragmentShader)
    {
        program = glCreateProgram();   // handle to the glProgram
        glAttachShader(program,vertexShader);
        glAttachShader(program,fragmentShader);
        glLinkProgram(program);

        return program;
    }
}
