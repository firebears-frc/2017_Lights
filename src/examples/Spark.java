package examples;

import opc.Animation;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;

/**
 * Display a moving white pixel with trailing orange/red flames
 * This looks pretty good with a ring of pixels.
 */
public class Spark extends Animation {

	/** Colors of the chasing pixel. */
	int color[] = { makeColor(196, 196, 196), // White
			makeColor(128, 128, 0), // Yellow
			makeColor(96, 64, 0), // Yellow-orange
			makeColor(64, 32, 0), // orange
			makeColor(32, 0, 0), // red
			makeColor(16, 0, 0), // red
			makeColor(0, 0, 0), // black
	};

	int currentPixel;
	int numPixels;
	long timePerPixel = 100L;

	@Override
	public void reset(PixelStrip strip) {
		currentPixel = 0;
		numPixels = strip.getPixelCount();
		setTimeoutMillis(1);
	}

	@Override
	public boolean draw(PixelStrip strip) {
		if (! isTimedOut()) { return false; }
		for (int i = 0; i < color.length; i++) {
			strip.setPixelColor(pixNum(currentPixel, i), color[i]);
		}
		currentPixel = pixNum(currentPixel + 1, 0);
		setTimeoutMillis(timePerPixel);
		return true;
	}

	/**
	 * Return the pixel number that is i steps behind number p.
	 */
	int pixNum(int p, int i) {
		return (p + numPixels - i) % numPixels;
	}

	protected final int FAST = 50; // twenty pixels per second
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
		String FC_SERVER_HOST = System.getProperty("fadecandy.server", "raspberrypi.local");
		int FC_SERVER_PORT = Integer.parseInt(System.getProperty("fadecandy.port", "7890"));
		int STRIP1_COUNT = Integer.parseInt(System.getProperty("fadecandy.strip1.count", "64"));
		int PIXELSTRIP_PIN = Integer.parseInt(System.getProperty("pixelStrip", "-1"));

		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		if (PIXELSTRIP_PIN>=0) { server.setSingleStripNum(PIXELSTRIP_PIN); }
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, STRIP1_COUNT);
//		PixelStrip strip1 = fadeCandy.addPixelStrip(2, STRIP1_COUNT);
		System.out.println(server.getConfig());

		Animation a = new Spark();
		a.setValue(0.5);
		strip1.setAnimation(a);

		for (int i=0; i<10000; i++) {
			server.animate();
			Thread.sleep(5);
		}

		server.close();
	}
}
