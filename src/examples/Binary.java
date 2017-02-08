package examples;

import opc.Animation;
import opc.Foreground;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;


/**
 * Example animation that makes a pixel move across the strip.
 */
public class Binary extends Foreground {
	
	public static final int FC_SERVER_PORT = 7890;
	public static final String FC_SERVER_HOST = "raspberrypi.local";
	public static final int BYTE_LEN = 8;
	public static long startingmillis = 0;
	
	int timepass = 0;
	long prevtime = 0;
	
	boolean bits[];
	boolean useAnim = false;
	double accel = 15000 / 255;

	int currentPixel;
	long timePerPixel = 200L;
	//Red, Yellow, Orange, Purple, White, 
	int colors[] = new int[] {
			0x000000, 0x00FFFF
	};
	
	/** Time for the next state change. */
	long changeTime;
	
	public Binary() {
	}

	@Override
	public void reset(PixelStrip strip) {
		prepare(strip);
		currentPixel = 0;
		changeTime = millis();
		bits = new boolean[strip.getPixelCount()];
		useAnim = false;
		startingmillis = millis();
	}
	
	public int mixColor(int c1, int c2, float percentOfOne) {
		float percentOfTwo = 1.f - percentOfOne;
		return makeColor(
			(int)((getRed(c1) * percentOfOne) + (getRed(c2) * percentOfTwo)) / 2,
			(int)((getGreen(c1) * percentOfOne) + (getGreen(c2) * percentOfTwo)) / 2,
			(int)((getBlue(c1) * percentOfOne) + (getBlue(c2) * percentOfTwo)) / 2);
	}
	
	public long getmillis() {
		return millis() - startingmillis;
	}
	
	public void add_to() {
		for (int p = 0; p < BYTE_LEN; p++) { //until hits 0
			if(bits[p]) {
				bits[p] = false;
			}else{
				bits[p] = true;
				return;
			}
		}
		//change animation
		useAnim = true;
		if (background!=null) {
			background.g_fade = 255;
			background.reset(g_strip);
		}
		System.out.println("time is: "+getmillis());
		startingmillis = millis();
	}

	@Override
	public boolean draw(PixelStrip strip) {
		int a, ct1, ct2;
		draw_bg();
		if(useAnim) { return true; }
		if(getmillis() > prevtime + accel) {
			prevtime += accel;
			add_to(); //add
		}
		
		for (int p = 0; p < strip.getPixelCount(); p++) {
			if(bits[p%BYTE_LEN])
				strip.setPixelColor(p, colors[1]);
//			else
//				strip.setPixelColor(p, colors[0]);
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		
		// Configure for three separate pixel strips
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 64);  // 8 x 8 grid on pin 0
		PixelStrip strip2 = fadeCandy.addPixelStrip(1, 8);   // 8 pixel strip on pin 1
		PixelStrip strip3 = fadeCandy.addPixelStrip(2, 16);  // 16 pixel ring on pin 2

		// Since the pixels are not uniform strips of 64, customize 
		// the server config JSON file with the following:
		System.out.println(server.getConfig());
		
		// Set each strip to show a different animation
		strip1.setAnimation(new Binary());
		strip2.setAnimation(new Binary());
		strip3.setAnimation(new Binary());

		System.out.println("starting anim...");
		
		while (true) {
			server.animate();
		}
		
//		server.clear();
//		server.show();
//		server.close();
	}

}
