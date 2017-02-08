package org.firebears.lights;

import java.util.ArrayList;
import java.util.List;

import opc.Animation;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;
import examples.Fire;
import examples.Pulsing;
import examples.Spark;
import examples.Crazy;

/**
 * This program runs the lights continuously.
 */
public class LightsConstant {

	/** Milliseconds between animation changes. */
	public static final long TIME_PER_CYCLE = 10 * 1000L;


	/** Host name or IP address of the Fadecandy server. */
	public static final String FC_SERVER_HOST
		= System.getProperty("fadecandy.server", "raspberrypi.local");

	/** Port number of the Fadecandy server. */
	public static final int FC_SERVER_PORT
		= Integer.parseInt(System.getProperty("fadecandy.port", "7890"));

	/** Whether to display extra information about internal processes. */
	public static final boolean VERBOSE
		= "true".equals(System.getProperty("verbose", "false"));


	public static void main(String[] args) {

		// Initialize Fadecandy server
		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		if (VERBOSE) System.out.println("# fadecandy.server=" + FC_SERVER_HOST);
		if (VERBOSE) System.out.println("# fadecandy.port=" + FC_SERVER_PORT);

		// Initialize pixel strips
		long changeTime = System.currentTimeMillis() + TIME_PER_CYCLE;
		List<AnimationSequence> stripList = new ArrayList<AnimationSequence>();

		{
			AnimationSequence s = new AnimationSequence(fadeCandy.addPixelStrip(0, 8));
			stripList.add(s);
			s.addAnimation(new Fire());
			s.addAnimation(new Pulsing());
			s.addAnimation(new Pulsing(LightsMain.CS_YELLOW));
			s.addAnimation(new Pulsing(6));
		}

		{
			AnimationSequence s = new AnimationSequence(fadeCandy.addPixelStrip(1, 8));
			stripList.add(s);
			s.addAnimation(new Fire());
			s.addAnimation(new Crazy());
			s.addAnimation(new Pulsing(LightsMain.CS_RED));
			s.addAnimation(new Pulsing(6));
		}

		{
			AnimationSequence s = new AnimationSequence(fadeCandy.addPixelStrip(1, 8));
			stripList.add(s);
			s.addAnimation(new Fire());
			s.addAnimation(new Spark());
			s.addAnimation(new Pulsing(LightsMain.CS_RED_YELLOW));
			s.addAnimation(new Pulsing(8));
		}

		System.out.println(server.getConfig());

		// Loop forever
		while (true) {
			server.animate();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e ) {
 				if (VERBOSE) { System.err.println(e.getMessage()); }
			}
			if (System.currentTimeMillis() > changeTime)  {
				for (int i = 0; i<stripList.size(); i++)  {
					AnimationSequence a = (AnimationSequence)stripList.get(i);
					a.nextAnimation();
				}
				changeTime = System.currentTimeMillis() + TIME_PER_CYCLE;
			}
		}
	}


	/** Sequence of animations attached to one PixelStrip. */
	public static class AnimationSequence {
		final public PixelStrip strip;
		final public List<Animation> animationList;
		private int state = 0;
		public AnimationSequence(PixelStrip s) {
			strip = s;
			animationList = new ArrayList<Animation>();
		}
		public void addAnimation(Animation a) {
			animationList.add(a);
			strip.setAnimation(a);
			state = animationList.size() - 1;
		}
		public void nextAnimation() {
			state = (state+1) % animationList.size();
			Animation a = (Animation)animationList.get(state);
			strip.setAnimation(a);
		}
	}

}
