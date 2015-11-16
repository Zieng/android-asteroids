package com.example.zieng.c9asteroids;

import android.app.Activity;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;

public class AsteroidsActivity extends Activity
{
    private GLSurfaceView asteroidsView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // get a display object to access screen detail
        Display display = getWindowManager().getDefaultDisplay();

        //load the resolution into a point object
        Point resolution = new Point();
        display.getSize(resolution);
        asteroidsView = new AsteroidsView(this,resolution.x,resolution.y);

        setContentView(asteroidsView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        asteroidsView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        asteroidsView.onResume();
    }

}
