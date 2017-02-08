package examples;

import opc.Animation;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;

/**
 * 
 */
public class TheaterLights extends Animation {

	public TheaterLights(int c) {
		color[0] = c;
	}

	public int color[] = {
			//row 0
			makeColor(255,100 ,0),
	};
	int N = 6;
	int rotat = 2;
	int state;
	long timePerCycle = 100L;
	boolean forward = true;
	
	
	
	/** Time for the next state change. */
	long changeTime;

	@Override
	public void reset(PixelStrip strip) {
		state = 0;
		changeTime = millis();
	}
	
	protected static final int FAST = 50; // twenty pixels per second
	protected static final int SLOW = 1000; // one pixel per second
	
	/**
	 * @param n value between -1.0 and 1.0;
	 */
	public void setValue(double n) {
		forward = (n >= 0.0);
		n = Math.abs(n);
		timePerCycle = Math.round(SLOW - (SLOW - FAST) * n);
		timePerCycle = Math.min(Math.max(FAST, timePerCycle), SLOW);
	}

	

	@Override
	public boolean draw(PixelStrip strip) {
		if (millis() < changeTime) { return false;}
			
		state = (state  + (forward ? -1 : 1)) % (N * rotat);
		for (int i=0; i<strip.getPixelCount(); i++)  {
			int j = (i+state+N) % (N * rotat);
			int c1 = color[0];
			strip.setPixelColor(i, j>=N ? color[0] : BLACK);
		}
		
		changeTime = millis() + timePerCycle;
		return true;
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
		String FC_SERVER_HOST = System.getProperty("fadecandy.server", "raspberrypi.local");
		int FC_SERVER_PORT = Integer.parseInt(System.getProperty("fadecandy.port", "7890"));
		int STRIP1_COUNT = Integer.parseInt(System.getProperty("fadecandy.strip1.count", "64"));
		
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, STRIP1_COUNT);
		System.out.println(server.getConfig());
		
		TheaterLights a = new TheaterLights(0x0000DD);
		a.setValue(0.8);
		strip1.setAnimation(a);
		
		for (int i=0; i<1000; i++) {
			server.animate();
			Thread.sleep(FAST/2);
		}
		
		strip1.clear();
		server.show();
		server.close();
	}

}
