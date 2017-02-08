package examples;

import org.firebears.lights.LightsMain;

import opc.Animation;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;

/**
 * Example animation that pulses all pixels through an array of colors.
 */
public class Pulsing extends Animation {


	/** Milliseconds for each pulse cycle. */
	long timeCycle = 2000;
	public int color[] = new int[2];
	int colorLen = color.length;

	public Pulsing() { this.setColor(-1); }
	public Pulsing(int colors)  { this.setColor(colors); }

	public void setColor(int colors) {
		switch (colors) {
			case LightsMain.CS_RED:
				color[0] = 0xFF0000;
				color[1] = 0xBB0000;
				break;
			case LightsMain.CS_BLUE:
				color[0] = 0x0000BB;
				color[1] = 0x0000FF;
				break;
			case LightsMain.CS_YELLOW:
				color[0] = 0xFFBB00;
				color[1] = 0xFF8800;
				break;
			case LightsMain.CS_RED_YELLOW:
				color[0] = 0xFF0000;
				color[1] = 0xFFAA00;
				break;
			case LightsMain.CS_RED_WHITE:
				color[0] = 0xFF0000;
				color[1] = 0xFFFFFF;
				break;
			case LightsMain.CS_WHITE:
				color[0] = 0xFFFFFF;
				color[1] = 0xBBBBBB;
				break;
			case LightsMain.CS_GREEN_WHITE:
				color[0] = 0x888888;
				color[1] = 0x0000FF;
				break;
			case LightsMain.CS_R_W_B:
				color = new int[3];
				color[0] = 0xFF0000;
				color[1] = 0x0000FF;
				color[2] = 0x888888;
				break;
			case LightsMain.CS_WHITE2:
				color = new int[3];
				color[0] = 0x888888;
				color[1] = 0x888888;
				color[2] = 0x888888;
				break;
			default:
				color[0] = makeColor(0, 128, 0); // Green
				color[1] = makeColor(64, 64, 0); // Yellow
				break;
		}
		colorLen = color.length;
	}


	@Override
	public void reset(PixelStrip strip) {
		g_fade = 255;
//		color[0] = makeColor(0, 128, 0); // Green
//		color[1] = makeColor(64, 64, 0); // Yellow
		colorLen = color.length;
	}

	@Override
	public boolean draw(PixelStrip strip) {
		long currentTime = millis() % timeCycle;
		for (int p = 0; p < strip.getPixelCount(); p++) {
			int color_num = p % colorLen;
			int timeShift = (int)(color_num * (timeCycle / colorLen));
			int brightness = pulseOverTime((currentTime + timeShift) % timeCycle);
			int c1 = color[color_num];
			int c2 = fadeColor(c1, brightness);
			setPix(strip, p, c2);
		}
		return true;
	}




	/**
	 * Return a brightness value as a function of time. The input value is the
	 * number of milliseconds into the cycle, from zero to timeCycle.
	 * Cycle over a sine function, so it's nice and smooth.
	 *
	 * @param timeNow time within the cycle.
	 * @return brightness value from 0 to 255
	 */
	private int pulseOverTime(long timeNow) {
	  double theta = 6.283 * timeNow / timeCycle;   // Angle in radians
	  double s = (Math.sin(theta) + 1.0) / 2.0;     // Value from 0.0 to 1.0
	  return (int)Math.round(s * 256);
	}

	protected final int FAST = 1000; // One cycle every second
	protected final int SLOW = 3000; // One cycle every three seconds

	/**
	 * @param n value between -1.0 and 1.0;
	 */
	public void setValue(double n) {
		n = Math.abs(n);
		timeCycle = Math.round(SLOW - (SLOW - FAST) * n);
		timeCycle = Math.min(Math.max(FAST, timeCycle), SLOW);
	}


	public static void main(String[] args) throws Exception {
		String FC_SERVER_HOST = System.getProperty("fadecandy.server", "raspberrypi.local");
		int FC_SERVER_PORT = Integer.parseInt(System.getProperty("fadecandy.port", "7890"));
		int STRIP1_COUNT = Integer.parseInt(System.getProperty("fadecandy.strip1.count", "512"));
		int PIXELSTRIP_PIN = Integer.parseInt(System.getProperty("pixelStrip", "-1"));

		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		if (PIXELSTRIP_PIN>=0) { server.setSingleStripNum(PIXELSTRIP_PIN); }
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, STRIP1_COUNT);
		System.out.println(server.getConfig());

		Animation a = new Pulsing();
		a.setValue(0.5);
		strip1.setAnimation(a);

		for (int i = 0; i < 1000; i++) {
			server.animate();
			Thread.sleep(100);
		}

		server.clear();
		server.show();
		server.close();
	}

}
