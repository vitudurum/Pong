package ch.vitudurum;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;


public class Pong extends JFrame {
	
	//screen size variables.
	static int gWidth = 1920;
	static int gHeight = 1080;
	Dimension screenSize = new Dimension(gWidth, gHeight);

	Image dbImage;
	Graphics dbGraphics;
	Font stringFont = new Font( "SansSerif", Font.PLAIN, 180 );
	//ball object
	static Ball b = new Ball(250, 200);
	Rectangle border;
	//String NamePLayer1;
	//String NamePLayer2;
	
	
	//constructor for window
	public Pong() {
		/*
		Scanner scanner = new Scanner(System.in);
		System.out.print("Name Player 1:");
		NamePLayer1=scanner.nextLine();
		System.out.print("Name Player 2:");
		NamePLayer2=scanner.nextLine();
*/

		border = new Rectangle(0,0, 100, 100);
		this.setTitle("Maege's Pong!");
		this.setSize(screenSize);
		this.setResizable(false);
		this.setVisible(true);
		this.setBackground(Color.DARK_GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(new AL());
	}
	
	public static void main(String[] args) {


		Pong pg = new Pong();
		
		//create and start threads.
		Thread ball = new Thread(b);
		ball.start();
		Thread p1 = new Thread(b.p1);
		Thread p2 = new Thread(b.p2);
		p2.start();
		p1.start();
		
	}
	
	@Override
	public void paint(Graphics g) {
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
		g.drawString(b.p1score+":"+b.p2score , this.getWidth()/2-80, this.getHeight()/2);


		g.drawRect(10,10,getWidth()-20,getHeight()-20);
		//g.drawString(""+b.p2score, 18, 50);

		//int oldStroke=g.gets
		//g.setColor(Color.red);
	    //g.drawRect(0,0,getWidth(),getHeight());


		repaint();

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
}
