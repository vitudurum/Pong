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

	public Paddle p1 = new Paddle(Pong.border_Left+15, 25, 0);
	public Paddle p2 = new Paddle(Pong.border_Right-Pong.paddle_width-15, 25, 1);
    int initSpeed = 5000000;
    //int incrVal = 300000;
    double incFact = 0.9;
    Font stringFont = new Font("SansSerif", Font.PLAIN, 20);
    Rectangle ball;
    int wait=initSpeed;
    Random r;
    double ballPosY=Pong.gHeight/2;

    public Ball(int x, int y) {
        p1score = p2score = 0;
        this.x = x;
        this.y = y;


        //create "ball"
        ball = new Rectangle(this.x, this.y, 25, 25);
     resetBall();
    }

    public void setXDirection(double xDir) {
        xDirection = xDir;
    }

    public void setYDirection(double yDir) {
        yDirection = yDir;
    }
    public double getYDirection() {
      return yDirection;
    }
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(ball.x, ball.y, ball.width, ball.height);
        g.setFont(stringFont);
        g.drawString("Speed:" + getSpeed(), Pong.gWidth / 2 - 10, Pong.gHeight / 2 + 50);
    }

    public void incSpeed() {
        wait = (int) (wait * incFact);
        if (wait < 0) {
            wait = 0;
        }
    }
        public void resetSpeed() {
            wait=initSpeed;

    }

    public double getSpeed() {
        return wait;
    }

    public void collision() {
        if (ball.intersects(p1.paddle)) {
            setXDirection(+1);
            // be safe
            ball.x=ball.x+Pong.paddle_width;
            incSpeed();
            Pong.PlaySound();
            //System.out.println("Pong-L...");
        }
        if (ball.intersects(p2.paddle)) {
            setXDirection(-1);
            // be safe
            ball.x=ball.x-Pong.paddle_width;
            incSpeed();
            Pong.PlaySound();

            //System.out.println("Pong-R...");
        }
    }
    public void resetBall()
    {
        System.out.println("-->Reset Ball");

        ball.x = Pong.gWidth/2;
        ballPosY=  Pong.gHeight/2;

        r = new Random();
        int rXDir = r.nextInt(2);
        if (rXDir == 0)
            rXDir--;
        setXDirection(rXDir);


        double rYDir = r.nextDouble();
        rYDir=rYDir-0.5;
        rYDir=rYDir*2;
        setYDirection(rYDir);

    }

    public void move() {
        collision();
        ballPosY=ballPosY+yDirection;
        ball.x += xDirection;
        ball.y = (int) ballPosY;
        //bounce the ball when it hits the edge of the screen
        if (ball.x <= Pong.border_Left) {
            setXDirection(+1);
            p2score++;
            resetSpeed();
            resetBall();
        }
        if (ball.x >= Pong.border_Right) {
            setXDirection(-1);
            p1score++;
            resetSpeed();
            resetBall();
        }

        if (ball.y <= Pong.border_Up) {
            setYDirection(getYDirection()*-1);
        }

        if (ball.y >= Pong.border_Down) {
            setYDirection(getYDirection()*-1);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                move();
                //Thread.sleep((long) wait,999999);
                //final long INTERVAL = 10000000;
                long start = System.nanoTime();
                long end=0;
                do{
                    end = System.nanoTime();
                }while(start + wait >= end);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
	public Paddle getPaddle1()
	{
		return p1;
	}
	public Paddle getPaddle2()
	{
		return p2;
	}
}