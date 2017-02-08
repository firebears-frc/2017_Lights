package examples;

import opc.Animation;
import opc.Foreground;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;

/**
 * Example animation that makes a pixel move across the strip.
 */
public class MovingPixel extends Foreground {
	
	/** Host name or IP address of the Fadecandy server. */
	public static final String FC_SERVER_HOST 
		= System.getProperty("fadecandy.server", "raspberrypi.local");
	
	/** Port number of the Fadecandy server. */
	public static final int FC_SERVER_PORT 
		= Integer.parseInt(System.getProperty("fadecandy.port", "7890"));

	int currentPixel;
	long timePerPixel = 200L;
	int color;
	
	/** Time for the next state change. */
	long changeTime;
	
	public MovingPixel(int c) {
		color = c;
	}

	@Override
	public void reset(PixelStrip strip) {
		prepare(strip);
		currentPixel = 0;
		changeTime = millis();
	}

	@Override
	public boolean draw(PixelStrip strip) {
		if (millis() < changeTime) { return false; }
		
//		setPix(strip, currentPixel, BLACK);
		draw_bg();
		
		currentPixel = currentPixel + 1;
		if (currentPixel >= strip.getPixelCount()) {
			currentPixel = 0;
		}
		setPix(strip, currentPixel, color);
		
		changeTime = millis() + timePerPixel;
		return true;
	}
	
	protected final int FAST = 100; // ten pixels per second
	protected final int SLOW = 500; // two pixels per second

	/**
	 * @param n value between -1.0 and 1.0;
	 */
	public void setValue(double n) { 
		n = Math.abs(n);
		timePerPixel = Math.round(SLOW - (SLOW - FAST) * n);
		timePerPixel = Math.min(Math.max(FAST, timePerPixel), SLOW);
	}
	
	
	public static void main(String[] args) throws Exception {
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		
		// Configure for three separate pixel strips
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 64);  // 8 x 8 grid on pin 0
		PixelStrip strip2 = fadeCandy.addPixelStrip(1, 8);   // 8 pixel strip on pin 1
		PixelStrip strip3 = fadeCandy.addPixelStrip(2, 16);  // 16 pixel ring on pin 2
		
//		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 8);  // 8 x 8 grid on pin 0
//		PixelStrip strip2 = fadeCandy.addPixelStrip(0, 16);   // 8 pixel strip on pin 1
//		PixelStrip strip3 = fadeCandy.addPixelStrip(0, 24);  // 16 pixel ring on pin 2
		
		// Since the pixels are not uniform strips of 64, customize 
		// the server config JSON file with the following:
		System.out.println(server.getConfig());
		
		// Set each strip to show a different animation
		strip1.setAnimation(new MovingPixel(0x0000FF));
		strip2.setAnimation(new Spark());
		strip3.setAnimation(new Pulsing());

		
		for (int i=0; i<1000; i++) {
			server.animate();
			Thread.sleep(50);
		}
		
		server.clear();
		server.show();
		server.close();
	}

}
