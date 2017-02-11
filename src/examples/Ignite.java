package examples;

import java.util.Random;

import opc.*;

public class Ignite extends Animation {

	int stage = 0;
	int ignite = 0;
	int ignite2 = 0;
	Random rand = new Random();
	int random;
	int count;

	public void reset(PixelStrip strip) {
		strip.clear();
		count = strip.getPixelCount();
		setTimeout(2);
		random = rand.nextInt(count);

	}

	public boolean draw(PixelStrip strip) {
		if (stage == 0) {
			for (int p = 0; p < (strip.getPixelCount()); p++) {
				strip.setPixelColor(p, 0x00cc00);
			}
			if (isTimedOut()) {
				stage = 1;
				setTimeout(count / 1.5);
			}
		}
		if (stage == 1) {
			strip.setPixelColor(random, 0xff0000);
			random = rand.nextInt(count);
			strip.setPixelColor(random, 0xcc3300);
			random = rand.nextInt(count);
			if (isTimedOut()) {
				stage = 2;
				setTimeout(count / 2);
			}

		}
		if (stage == 2) {
			strip.setPixelColor(random, 0xffffff);
			random = rand.nextInt(count);
			if (isTimedOut()) {
				stage = 3;
				setTimeout(count / 2);
				for (int p = 0; p < (strip.getPixelCount()); p++) {
					strip.setPixelColor(p, 0xffffff);
				}
			}

		}
		if (stage == 3) {
			strip.setPixelColor(random, 0x00cc00);
			random = rand.nextInt(count);
			if (isTimedOut()) {
				stage = 0;
				setTimeout(1);
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

		strip1.setAnimation(new Ignite());
		for (int i = 0; i < 1000; i++) {
			server.animate();
			Thread.sleep(100);
		}
		server.close();
	}

}
