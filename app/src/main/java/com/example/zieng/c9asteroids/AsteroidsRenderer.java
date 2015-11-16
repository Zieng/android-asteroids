package com.example.zieng.c9asteroids;

/**
 * Created by zieng on 10/17/15.
 */

import android.graphics.PointF;
import android.graphics.Rect;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

public class AsteroidsRenderer implements Renderer
{
    boolean debugging = true;   // for debug use

    long frameCounter = 0;  // for monitoringand controlling the frames per second
    long averageFPS = 0;
    private long fps;

    private final float[] viewportMatrix = new float[16];   // for convert into opengl coordinate(-1.0~1.0)

    private GameManager gm;   // help manage current game states
    private SoundManager sm;
    private InputController ic;

    private final GameButton[] gameButtons =new GameButton[5];

    PointF handyPointF;   // capture various PointF details without creating new objects
    PointF handyPointF2;

    public AsteroidsRenderer(GameManager gameManager,SoundManager soundManager,InputController inputController)
    {
        gm = gameManager;
        sm = soundManager;
        ic = inputController;

        handyPointF = new PointF();
        handyPointF2 = new PointF();

        // debug
        Log.e("init AsteroidsRenderer:","screenWidth="+gm.screenWidth+",screenHeight="+gm.screenHeight);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.f,0.f,0.f,0.f);

        GLManager.buildProgram();

        createObjects();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused,int width,int height)
    {
        glViewport(0, 0, width, height);   //  make full screen

        orthoM(viewportMatrix, 0, 0, gm.metresToShowX, 0, gm.metresToShowY, 0f, 1f);
    }

    private void createObjects()
    {
        gm.ship = new SpaceShip(gm.mapWidth/2,gm.mapHeight/2);    // pass in the worldLocation of ship
        gm.border = new Border(gm.mapWidth,gm.mapHeight);      // pass in as the map size
        gm.stars = new Star[gm.numStars];

        for(int i=0;i<gm.numStars;i++)
        {
            gm.stars[i] = new Star(gm.mapWidth,gm.mapHeight);
        }
        gm.bullets = new Bullet[gm.numBullets];
        for(int i=0;i<gm.numBullets;i++)
        {
            gm.bullets[i] = new Bullet(gm.ship.getWorldLocation().x,gm.ship.getWorldLocation().y);

        }

        gm.numAsteroids = gm.baseNumAsteroids * gm.levelNumber;  // determine the number of asteroids
        gm.numAsteroidsRemaining = gm.numAsteroids;   // set how many asteroids need to be destroyed by player
        for(int i=0;i<gm.numAsteroids * gm.levelNumber; i++)
        {
            gm.asteroids[i] = new Asteroid(gm.levelNumber,gm.mapWidth,gm.mapHeight);
        }

        // HUD objects
        for(int i=0;i<gm.numLives;i++)
        {
            gm.lifeIcons[i] = new LifeIcon(gm,i);
        }
        for(int i=0;i<gm.numAsteroidsRemaining;i++)
        {
            gm.tallyIcons[i] = new TallyIcon(gm,i);
        }
        ArrayList<Rect> buttonsToDraw = ic.getButtons();
        int i=0;
        for(Rect rect:buttonsToDraw)
        {
            gameButtons[i] = new GameButton(rect.top,rect.left,rect.bottom,rect.right,gm);
            i++;
        }
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        long startFrameTime = System.currentTimeMillis();

        if(gm.isPlaying())
            update(fps);

        draw();

        long timeThisFrame = System.currentTimeMillis() - startFrameTime;
        if(timeThisFrame >= 1)
            fps = 1000/timeThisFrame;
        if(debugging)
        {
            frameCounter++;
            averageFPS += fps;
            if(frameCounter > 100)
            {
                averageFPS /= frameCounter;
                frameCounter = 0;
                Log.e("averageFPS:",""+averageFPS+",ship.x="+gm.ship.getWorldLocation().x+",ship.y="+gm.ship.getWorldLocation().y);
            }
        }
    }

    private void update(long fps)
    {
        for(int i=0;i<gm.numStars;i++)
        {
            gm.stars[i].update();
        }
        gm.ship.update(fps);

        for(int i=0;i<gm.numBullets;i++)
        {
            gm.bullets[i].update(fps,gm.ship.getWorldLocation());
        }
        //update the asteroids
        for(int i=0;i<gm.numAsteroids;i++)
        {
            if(gm.asteroids[i].isActive)
                gm.asteroids[i].update(fps);
        }

        // now all the objects are in their new location, so we start collision detection
        // check if the ship needs containing in the map
        if(CD.contain(gm.mapWidth,gm.mapHeight,gm.ship.cp))
        {
            lifeLost();
        }
        // check if the asteroids need containing in the map
        for(int i=0;i<gm.numAsteroids;i++)
        {
            if(gm.asteroids[i].isActive)
            {
                if(CD.contain(gm.mapWidth,gm.mapHeight,gm.asteroids[i].cp))
                {
                    gm.asteroids[i].bounce();
                    sm.playSound("blip");
                }
            }
        }
        // check if the bullet needs containing in the map
        // first to check if the bullet is out of sight.(if it is, it should be reset for the balance of the game)
        for(int i=0;i<gm.numBullets;i++)
        {
            if(gm.bullets[i].isInFlight())
            {
                handyPointF = gm.bullets[i].getWorldLocation();
                handyPointF2 = gm.ship.getWorldLocation();
                // if the bullet out of sight
                if(handyPointF.x > handyPointF2.x + gm.metresToShowX/2)
                {
                    gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                }
                else if(handyPointF.y > handyPointF2.y + gm.metresToShowY/2)
                {
                    gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                }
                else if(handyPointF.x < handyPointF2.x - gm.metresToShowX/2)
                {
                    gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                }
                else if(handyPointF.y < handyPointF2.y - gm.metresToShowY/2)
                {
                    gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                }

                // if the bullet out of the border
                if(CD.contain(gm.mapWidth,gm.mapHeight,gm.bullets[i].cp))
                {
                    gm.bullets[i].resetBullet(gm.ship.getWorldLocation());
                    sm.playSound("ricochet");
                }

                // check collision between asteroids and bullets
                for(int bulletNum = 0;bulletNum<gm.numBullets;bulletNum++)
                {
                    for(int asteroidNum =0 ;asteroidNum<gm.numAsteroids;asteroidNum++)
                    {
                        if(gm.bullets[bulletNum].isInFlight() && gm.asteroids[asteroidNum].isActive())
                        {
                            if(CD.detect(gm.bullets[bulletNum].cp , gm.asteroids[asteroidNum].cp))
                            {
                                gm.bullets[bulletNum].resetBullet(gm.ship.getWorldLocation());
                                destroyAsteroid(asteroidNum);
                            }
                        }
                    }
                }

                // check collision between asteroids and ship
                for(int asteroidNum =0;asteroidNum<gm.numAsteroids;asteroidNum++)
                {
                    if(gm.asteroids[asteroidNum].isActive())
                    {
                        if(CD.detect(gm.ship.cp,gm.asteroids[asteroidNum].cp))
                        {
                            destroyAsteroid(asteroidNum);
                            lifeLost();
                        }
                    }
                }
            }
        }
    }

    private void draw()
    {
        handyPointF = gm.ship.getWorldLocation();  // get where is the ship

        orthoM(viewportMatrix, 0, handyPointF.x - gm.metresToShowX / 2, handyPointF.x + gm.metresToShowX / 2,
                handyPointF.y - gm.metresToShowY / 2, handyPointF.y+gm.metresToShowY/2,0f,1f);

        glClear(GL_COLOR_BUFFER_BIT);

        //draw the ship
        gm.ship.draw(viewportMatrix);
        //draw the border
        gm.border.draw(viewportMatrix);
        //draw stars
        for(int i=0;i<gm.numStars;i++)
        {
            if(gm.stars[i].isActive)
                gm.stars[i].draw(viewportMatrix);
        }
        //draw the bullets
        for(int i=0;i<gm.numBullets;i++)
        {
            gm.bullets[i].draw(viewportMatrix);
        }
        //draw the asteroids
        for(int i=0;i<gm.numAsteroids;i++)
        {
            if(gm.asteroids[i].isActive)
                gm.asteroids[i].draw(viewportMatrix);
        }

        //draw the buttons
        for(int i=0;i<gameButtons.length;i++)
        {
            gameButtons[i].draw();
        }
        //draw the life icons
        for(int i=0;i<gm.numLives;i++)
        {
            gm.lifeIcons[i].draw();
        }
        //draw the level icons
        for(int i=0;i<gm.numAsteroidsRemaining;i++)
        {
            gm.tallyIcons[i].draw();
        }
    }

    public void lifeLost()
    {
        gm.ship.setWorldLocation(gm.mapWidth/2,gm.mapHeight/2);
        sm.playSound("shipexplode");
        gm.numLives -= 1; // deduct a life
        if(gm.numLives==0)
        {
            gm.levelNumber = 1;
            gm.numLives=3;
            createObjects();
            gm.switchPlayingStatus();
            sm.playSound("gameover");
        }
    }

    public void destroyAsteroid(int asteroidIndex)
    {
        gm.asteroids[asteroidIndex].setActive(false);
        sm.playSound("explode");
        gm.numAsteroidsRemaining --;

        if(gm.numAsteroidsRemaining==0)
        {
            gm.levelNumber++;
            gm.numLives++;
            sm.playSound("nextLevel");
            createObjects();
        }
    }


}
