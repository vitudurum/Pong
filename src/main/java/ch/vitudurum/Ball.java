package ch.vitudurum;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

import java.awt.*;
import java.util.Random;


public class Ball implements Runnable {

    //global variables
    int x, y;
    double xDirection, yDirection;

    int p1score, p2score;

    Paddle p1 = new Paddle(10, 25, 0);
    Paddle p2 = new Paddle(Pong.gWidth - 25, 25, 1);
    int initSpeed = 5;
    double incrFactor = 1.1;
    //double speed = 0;
    Font stringFont = new Font("SansSerif", Font.PLAIN, 20);
    Rectangle ball;


    public Ball(int x, int y) {
        p1score = p2score = 0;
        this.x = x;
        this.y = y;


        //Set ball moving randomly
        Random r = new Random();
        int rXDir = r.nextInt(initSpeed);
        if (rXDir == 0)
            rXDir--;
        setXDirection(rXDir);
        System.out.println("R X:"+rXDir);

        int rYDir = r.nextInt(initSpeed);
        if (rYDir == 0)
            rYDir--;
        System.out.println("R Y:"+rYDir);
        setYDirection(rYDir);

        //create "ball"
        ball = new Rectangle(this.x, this.y, 25, 25);

    }

    public void setXDirection(double xDir) {
        xDirection = xDir;
    }

    public void setYDirection(double yDir) {
        yDirection = yDir;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(ball.x, ball.y, ball.width, ball.height);
        g.setFont(stringFont);
        g.drawString("Speed:" + getSpeed(), Pong.gWidth / 2 - 10, Pong.gHeight / 2 + 50);
    }

    public void incSpeed() {
        setXDirection(xDirection * incrFactor);
        System.out.println("Speed:" + xDirection);


    }

    public double getSpeed() {
        return xDirection;
    }

    public void collision() {
        if (ball.intersects(p1.paddle)) {
            setXDirection(xDirection*-1);
            incSpeed();

        }
        if (ball.intersects(p2.paddle)) {
            setXDirection(xDirection*-1);
            incSpeed();
        }
    }

    public void move() {
        collision();
        ball.x += xDirection;
        ball.y += yDirection;
        //bounce the ball when it hits the edge of the screen
        if (ball.x <= 0) {
            setXDirection(initSpeed);
            p2score++;
            ball.x = 100;
            ball.y = 500;
        }
        if (ball.x >= Pong.gWidth) {
            setXDirection(initSpeed * -1);
            p1score++;
            ball.x = 1800;
            ball.y = 500;
        }

        if (ball.y <= 25) {
            setYDirection(yDirection*-1);
        }

        if (ball.y >= Pong.gHeight-30) {
            setYDirection(yDirection*-1);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                move();
                Thread.sleep(20);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

}