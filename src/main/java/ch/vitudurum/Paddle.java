package ch.vitudurum;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;


public class Paddle implements Runnable{
	
	int x, y, yDirection;
	public int id;
	Rectangle paddle;
	final static ADCReader adc = new ADCReader();


	public Paddle(int x, int y,int id) {
		this.x = x;
		this.y = y;
		this.id = id;
		paddle = new Rectangle(x, y, 15, 100);

	}
		
	public void keyPressed(KeyEvent e) {
			if (id==0) {
				if (e.getKeyCode() == KeyEvent.VK_W) {
					setYDirection(-10);
				}
				if (e.getKeyCode() == KeyEvent.VK_S) {
					setYDirection(10);
				}
			}
			if (id==1) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					setYDirection(-10);
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					setYDirection(10);
				}
			}
	}
	
	public void keyReleased(KeyEvent e) {
	if (id==0) {
		if (e.getKeyCode() == e.VK_W) {
		setYDirection(0);
	}
	if (e.getKeyCode() == e.VK_S) {
		setYDirection(0);
	}
	}
	if (id==1)
{
		if(e.getKeyCode() == e.VK_UP) {
			setYDirection(0);
		}
		if(e.getKeyCode() == e.VK_DOWN) {
			setYDirection(0);
		}
}
	}
	public void setYDirection(int yDir) {
		yDirection = yDir;
	}
	
	public void move() {

		//Keyboard
	 	paddle.y = paddle.y+yDirection;

		 //ADC
		//System.out.println("State ADC:"+adc.isADCUp());
		if (adc.isADCUp())
		{
		//	paddle.y=adc.getADCValue(id);
		//	paddle.y *= 3.75;
		}
		//System.out.println("Wert Paddle "+this.id+":"+paddle.y);

		//System.out.println("Wert Paddle:"+paddle.y);
		if (paddle.y <= 15)
	 		paddle.y = 15;

	 	if (paddle.y > 1030)
			 paddle.y = 1030;

		//System.out.println("["+this.id+"]"+yDirection);
		//System.out.println("["+this.id+"]"+paddle.y);

	}
	public void draw(Graphics g) {

		if (id==0) {
			g.setColor(Color.CYAN);
			g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
		}
		if (id==1)
		{
			g.setColor(Color.pink);
			g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
			}

	}
	@Override
	public void run() {
		try {
			while(true) {
				move();
				Thread.sleep(20);
			}
		} catch(Exception e) { System.err.println(e.getMessage()); }
	}


	

}
