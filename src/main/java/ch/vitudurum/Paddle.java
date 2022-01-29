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
	private static final byte TCA9534_REG_ADDR_OUT_PORT1 = (byte) 0x84;
	private static final byte TCA9534_REG_ADDR_OUT_PORT2 = (byte) 0xc4;

	Rectangle paddle;
	private static final byte TCA9534_REG_ADDR_CFG = (byte)0x4B;
	I2C tca9534Dev;
	Context pi4j = Pi4J.newAutoContext();
	I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
	I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("7830").bus(1).device(0x4B).build();

	public Paddle(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
		paddle = new Rectangle(x, y, 10, 100);

		try (I2C tca9534Dev = i2CProvider.create(i2cConfig)) {
			this.tca9534Dev=tca9534Dev;
			int config = tca9534Dev.readRegister(TCA9534_REG_ADDR_CFG);
			if (config < 0)
				throw new IllegalStateException(
						"Failed to read configuration from address 0x" + String.format("%02x", TCA9534_REG_ADDR_CFG));

			System.out.println("IC2 Ready");
		}
	}
		
	public void keyPressed(KeyEvent e) {
			if (id==0) {
				if (e.getKeyCode() == KeyEvent.VK_W) {
					setYDirection(-5);
				}
				if (e.getKeyCode() == KeyEvent.VK_S) {
					setYDirection(5);
				}
			}
			if (id==1) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					setYDirection(-5);
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					setYDirection(5);
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

	 	//paddle.y = paddle.y+yDirection;
		//if (this.id==0)
		//	paddle.y=tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT1);
		//if (this.id==1)
		//	paddle.y=tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT2);
		System.out.println("Wert Paddle 0:"+tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT1));
		System.out.println("Wert Paddle 1:"+tca9534Dev.readRegister(TCA9534_REG_ADDR_OUT_PORT2));

		paddle.y *= 3.75;
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
				Thread.sleep(8);
			}
		} catch(Exception e) { System.err.println(e.getMessage()); }
	}


	

}
