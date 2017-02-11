package examples;

import java.util.Random;

import opc.*;

public class SweeperBackward extends Animation {

	int count;
	int q = 0;
	int d = count;
	int w = count;
	int e = count;
	int r = count;

	public void reset(PixelStrip strip) {
		strip.clear();
		count = strip.getPixelCount();
		setTimeout(0.1);
		d = count - 1;
		w = count - 4;
		e = count - 3;
		r = count - 2;
	}

	public boolean draw(PixelStrip strip) {
		if (isTimedOut()) {
			for (int p = 0; p < (strip.getPixelCount()); p++) {
				strip.setPixelColor(p, 0x0033cc);
			}
			strip.setPixelColor(w, 0xff3300);
			w = w - 1;
			strip.setPixelColor(e, 0xff9933);
			e = e - 1;
			strip.setPixelColor(r, 0xffcc00);
			r = r - 1;
			strip.setPixelColor(d, 0xffff00);
			d = d - 1;
			if (w <= 0) {
				w = count - 1;
			}
			if (e <= 0) {
				e = count - 1;
			}
			if (r <= 0) {
				r = count - 1;
			}
			if (d <= 0) {
				d = count - 1;
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

		strip1.setAnimation(new SweeperBackward());
		for (int i = 0; i < 1000; i++) {
			server.animate();
			Thread.sleep(100);
		}
		server.close();
	}

}
