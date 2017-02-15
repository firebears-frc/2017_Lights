package examples;

import java.util.Random;

import opc.*;

public class High extends Animation {

	int high1;
	int high2;
	public High() {
		// TODO Auto-generated constructor stub
	}

	public void setValue(double n) {
	}

	public void reset(PixelStrip strip) {
		strip.clear();
		setTimeout(0.5);
		high2 = strip.getPixelCount() / 10;
		high1 = strip.getPixelCount() - high2;
	}

	public boolean draw(PixelStrip strip) {
				for (int p = 0; p < high2; p++) {
					strip.setPixelColor(p, 0xff0000);
				}
				for (int p = high1; p <= strip.getPixelCount(); p++) {
					strip.setPixelColor(p, 0x00ff00);
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

		strip1.setAnimation(new High());
		for (int i = 0; i < 1000; i++) {
			server.animate();
			Thread.sleep(100);
		}
		server.close();
	}

}
