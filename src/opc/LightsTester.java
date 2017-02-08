package opc;

import java.util.*;

/**
 * Simple program to test if a light strip works.
 * 
 */
public class LightsTester {

	public static final String FC_SERVER_HOST = System.getProperty(
			"fadecandy.server", "127.0.0.1");

	public static final int FC_SERVER_PORT = Integer.parseInt(System
			.getProperty("fadecandy.port", "7890"));
	
	public static final int STRIP1_COUNT = Integer.parseInt(System
			.getProperty("fadecandy.strip1.count", "64"));

	static List<PixelStrip> stripList = new ArrayList<PixelStrip>();
	static List<Integer> colorList = new ArrayList<Integer>();

	
	
	public static void main(String[] args) throws InterruptedException {
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		int totalPixels = 0;
		{
			PixelStrip strip = null;
	
			strip = fadeCandy.addPixelStrip(0, STRIP1_COUNT);
			colorList.add(0x660000);
			stripList.add(strip);
			totalPixels += strip.getPixelCount();
	
//			strip = fadeCandy.addPixelStrip(1, 16);
//			colorList.add(0x006600);
//			stripList.add(strip);
//			totalPixels += strip.getPixelCount();
		}

		long timePerPixel = 250;


		System.out.println("# total pixels = " + totalPixels);
		System.out.println(server.getConfig());

		for (int j = 0; j < 100; j++) {
			int n = 0;
			for (int i = 0; i < stripList.size(); i++) {
				PixelStrip strip = stripList.get(i);
				int color = colorList.get(i);
				for (int pixelNumber = 0; pixelNumber < strip.getPixelCount(); pixelNumber++) {
					server.clear();
					strip.setPixelColor(pixelNumber, color);
					server.show();
					System.out.println(i + "\t" + pixelNumber + "\t" + n);
					Thread.sleep(timePerPixel);
					n++;
				}
			}

			for (int i=0; i<totalPixels; i++) {
				server.clear();
				server.setPixelColor(i, 0x444444);
				server.show();
				System.out.println("\t\t" + i);
				Thread.sleep(timePerPixel);
			}
		}
		
		server.clear();
		server.show();
		server.close();
	}

}
