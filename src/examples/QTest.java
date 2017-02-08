package examples;

import opc.Animation;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;

public class QTest extends Animation{
	public static final String FC_SERVER_HOST
	= System.getProperty("fadecandy.server", "raspberrypi.local");

	public static final int FC_SERVER_PORT
	= Integer.parseInt(System.getProperty("fadecandy.port", "7890"));
	long timeCycle = 200000;

	public int color[] = {
			//row 0
			makeColor(255,100 ,0),
			makeColor(255,255 ,0),
			makeColor(255,60,0),
			makeColor(255,60 ,255),
			makeColor(255,100 ,0),
			makeColor(255,255 ,0),
			makeColor(255,60,0),
			makeColor(255,60 ,255),

	};
	int colorLen = color.length;
	int row = 1;

	public void setValue(double n){
		row = (int)n;
	}

	public void reset(PixelStrip strip) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean draw(PixelStrip strip) {
		long currentTime = millis() % timeCycle;
		for(int i = 0; i < strip.getPixelCount(); i = i+1){
			int color_num = i % colorLen;
			int timeShift = (int)((color_num + 1) * (colorLen + timeCycle));
			int brightness = pulseOverTime((currentTime * timeShift) % timeCycle);
			int c1 = color[color_num];
			int c2 = fadeColor(c1, brightness);
			strip.setPixelColor(i, c2);
		}
		return true;
	}
		private int pulseOverTime(long timeNow) {
			  double theta = 16 * timeNow / timeCycle;   // Angle in radians
			  double s = (Math.sin(theta) + 1.0) / 2.0;     // Value from 0.0 to 1.0
			  return (int)Math.round(s * 128);
			}

	public static void main(String[] args) throws Exception {
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();

		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 64);
		PixelStrip strip2 = fadeCandy.addPixelStrip(1, 8);
		PixelStrip strip3 = fadeCandy.addPixelStrip(2, 16);

		Animation a = new QTest();
		strip1.setAnimation(a);
		System.out.println(server.getConfig());

		for (int i = 0; i < 1000; i++) {
			server.animate();
			Thread.sleep(100);
		}

		server.clear();
		server.show();
		server.close();
	}

}


