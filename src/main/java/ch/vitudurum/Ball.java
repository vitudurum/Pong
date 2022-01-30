package ch.vitudurum;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;


public class Ball implements Runnable {

	//global variables
	int x, y;
   double  xDirection, yDirection;

	int p1score, p2score;
	
	Paddle p1 = new Paddle(10, 25, 0);
	Paddle p2 = new Paddle(Pong.gWidth-25, 25, 1);
	double speed = 1.2;
	
	Rectangle ball;

	
	public Ball(int x, int y){
		p1score = p2score = 0;
		this.x = x;
		this.y = y;





		//Set ball moving randomly
		Random r = new Random();
		int rXDir = r.nextInt(1);
		if (rXDir == 0)
			rXDir--;
		setXDirection(rXDir);
		
		int rYDir = r.nextInt(1);
		if (rYDir == 0)
			rYDir--;
		setYDirection(rYDir);
		
		//create "ball"
		ball = new Rectangle(this.x, this.y, 25, 25);
	}
	
	public void setXDirection(double xDir){
		xDirection = xDir;
	}
	public void setYDirection(int yDir){
		yDirection = yDir;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(ball.x, ball.y, ball.width, ball.height);
	}


	public void collision(){
        if(ball.intersects(p1.paddle)) {
			speed=speed*1.2;
			System.out.println("Speed:"+speed);
			setXDirection(speed);
		}
        if(ball.intersects(p2.paddle)) {
			speed=speed*1.2;
			System.out.println("Speed:"+speed);
			setXDirection(speed*-1);
		}
	}	
	public void move() {
		collision();
		ball.x += xDirection;
		ball.y += yDirection;
		//bounce the ball when it hits the edge of the screen
		if (ball.x <= 0) {
			speed=1.2;
			setXDirection(+1);
			p2score++;
			ball.x=1000;
			ball.y=500;


		}
		if (ball.x >= Pong.gWidth) {
			speed=1.2;
			setXDirection(-1);
			p1score++;
			ball.x=1000;
			ball.y=500;

		}
		
		if (ball.y <= 15) {
			setYDirection(+1);
		}
		
		if (ball.y >= Pong.gHeight) {
			setYDirection(-1);
		}
	}
		@Override
	public void run() {
		try {
			while(true) {
				move();
				Thread.sleep(5);
			}
		}catch(Exception e) { System.err.println(e.getMessage()); }

	}

}