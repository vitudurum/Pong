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


public class Paddle implements Runnable {

    int x, y, yDirection;
    public int id;
    Rectangle paddle;
    Ball ball;
    boolean startGame = false;

    //private boolean kick=false;
    public Paddle(int x, int y, int id, Ball b) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.ball = b;
        paddle = new Rectangle(x, y, Pong.paddle_width, Pong.paddle_height);

    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("ID:"+id);
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            startGame = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_1 && ball.getMode() == Ball.MODE_ANSPIEL_1) {
            ball.setBoostMode();

        }
        if (e.getKeyCode() == KeyEvent.VK_2 && ball.getMode() == Ball.MODE_ANSPIEL_2) {
            ball.setBoostMode();

        }

        if (id == 0) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                setYDirection(-10);
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                setYDirection(10);
            }
        }
        if (id == 1) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                setYDirection(-10);
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                setYDirection(10);
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            ball.startGame();
        }
    }

    public void keyReleased(KeyEvent e) {
        if (id == 0) {
            if (e.getKeyCode() == e.VK_W) {
                setYDirection(0);
            }
            if (e.getKeyCode() == e.VK_S) {
                setYDirection(0);
            }
        }
        if (id == 1) {
            if (e.getKeyCode() == e.VK_UP) {
                setYDirection(0);
            }
            if (e.getKeyCode() == e.VK_DOWN) {
                setYDirection(0);
            }
        }
    }

    public void setYDirection(int yDir) {
        yDirection = yDir;
    }

    public void setPaddleValue(double val) {
        //System.out.println("ID:"+id+":"+val);
        paddle.y = (int) val;

    }

    public void move() {

        //Keyboard
        paddle.y = paddle.y + yDirection;

        if (paddle.y <= Pong.border_Up)
            paddle.y = Pong.border_Up;

        if (paddle.y > Pong.border_Down - Pong.paddle_height + Pong.border_Up)
            paddle.y = Pong.border_Down - Pong.paddle_height + Pong.border_Up;

        //System.out.println("["+this.id+"]"+yDirection);
        //System.out.println("["+this.id+"]"+paddle.y);

    }

    public void draw(Graphics g) {

        if (id == 0) {
            g.setColor(Color.CYAN);
            g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
        }
        if (id == 1) {
            g.setColor(Color.pink);
            g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
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
