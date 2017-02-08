package examples;

import opc.Animation;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;

public class TestColor extends Animation{
	public static final String FC_SERVER_HOST 
	= System.getProperty("fadecandy.server", "raspberrypi.local");
	
	public static final int FC_SERVER_PORT 
	= Integer.parseInt(System.getProperty("fadecandy.port", "7890"));
	long timeCycle = 2000;

	public int color[] = { 
			makeColor(0, 128, 20),
			makeColor(0, 255, 255),
			makeColor(0, 100, 0)
	};
	int colorLen = color.length;
	int row = 0;

	public void setValue(double n){
		row = (int)n;
	}
	
	public void reset(PixelStrip strip) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean draw(PixelStrip strip) {
		for(int i = 0; i < strip.getPixelCount(); i++){
		strip.setPixelColor(i ,color[1]);
		}
		return true;
	}
public static void main(String[] args) throws Exception {
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 64);
		PixelStrip strip2 = fadeCandy.addPixelStrip(1, 8);
		PixelStrip strip3 = fadeCandy.addPixelStrip(2, 16);
		
		Animation a = new TestColor();
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


