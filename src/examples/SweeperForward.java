package examples;

import java.util.Random;

import opc.*;

public class SweeperForward extends Animation {

	int count;
	int q = 0;
	int d = 0;
	int w = 3;
	int e = 2;
	int r = 1;

	public void reset(PixelStrip strip) {
		strip.clear();
		count = strip.getPixelCount();
		setTimeout(0.1);
	}

	public boolean draw(PixelStrip strip) {
		if (isTimedOut()){
		for (int p = 0; p < (strip.getPixelCount()); p++) {
			strip.setPixelColor(p, 0x0033cc);
		}
		strip.setPixelColor(w, 0xff3300);
		w = w + 1;
		strip.setPixelColor(e, 0xff9933);
		e = e + 1;
		strip.setPixelColor(r, 0xffcc00);
		r = r + 1;
		strip.setPixelColor(d, 0xffff00);
		d = d + 1;
		if (w == count){
			w = 0;
		}
		if (e == count){
			e = 0;
		}
		if (r == count){
			r = 0;
		}
		if (d == count){
			d = 0;
		}
		setTimeout(0.2);
		}
		return true;

	}

	public static void main(String[] args) throws Exception {
		final String FC_SERVER_HOST = System.getProperty("fadecandy.server", "raspberrypi.local");
		final int FC_SERVER_PORT = Integer.parseInt(System.getProperty("fadecandy.port", "7890"));
		final int STRIP1_COUNT = Integer.parseInt(System.getProperty("fadecandy.strip1.count", "8"));
		final int PIXELSTRIP_PIN = Integer.parseInt(System.getProperty("pixelStrip", "0"));
		final boolean VERBOSE = "true".equalsIgnoreCase(System.getProperty("verbose", "false"));

		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		server.setVerbose(VERBOSE);
		server.setSingleStripNum(PIXELSTRIP_PIN);
		OpcDevice fadeCandy = server.addDevice();

		PixelStrip strip1 = fadeCandy.addPixelStrip(PIXELSTRIP_PIN, STRIP1_COUNT);

		strip1.setAnimation(new SweeperForward());
		for (int i = 0; i < 1000; i++) {
			server.animate();
			Thread.sleep(100);
		}
		server.close();
	}

}
