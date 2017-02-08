package org.firebears.lights;

import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import examples.Binary;
import examples.Caterpillar;
import examples.Exploding;
import examples.Exploding_R_W_B;
import examples.Fire;
import examples.LiftLights;
import examples.MovingPixel;
import examples.Pulsing;
import examples.Spark;
import examples.TheaterLights;
import examples.Crazy;

/**
 * This program allows the robot to control lights connected
 * to the Fadecandy server.  The robot will make changes into
 * the "lights" network table.  This program will detect those
 * changes and cause the animations to change on the pixel
 * strips.
 * <p>
 * This program can run on any computer in the robot's
 * subnet.  It may run on the same Raspberry Pi where the
 * Fadecandy server is running.
 */
public class LightsMain {

	// Constants for pixel strips
	public static final String STRIP_CHASSIS_LEFT = "strip_chassis_left";
	public static final String STRIP_CHASSIS_RIGHT = "strip_chassis_right";
	public static final String STRIP_CELEBRATE = "strip_celebrate";

	// Constants for  animations
	public static final String ANIM_PULSING_GREEN = "ANIM_PULSING_GREEN";
	public static final String ANIM_PULSING_RED = "ANIM_PULSING_RED";
	public static final String ANIM_PULSING_BLUE = "ANIM_PULSING_BLUE";
	public static final String ANIM_PULSING_R_W_B = "ANIM_PULSING_R_W_B";
	public static final String ANIM_EXPLODING_R_W_B = "ANIM_EXPLODING_R_W_B";
	public static final String ANIM_MOVING_BLUE = "ANIM_MOVING_BLUE";
	public static final String ANIM_FIRE = "ANIM_FIRE";
	public static final String ANIM_CRAZY = "ANIM_CRAZY";
	public static final String ANIM_BINARY = "ANIM_BINARY";
	public static final String ANIM_BULB = "ANIM_BULB";
	public static final String ANIM_CATERPILLAR = "ANIM_CATERPILLAR";
	public static final String ANIM_SPARK = "ANIM_SPARK";
	public static final String ANIM_THEATER = "ANIM_THEATER";
	public static final String ANIM_EXPLODE = "ANIM_EXPLODE";

	//Color Schemes
	public static final int CS_RED = 0;
	public static final int CS_BLUE = 1;
	public static final int CS_YELLOW = 2;
	public static final int CS_RED_YELLOW = 3;
	public static final int CS_RED_WHITE = 4;
	public static final int CS_WHITE = 5;
	public static final int CS_GREEN_WHITE = 6;
	public static final int CS_R_W_B = 7;
	public static final int CS_WHITE2 = 8;

	/** Host name or IP address of the Network Table server. */
	public static final String NT_SERVER_HOST
		= System.getProperty("networkTable.server", "roborio-2846-frc.local");

	/** Host name or IP address of the Fadecandy server. */
	public static final String FC_SERVER_HOST
		= System.getProperty("fadecandy.server", "raspberrypi.local");

	/** Port number of the Fadecandy server. */
	public static final int FC_SERVER_PORT
		= Integer.parseInt(System.getProperty("fadecandy.port", "7890"));

	/** Whether to display extra information about internal processes. */
	public static final boolean VERBOSE
		= "true".equals(System.getProperty("verbose", "false"));

	private static TableWatcher initializePixelStripAnimations(
		OpcDevice fadeCandy, NetworkTable table,
		int pin, int len, String name)
	{
		PixelStrip strip = fadeCandy.addPixelStrip(pin, len, name);
		TableWatcher watcher = new TableWatcher(name, strip);

		watcher.addAnimation(ANIM_PULSING_GREEN, new Pulsing());
		watcher.addAnimation(ANIM_PULSING_RED, new Pulsing(CS_RED_YELLOW));
		watcher.addAnimation(ANIM_PULSING_BLUE, new Pulsing(CS_BLUE));
		watcher.addAnimation(ANIM_MOVING_BLUE, new MovingPixel(0x0000FF));
		watcher.addAnimation(ANIM_EXPLODING_R_W_B, new Exploding_R_W_B());
		watcher.addAnimation(ANIM_FIRE, new Fire());
		watcher.addAnimation(ANIM_CRAZY, new Crazy());
		watcher.addAnimation(ANIM_BINARY, new Binary());
		watcher.addAnimation(ANIM_CATERPILLAR, new Caterpillar());
		watcher.addAnimation(ANIM_SPARK, new Spark());
		watcher.addAnimation(ANIM_THEATER, new TheaterLights(0xFFAA00));
		watcher.addAnimation(ANIM_EXPLODE, new Exploding());

		table.addTableListener(watcher, true);
		return watcher;
	}

	public static void main(String[] args) {

		// Initialize the NetworkTables
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress(NT_SERVER_HOST);
		NetworkTable table = NetworkTable.getTable("lights");
		if (VERBOSE) System.out.println("# network_table.server=" + NT_SERVER_HOST);

		// Initialize Fadecandy server
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		if (VERBOSE) System.out.println("# fadecandy.server=" + FC_SERVER_HOST);
		if (VERBOSE) System.out.println("# fadecandy.port=" + FC_SERVER_PORT);

		// Initialize pixel strips

		TableWatcher stripChassisLeft = initializePixelStripAnimations(fadeCandy, table, 0, 64, STRIP_CHASSIS_LEFT);
		TableWatcher stripChassisRight = initializePixelStripAnimations(fadeCandy, table, 1, 64, STRIP_CHASSIS_RIGHT);
		TableWatcher stripCelebrate = initializePixelStripAnimations(fadeCandy, table, 2, 56, STRIP_CELEBRATE);

//		stripChassisLeft.setAnimation(ANIM_FIRE);
//		stripChassisRight.setAnimation(ANIM_FIRE);
//		stripCelebrate.setAnimation(ANIM_FIRE);

		// Wait forever while Client Connection Reader thread runs
		System.out.println(server.getConfig());

		while (true) {
			try {
				server.animate();
			} catch (Error e) {
				System.out.println("Lights cannot animate");
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e ) {
 				if (VERBOSE) { System.err.println(e.getMessage()); }
			}
		}
	}
}