package com.example.zieng.c9asteroids;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by zieng on 10/17/15.
 */
public class AsteroidsView extends GLSurfaceView
{
    GameManager gm;
    SoundManager sm;
    InputController ic;

    public AsteroidsView(Context context,int screenX,int screenY)
    {
        super(context);
        gm = new GameManager(screenX,screenY);
        sm = new SoundManager();
        sm.loadSound(context);
        ic = new InputController(screenX,screenY);

        setEGLContextClientVersion(2);  // set to opegl es 2
        setRenderer(new AsteroidsRenderer(gm,sm,ic));

        // test coordinate
        Log.e("init AsteroidsView:","screenX="+screenX+",screenY="+screenY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        ic.handleInput(motionEvent,gm,sm);
        return true;

    }
}
