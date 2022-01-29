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
	int x, y, xDirection, yDirection;
	private static final byte TCA9534_REG_ADDR_OUT_PORT1 = (byte) 0x84;
	private static final byte TCA9534_REG_ADDR_OUT_PORT2 = (byte) 0xc4;

	private static final byte TCA9534_REG_ADDR_CFG = (byte)0x4B;
	I2C tca9534Dev;
	Context pi4j = Pi4J.newAutoContext();
	I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
	I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("7830").bus(1).device(0x4B).build();


	int p1score, p2score;
	
	Paddle p1 = new Paddle(this,10, 25, 0);
	Paddle p2 = new Paddle(this,Pong.gWidth-25, 25, 1);
	
	Rectangle ball;

	
	public Ball(int x, int y){
		p1score = p2score = 0;
		this.x = x;
		this.y = y;

		// Connect ADC
		try (I2C tca9534Dev = i2CProvider.create(i2cConfig)) {
			this.tca9534Dev=tca9534Dev;
			int config = tca9534Dev.readRegister(TCA9534_REG_ADDR_CFG);
			if (config < 0)
				throw new IllegalStateException(
						"Failed to read configuration from address 0x" + String.format("%02x", TCA9534_REG_ADDR_CFG));

			System.out.println("IC2 Ready");
		}



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
			setXDirection(+1);
			p2score++;
			
	}
		if (ball.x >= 1920) {
			setXDirection(-1);
			p1score++;
		}
		
		if (ball.y <= 15) {
			setYDirection(+1);
		}
		
		if (ball.y >= 1060) {
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
	public int getADCValue(int id) {
		if (id == 0)
			return tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT1);
		if (id == 2)
			return tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT2);
		return -1;
	}
}