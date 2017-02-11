package examples;

import java.util.Random;

import opc.*;

public class Range extends Animation {

	double range;
	double stripSize = 32;

	public void setValue(double n) {
		range = Math.min(n, stripSize);
	}

	public void reset(PixelStrip strip) {
		strip.clear();
		stripSize = strip.getPixelCount();
	}

	public boolean draw(PixelStrip strip) {
		if (range <= strip.getPixelCount()) {
			for (int p = 0; p < (range); p++) {
				strip.setPixelColor(p, 0x00cc00);
			}
			for (int p = strip.getPixelCount() - 1; p >= (range); p--) {
				strip.setPixelColor(p, 0xff0000);
			}
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

		strip1.setAnimation(new Range());
		for (int i = 0; i < 1000; i++) {
			server.animate();
			Thread.sleep(100);
		}
		server.close();
	}

}
