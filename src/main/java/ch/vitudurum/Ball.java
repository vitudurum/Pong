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
    final static int MAXP=5;
    double xDirection, yDirection;

    int p1score, p2score;

	public Paddle p1 = new Paddle(Pong.border_Left+15, 25, 0,this);
	public Paddle p2 = new Paddle(Pong.border_Right-Pong.paddle_width-15, 25, 1,this);
    int initSpeed = 2500000;
    //int incrVal = 300000;
    double incFact = 0.97;
    Font stringFont = new Font("SansSerif", Font.PLAIN, 20);
    Font stringFontEnde = new Font("SansSerif", Font.PLAIN, 50);
    Rectangle ball;
    int wait=initSpeed;
    Random r;
    double ballPosY=Pong.gHeight/2;
    boolean gameRun=true;
    int anspiel=0;

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
        //g.drawString("Speed:" + getSpeed(), Pong.gWidth / 2 - 10, Pong.gHeight / 2 + 50);
        if (!gameRun) {
            g.setColor(Color.RED);
            g.setFont(stringFontEnde);
            g.drawString("Game over",Pong.gWidth / 2 - 80,300);
        }
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
public void win(){
        gameRun=false;
        setXDirection(0);
        setYDirection(0);
     ball.x = Pong.gWidth/2;
    ballPosY=  Pong.gHeight/2;

}
public void startGame()
{
    System.out.println("Starting Game...");
    anspiel=0;
    p1score=0;
    p2score=0;
    p1.startGame=false;
    p2.startGame=false;
    gameRun=true;
    resetBall();
    resetSpeed();
}
    public void move() {

        System.out.println("m1");

        if (p1.startGame==true) {
            startGame();
        }
        if (p1.getKick()==true && anspiel==1)
        {
             anspiel=0;
            p1.setKick(false);
            p2.setKick(false);
        }
        if (p2.getKick()==true && anspiel==2)
        {
            anspiel=0;
            p1.setKick(false);
            p2.setKick(false);
        }

        System.out.println("m2");
        if (anspiel==0)
        {
            collision();
            System.out.println("m22");
            ballPosY = ballPosY + yDirection;
            ball.x += xDirection;
            ball.y = (int) ballPosY;

            //bounce the ball when it hits the edge of the screen
            if (ball.x <= Pong.border_Left) {
                setXDirection(+1);
                p2score++;
                if (p2score >=MAXP)
                    win();
                else {
                    resetSpeed();
                    anSpiel(1);
                }
            }
            if (ball.x >= Pong.border_Right) {
                setXDirection(-1);
                p1score++;
                if (p1score >=MAXP)
                    win();
                else {
                    resetSpeed();
                    anSpiel(2);
                }
            }

            if (ball.y <= Pong.border_Up) {
                setYDirection(getYDirection()*-1);
            }

            if (ball.y >= Pong.border_Down) {
                setYDirection(getYDirection()*-1);
            }

        }
        System.out.println("m3");
        if (anspiel==1)
        {
            //System.out.println("Anspiel 1");
            ball.x =p1.paddle.x+20;
            ball.y =p1.paddle.y+p1.paddle.height/2-ball.height/2;
            ballPosY=ball.y;
        }
        if (anspiel==2)
        {
            //System.out.println("Anspiel 2");
            ball.x =p2.paddle.x-20;
            ball.y =p2.paddle.y+p2.paddle.height/2-ball.height/2;
            ballPosY=ball.y;
        }


    }
public void anSpiel(int a)
{
    //System.out.println("Anspiel:"+a);
    anspiel=a;
}
    @Override
    public void run() {
        long start, end;
        try {
            while (true) {
                move();                //Thread.sleep((long) wait,999999);
                //final long INTERVAL = 10000000;
                 start = System.nanoTime();
                 end=0;
                do{
                    end = System.nanoTime();
                }while(start + wait >= end);
            }
        } catch (Exception e) {
            System.out.println("Ball:"+e.getMessage());
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
    public void Kick(int id)
    {
        if (id==0)
            p1.setKick(true);
        if (id==1)
            p2.setKick(true);

    }
}