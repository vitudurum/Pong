package ch.vitudurum;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;


public class Pong extends JFrame  {

    //screen size variables.
    //static int gWidth = 1760;
    //static int gHeight = 1060;
    static int gWidth = 1260;
    static int gHeight = 1060;
    static int border_Up = 35;
    static int border_Down = gHeight - 50;
    static int border_Left = 0;
    static int border_Right = gWidth;
    static int paddle_height = 180;
    static int paddle_width = 30;
    Dimension screenSize = new Dimension(gWidth, gHeight);
    static ADCReader adc;
    Image dbImage;
    Graphics dbGraphics;
    Font stringFont = new Font("SansSerif", Font.PLAIN, 180);
    //ball object
    Ball b;
    Rectangle border;
    //String NamePLayer1;
    //String NamePLayer2;
    static public Sound sound;
    Graphics aa;
    Thread ball,p1,p2,a;
    //constructor for window
    public Pong() {
      //  try {
            //sound = new Sound();
            //sound.play();
            b = new Ball(this,250, 250);
            adc = new ADCReader(this);

        //create and start threads.
            ball= new Thread(b);
            p1 = new Thread(b.p1);
            p2 = new Thread(b.p2);
            a = new Thread(adc);
            ball.start();
            p2.start();
            p1.start();
            a.start();
            /*
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        */

		/*
		Scanner scanner = new Scanner(System.in);
		System.out.print("Name Player 1:");
		NamePLayer1=scanner.nextLine();
		System.out.print("Name Player 2:");
		NamePLayer2=scanner.nextLine();
*/

        border = new Rectangle(0, 0, 100, 100);
        this.setTitle("Marc's Pong!");
        this.setSize(screenSize);
        this.setResizable(false);
        this.setVisible(true);
        this.setBackground(Color.DARK_GRAY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new AL());

    }

    public static void main(String[] args) {

        Pong pg = new Pong();
    }

    @Override
    public void paint(Graphics g) {
        aa=g;
        dbImage = createImage(getWidth(), getHeight());
        dbGraphics = dbImage.getGraphics();
        draw(dbGraphics);
        g.drawImage(dbImage, 0, 0, this);
    }

    public void draw(Graphics g) {
        b.draw(g);
        b.p1.draw(g);
        b.p2.draw(g);
        g.setFont(stringFont);
        g.setColor(Color.WHITE);
        g.drawString(b.p1score + ":" + b.p2score, this.getWidth() / 2 - 80, this.getHeight() / 2);
        g.drawRect(8, 35, getWidth() - 18, getHeight() - 50);
        repaint();
       // System.out.print(".");
    }


    public class AL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            b.p1.keyPressed(e);
            b.p2.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            b.p1.keyReleased(e);
            b.p2.keyReleased(e);
        }

    }


    public Ball getBall() {
        return b;
    }

    static public void PlaySound()
    {
        sound.play();
    }
}
