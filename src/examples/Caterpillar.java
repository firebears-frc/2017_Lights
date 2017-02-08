package examples;

import opc.Animation;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;

import org.firebears.lights.LightsMain;

public class Caterpillar extends Animation{

	public final int DIR_NO = 0;
	public final int DIR_FORWARD = 1;
	public final int DIR_BACKWARD = 2;

	double speed = 1.0;
	int colors;
	int colorsa[] = new int[2];
	int progr = 1;

	int change = 0;

	long prevtime = 0;

	public void setColor(int color) {
		colors = color;
		switch (colors) {
			case LightsMain.CS_BLUE:
				colorsa[0] = 0x0000BB;
				colorsa[1] = 0x0000FF;
				break;
			case LightsMain.CS_RED:
				colorsa[0] = 0xFF0000;
				colorsa[1] = 0xBB0000;
				break;
			case LightsMain.CS_RED_WHITE:
				colorsa[0] = 0xFF0000;
				colorsa[1] = 0xFFFFFF;
				break;
			case LightsMain.CS_RED_YELLOW:
				colorsa[0] = 0xFF0000;
				colorsa[1] = 0xFFAA00;
				break;
			case LightsMain.CS_WHITE:
				colorsa[0] = 0xFFFFFF;
				colorsa[1] = 0xBBBBBB;
				break;
			case LightsMain.CS_YELLOW:
				colorsa[0] = 0xFFBB00;
				colorsa[1] = 0xFF8800;
				break;
			default:
				colorsa[0] = 0xFF0000;
				colorsa[1] = 0xBB0000;
				break;
		}
	}

	public void setValue(double n) {
		speed = n;
	}

	@Override
	public void reset(PixelStrip strip) {
		g_fade = 255;
		colorsa[0] = 0xFF0000;
		colorsa[1] = 0xBBBBBB;
		speed = -1.0;
	}

	@Override
	public boolean draw(PixelStrip strip) {
		if( speed == 0 ) {
			return false;
		}
		//Speed = 0 - does nothing
		//Speed = .1 - every 4700 mpc //Milliseconds per cycle - 4.7 seconds
		//Speed = .5 - every 2700 mpc //2.7 seconds
		//Speed = 1. - every 200 mpc //1/5 second
		//If time that has passed > time that should have passed
		if(millis() - prevtime > (5000 - (speed *  5000)) + 200) {
			prevtime = millis();
			if(speed > 0.0) {
				progr++;
				if(progr > 4) {
					progr = 0;
				}
			}else if(speed < 0.0) {
				progr--;
				if(progr < 0) {
					progr = 3;
				}
			}

			for (float p = 0; p < strip.getPixelCount(); p++) {
				strip.setPixelColor((int)p, colorsa[((int)((p+progr)/2)%2)]);
			}
			return true;
		}else{
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		String FC_SERVER_HOST = System.getProperty("fadecandy.server", "raspberrypi.local");
		int FC_SERVER_PORT = Integer.parseInt(System.getProperty("fadecandy.port", "7890"));

		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 512);
//		PixelStrip strip1 = fadeCandy.addPixelStrip(2, 16);
		System.out.println(server.getConfig());

		Animation a = new Caterpillar();
		a.setValue(1.0);
		strip1.setAnimation(a);

		for (int i=0; i<10000; i++) {
			server.animate();
			Thread.sleep(5);
		}

		server.close();
	}

}
