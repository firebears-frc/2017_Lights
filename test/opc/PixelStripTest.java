package opc;

import static org.junit.Assert.*;

import org.junit.Test;

public class PixelStripTest {

	/**
	 * Test JSON config string, for a single pixel strip attached to pin 0.
	 */
	@Test
	public void testConfig1() {
		OpcClientMock server = new OpcClientMock();		
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 8); 
		server.setVerbose(true);
		
		server.close();
		
		assertEquals(8, strip1.getPixelCount());
		assertEquals("[0, 0, 0, 8 ]", strip1.getConfig());
		String[] devices = server.parseDeviceMap();
		assertEquals(1, devices.length);
		assertEquals("[0, 0, 0, 8 ]", devices[0]);
	}
	
	/**
	 * Test JSON config string, for a single pixel strip attached to pin 1.
	 * The first output pixel now becomes 64.
	 */
	@Test
	public void testConfig2() {
		OpcClientMock server = new OpcClientMock();		
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(1, 8); 
		server.setVerbose(true);
		
		server.close();
		
		assertEquals(8, strip1.getPixelCount());
		assertEquals("[0, 0, 64, 8 ]", strip1.getConfig());
		String[] devices = server.parseDeviceMap();
		assertEquals(1, devices.length);
		assertEquals("[0, 0, 64, 8 ]", devices[0]);
	}
	
	/**
	 * Test JSON config string, for multiple pixel strips.
	 */
	@Test
	public void testConfig3() {
		OpcClientMock server = new OpcClientMock();		
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 8); 
		PixelStrip strip2 = fadeCandy.addPixelStrip(1, 16); 
		PixelStrip strip3 = fadeCandy.addPixelStrip(2, 64); 
		
		server.close();
		
		assertEquals(64, strip3.getPixelCount());
		
		assertEquals(strip1.getConfig(), "[0, 0, 0, 8 ]");
		assertEquals(strip2.getConfig(), "[0, 8, 64, 16 ]");
		assertEquals(strip3.getConfig(), "[0, 24, 128, 64 ]");
		
		String config = server.getConfig();
		String[] devices = server.parseDeviceMap();
		assertEquals(3, devices.length);
		assertTrue(config.contains("[0, 0, 0, 8 ]"));
		assertTrue(config.contains("[0, 24, 128, 64 ]"));
	}

}
