package pongGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;


public class Ball implements Runnable {

	//global variables
	int x, y, xDirection, yDirection;
	
	
	int p1score, p2score;
	
	Paddle p1 = new Paddle(10, 25, 0);
	Paddle p2 = new Paddle(1900, 25, 1);
	
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
	
	public void setXDirection(int xDir){
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
        if(ball.intersects(p1.paddle))
            setXDirection(+1);
        if(ball.intersects(p2.paddle))
            setXDirection(-1);
	}	
	public void move() {
		collision();
		ball.x += xDirection;
		ball.y += yDirection;
		//bounce the ball when it hits the edge of the screen
		if (ball.x <= 0) {
			setXDirection(+2);
			p2score++;
			
	}
		if (ball.x >= 1920) {
			setXDirection(-2);
			p1score++;
		}
		
		if (ball.y <= 15) {
			setYDirection(+2);
		}
		
		if (ball.y >= 1060) {
			setYDirection(-2);
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