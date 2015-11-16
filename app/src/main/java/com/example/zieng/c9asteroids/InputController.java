package com.example.zieng.c9asteroids;

import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by zieng on 10/18/15.
 */
public class InputController
{
    private  int currentBullet;
    Rect left;
    Rect right;
    Rect thrust;
    Rect shoot;
    Rect pause;

    InputController(int screenWidth,int screenHeight)
    {
        int buttonWidth=screenWidth/8;
        int buttonHeight=screenHeight/7;
        int buttonPadding = screenWidth/80;

        left=new Rect(buttonPadding,
                screenHeight-buttonHeight-buttonPadding,
                buttonWidth,
                screenHeight-buttonPadding);
        right= new Rect(buttonWidth+buttonPadding,
                screenHeight-buttonHeight-buttonPadding,
                buttonWidth+buttonPadding+buttonWidth,
                screenHeight-buttonPadding);
        thrust=new Rect(screenWidth-buttonWidth-buttonPadding,
                screenHeight-buttonHeight-buttonPadding-buttonHeight-buttonPadding,
                screenWidth-buttonPadding,
                screenHeight-buttonPadding-buttonHeight-buttonPadding);
        shoot = new Rect(screenWidth-buttonWidth-buttonPadding,
                screenHeight-buttonHeight-buttonPadding,
                screenWidth-buttonPadding,
                screenHeight-buttonPadding);
        pause= new Rect(screenWidth-buttonPadding-buttonWidth,
                buttonPadding,
                screenWidth-buttonPadding,
                buttonPadding+buttonHeight);
    }

    public ArrayList getButtons()
    {
        ArrayList<Rect> currentButtonList = new ArrayList<>();
        currentButtonList.add(left);
        currentButtonList.add(right);
        currentButtonList.add(thrust);
        currentButtonList.add(shoot);
        currentButtonList.add(pause);

        return currentButtonList;
    }

    public void handleInput(MotionEvent motionEvent,GameManager gm,SoundManager sm)
    {
        int pointerCount = motionEvent.getPointerCount();
        for(int i=0;i<pointerCount;i++)
        {
            int x=(int) motionEvent.getX(i);
            int y=(int) motionEvent.getY(i);

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    if(right.contains(x,y))
                    {
                        gm.ship.setPressingLeft(false);
                        gm.ship.setPressingRight(true);
                    }
                    else if(left.contains(x,y))
                    {
                        gm.ship.setPressingRight(false);
                        gm.ship.setPressingLeft(true);
                    }
                    else if(thrust.contains(x,y))
                    {
                        gm.ship.toggleThrust();
                    }
                    else if(shoot.contains(x,y))
                    {
                        if(gm.ship.pullTrigger())
                        {
                            gm.bullets[currentBullet].shoot(gm.ship.getFacingAngle());
                            currentBullet=(currentBullet==gm.numBullets)?0:currentBullet+1;
                            sm.playSound("shoot");
                        }
                    }
                    else if(pause.contains(x,y))
                    {
                        gm.switchPlayingStatus();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(right.contains(x,y))
                    {
                        gm.ship.setPressingRight(false);
                    }
                    else if(left.contains(x,y))
                    {
                        gm.ship.setPressingLeft(false);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if(right.contains(x,y))
                    {
                        gm.ship.setPressingLeft(false);
                        gm.ship.setPressingRight(true);
                    }
                    else if(left.contains(x,y))
                    {
                        gm.ship.setPressingRight(false);
                        gm.ship.setPressingLeft(true);
                    }
                    else if(thrust.contains(x,y))
                    {
                        gm.ship.toggleThrust();
                    }
                    else if(shoot.contains(x,y))
                    {
                        if(gm.ship.pullTrigger())
                        {
                            gm.bullets[currentBullet].shoot(gm.ship.getFacingAngle());
                            currentBullet=(currentBullet==gm.numBullets)?0:currentBullet+1;
                            sm.playSound("shoot");
                        }
                    }
                    else if(pause.contains(x,y))
                    {
                        gm.switchPlayingStatus();
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if(right.contains(x,y))
                    {
                        gm.ship.setPressingRight(false);
                    }
                    else if(left.contains(x,y))
                    {
                        gm.ship.setPressingLeft(false);
                    }
                    break;

            }
        }
    }
}
