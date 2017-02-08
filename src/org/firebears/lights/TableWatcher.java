package org.firebears.lights;

import opc.Animation;
import opc.PixelStrip;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

import java.util.*;

/**
 * Watches for changes to a given {@link NetworkTable} where the key starts with
 * a specific prefix.
 * <p>
 * Probably you will create one of these watchers for each {@link PixelStrip}.
 * Then you will add named animations to each watcher.  When the animation named
 * in the {@code NetworkTable} is changed, it triggers an animation change.
 * Setting the animation name to a blank causes animation to stop.
 * <p>
 * You can also change numbers in table entries to push number values into
 * the animations.  This lets you modify a running animation to correspond
 * to something hapening on the robot.
 */
public class TableWatcher implements ITableListener {

	public static final boolean VERBOSE
		= "true".equals(System.getProperty("verbose", "false"));

	private final String prefix;
	private final PixelStrip strip;
	private final Map<String,Animation> animationMap;
	private final boolean clear = true;

	double animate_value;
	String animate_bg;
	double animate_dim;
	double animate_col;


	public TableWatcher(String prefix, PixelStrip strip) {
		this.prefix = prefix;
		this.strip = strip;
		this.animationMap = new HashMap<String,Animation>();
	}

	/**
	 * Handle every change in the {@link NetworkTable}.
	 * Ignore changes whose key doesn't match the prefix.
	 */
	@Override
	public void valueChanged(ITable source, String key, Object value, boolean isNew) {
		if (! key.startsWith(prefix)) { return; }
		String suffix = key.substring(prefix.length());
		if (contains(suffix, NetworkTable.PATH_SEPARATOR)) { return; }
		if(strip.getAnimation() == null) {
			if (suffix.equals(".value"))  {
				animate_value = (double) value;
			} else if (suffix.equals(".bg")) {
				animate_bg = (String) value;
			} else if (suffix.equals(".dim")) {
				animate_dim = (double) value;
			} else if (suffix.equals(".color")) {
				animate_col = (double) value;
			}else{
				setAnimation((String)value);
				updateAnimation();
			}
			return;
		}
		if (suffix.equals(".value"))  {
			animate_value = (double) value;
			setValue(animate_value);
		} else if (suffix.equals(".bg")) {
			animate_bg = (String) value;
			setBg(animate_bg);
		} else if (suffix.equals(".dim")) {
			animate_dim = (double) value;
			setBg_value(animate_dim);
		} else if (suffix.equals(".color")) {
			animate_col = (double) value;
			setColor(animate_col);
		}else{
			setAnimation((String)value);
			updateAnimation();
		}
	}

	private void updateAnimation() {
		setValue(animate_value);
		setBg(animate_bg);
		setBg_value(animate_dim);
//		setColor(animate_col);
	}

	/**
	 * Change the number value of the current animation.
	 */
	private void setValue(Double value) {
		if (strip.getAnimation() == null) {
			if (VERBOSE) {
				System.err.println("Error: no animation running on " + strip);
			}
			return;
		}
		if (VERBOSE) {
			System.out.println("changeAnimationValue on " + strip + " and "
					+ strip.getAnimation() + " to " + value);
		}
		if (strip.getAnimation()!=null) { strip.getAnimation().setValue((Double) value); }
	}

	private void setColor(double value) {
		if (VERBOSE) {
			System.out.println("change color value on " + strip + " and "
					+ strip.getAnimation() + " to " + value);
		}
		if (strip.getAnimation()!=null) {  strip.getAnimation().setColor( (int) value); }
	}

	private void setBg_value(double value) {
		if (VERBOSE) {
			System.out.println("change BG Dim value on " + strip + " and "
					+ strip.getAnimation() + " to " + value);
		}
		if (strip.getAnimation()!=null) { strip.getAnimation().setDimness((int) value); }
	}

	private void setBg(String animationName) {
		if (animationName==null || animationName.trim().length()==0) {
			if (strip.getAnimation()!=null) { strip.getAnimation().setBg(null); }
			if (VERBOSE) {
				System.out.println("changeAnimation on " + strip
						+ " to nothing ");
			}
			return;
		}

		Animation newAnimation = animationMap.get(animationName.toString());
		if (newAnimation == null) {
			System.err.println("Error: unknown animation " + animationName + " for " + strip);
			return;
		}

		if (VERBOSE) {
			System.out.println("changeAnimation on " + strip + " to "
					+ animationName + " : " + newAnimation);
		}
		if (clear) { strip.clear(); }

		if (strip.getAnimation()!=null) { strip.getAnimation().setBg(newAnimation); }
	}

	/**
	 * Change the animation currently running on this strip.
	 * If the animationName is blank, turn off animation on this strip.
	 */
	protected void setAnimation(String animationName) {
		if (animationName==null || animationName.trim().length()==0) {
			strip.setAnimation(null);
			strip.clear();
			if (VERBOSE) {
				System.out.println("changeAnimation on " + strip
						+ " to nothing ");
			}
			return;
		}
		Animation newAnimation = animationMap.get(animationName.toString());
		if (newAnimation == null) {
			System.err.println("Error: unknown animation " + animationName + " for " + strip);
			return;
		}
		if (newAnimation == strip.getAnimation()) {
			return;
		} else {
			if (VERBOSE) {
				System.out.println("changeAnimation on " + strip + " to "
						+ animationName + " : " + newAnimation);
			}
			if (clear) { strip.clear();  }
			strip.setAnimation(newAnimation);

		}
	}

	/**
	 * @return whether the key contains the separator character.
	 */
	private static boolean contains(String key, char separator) {
		for (int i = 0; i < key.length(); ++i)  {
			if (key.charAt(i) == separator) { return true; }
		}
		return false;
	}

	/**
	 * Add a named animation to this watcher.
	 */
	public void addAnimation(String animationName, Animation animation)  {
		this.animationMap.put(animationName, animation);
	}
}