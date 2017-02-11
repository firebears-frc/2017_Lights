package opc;

import static org.junit.Assert.*;

import org.junit.Test;

public class OpcClientTest {
	
	/**
	 * Try clearing the server, but with zero strips attached.
	 */
	@Test
	public void testClear0() {
		OpcClientMock server = new OpcClientMock();
		assertFalse(server.initialized);
		assertEquals(0, server.calledOpen);
		assertEquals(0, server.calledWrite);
		
		server.clear();
		server.show();
		byte[] buffer = server.getByteArray();
		server.close();
		
		assertTrue(server.initialized);
		assertEquals(1, server.calledOpen);
		assertEquals("One write for server.show()", 1, server.calledWrite);
		assertEquals("Four header bytes", 4, buffer.length);
		assertEquals("No pixels set", 0, server.pixelSet.size());
	}
	
	/**
	 * Try clearing the server, with one strip attached.
	 */
	@Test
	public void testClear1() {
		OpcClientMock server = new OpcClientMock();
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 8); 
		
		server.clear();
		server.show();
		byte[] buffer = server.getByteArray();
		server.close();
		
		assertEquals(0, server.pixelSet.size());
		assertEquals("One write for server.show()", 1, server.calledWrite);
		assertEquals("Four header bytes plus three bytes per pixel", 28, buffer.length);
		byte[] expectedBuffer = {0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		for (int i=0; i<expectedBuffer.length; i++) {
			assertEquals(expectedBuffer[i], buffer[i]);
		}
		
		String config = server.getConfig();
		assertTrue(config.contains("[0, 0, 0, 8 ]"));
	}

	/**
	 * Set pixels, with one strip attached.
	 */
	@Test
	public void testSet1() {
		OpcClientMock server = new OpcClientMock();
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 8); 
		
		strip1.setPixelColor(0, 0x440000);
		strip1.setPixelColor(1, 0x440000);
		strip1.setPixelColor(3, 0x444400);
		strip1.setPixelColor(4, 0x444400);
		strip1.setPixelColor(6, 0x000088);
		strip1.setPixelColor(7, 0x000088);
		server.show();
		byte[] buffer = server.getByteArray();
		server.close();
		
		assertEquals(6, server.pixelSet.size());
		assertEquals(1, server.calledWrite);
		assertEquals(28, buffer.length);
		assertEquals((Integer)0x440000, server.pixelSet.get(0));
		assertEquals((Integer)0x444400, server.pixelSet.get(3));
		assertEquals((Integer)0x000088, server.pixelSet.get(7));
		byte[] expectedBuffer = {0, 0, 0, 24, 68, 0, 0, 68, 0, 0, 0, 0, 0, 68, 68, 0, 
				68, 68, 0, 0, 0, 0, 0, 0, -120, 0, 0, -120};
		for (int i=0; i<expectedBuffer.length; i++) {
			assertEquals(expectedBuffer[i], buffer[i]);
		}
	}
	

	/**
	 * Set pixels, for multiple strips on one server.
	 */
	@Test
	public void testSet2() {
		OpcClientMock server = new OpcClientMock();
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, 8); 
		PixelStrip strip2 = fadeCandy.addPixelStrip(1, 16); 
		PixelStrip strip3 = fadeCandy.addPixelStrip(2, 64); 
		
		strip1.setPixelColor(0,  0x000022);
		strip1.setPixelColor(3,  0x000044);
		strip1.setPixelColor(7,  0x000066);
		strip2.setPixelColor(0,  0x002200);
		strip2.setPixelColor(10, 0x004400);
		strip2.setPixelColor(15, 0x006600);
		strip3.setPixelColor(0,  0x220000);
		strip3.setPixelColor(35, 0x440000);
		strip3.setPixelColor(63, 0x660000);
		
		server.show();
		byte[] buffer = server.getByteArray();
		server.close();
		
		assertEquals(9, server.pixelSet.size());
		assertEquals(1, server.calledWrite);
		assertEquals(268, buffer.length);
		assertEquals((Integer)0x000022, server.pixelSet.get(0));
		assertEquals((Integer)0x000044, server.pixelSet.get(3));
		assertEquals((Integer)0x000066, server.pixelSet.get(7));
		assertEquals((Integer)0x002200, server.pixelSet.get(8));
		assertEquals((Integer)0x004400, server.pixelSet.get(18));
		assertEquals((Integer)0x006600, server.pixelSet.get(23));
		assertEquals((Integer)0x220000, server.pixelSet.get(24));
		assertEquals((Integer)0x440000, server.pixelSet.get(59));
		assertEquals((Integer)0x660000, server.pixelSet.get(87));
		assertEquals(0x000044, server.getPixelColor(3));
		assertEquals(0x004400, server.getPixelColor(18));
		assertEquals(0x660000, server.getPixelColor(87));
		byte[] expectedBuffer = {0, 0, 1, 8, 0, 0, 34, 0, 0, 0, 0, 0, 0, 0, 0, 68, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 0, 34, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 0, 34, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102, 0, 0};
		for (int i=0; i<expectedBuffer.length; i++) {
			assertEquals(expectedBuffer[i], buffer[i]);
		}
		String[] devices = server.parseDeviceMap();
		assertEquals(3, devices.length);
		assertEquals("[0, 0, 0, 8 ]", devices[0]);
		assertEquals("[0, 8, 64, 16 ]", devices[1]);
		assertEquals("[0, 24, 128, 64 ]", devices[2]);
	}
	
}
