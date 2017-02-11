package opc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Fake {@link OpcClient} that doesn't really communicate through a socket. This
 * is used purely for unit tests.
 */
public class OpcClientMock extends OpcClient {

	public static final int FC_SERVER_PORT = 7890;
	public static final String FC_SERVER_HOST = "localhost";
	
	int calledOpen = 0;
	int calledWrite = 0;
	int calledSendFirmware = 0;
	int calledSetPixel = 0;
	Map<Integer,Integer> pixelSet = new TreeMap<Integer,Integer>();

	public OpcClientMock() {
		super(FC_SERVER_HOST, FC_SERVER_PORT);
	}
	
	public OpcClientMock(String hostname, int portNumber) {
		super(hostname, portNumber);
	}

	/**
	 * Don't really open a Socket.
	 */
	@Override
	protected void open() {
		if (this.output == null) {
			output = new ByteArrayOutputStream();
		}
		calledOpen++;
	}
	
	@Override
	protected void sendFirmwareConfigPacket() {
		super.sendFirmwareConfigPacket();
		calledSendFirmware++;
	}

	@Override
	protected void writePixels(byte[] packetData) {
		super.writePixels(packetData);
		calledWrite++;
	}
	
	@Override
	protected void setPixelColor(int pixelNumber, int color) {
		super.setPixelColor(pixelNumber, color);
		calledSetPixel++;
		pixelSet.put(pixelNumber, color);
	}

	byte[] getByteArray() {
		try {
			output.flush();
		} catch (IOException iox) {
			throw new RuntimeException(iox);
		}
		return ((ByteArrayOutputStream) output).toByteArray();
	}
	
	public String[] parseDeviceMap() {
		String json = this.getConfig();
		int i = json.indexOf("\"map\": [") + 9;
		int j = json.indexOf("\t\t\t\t]\n", i);
		String deviceMapString = json.substring(i, j).trim();
		return deviceMapString.split("[,]?\n[\\s]*");
	}
}
