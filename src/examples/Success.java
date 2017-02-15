package examples;

import java.util.Random;

import opc.*;

public class Success extends Animation {

	int stage = 1;

	public Success() {
		// TODO Auto-generated constructor stub
	}

	public void setValue(double n) {
	}

	public void reset(PixelStrip strip) {
		strip.clear();
		setTimeout(0.5);
	}

	public boolean draw(PixelStrip strip) {
		if (stage == 1){
			if (isTimedOut()){
				stage = 2;
				for (int p = 0; p < strip.getPixelCount(); p++) {
					strip.setPixelColor(p, 0x00ff00);
				}
				setTimeout(0.5);
			}
		}
		if (stage == 2){
			if (isTimedOut()){
				for (int p = 0; p < strip.getPixelCount(); p++) {
					strip.setPixelColor(p, 0x000000);
				}
				setTimeout(0.5);
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

		strip1.setAnimation(new Success());
		for (int i = 0; i < 1000; i++) {
			server.animate();
			Thread.sleep(100);
		}
		server.close();
	}

}
