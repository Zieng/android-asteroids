package com.example.zieng.c9asteroids;

/**
 * Created by zieng on 10/17/15.
 */
public class GameManager
{
    int mapWidth=800;
    int mapHeight=600;
    private boolean playing = false;

    SpaceShip ship;   //  first game object
    Border border;
    Star[] stars;
    int numStars = 200;
    Bullet[] bullets;
    int numBullets = 20;
    Asteroid[] asteroids;
    int numAsteroids;
    int numAsteroidsRemaining;
    int baseNumAsteroids =10;
    int levelNumber = 1;
    TallyIcon [] tallyIcons;
    int numLives = 3;
    LifeIcon [] lifeIcons;

    int screenWidth,screenHeight;

    // how many metres of the virtual world
    int metresToShowX = 390;
    int metresToShowY = 220;

    public GameManager(int x,int y)
    {
        screenHeight = y;
        screenWidth = x;

        asteroids = new Asteroid[500];

        lifeIcons = new LifeIcon[50];
        tallyIcons = new TallyIcon[500];
    }

    public void switchPlayingStatus()
    {
        playing = !playing;
    }

    public boolean isPlaying()
    {
        return playing;
    }


}
