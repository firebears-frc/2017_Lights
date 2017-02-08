package opc;

import examples.Fire;

public abstract class Foreground extends Animation {

	protected PixelStrip g_strip;
	
	@Override
	public abstract void reset(PixelStrip strip);

	@Override
	public abstract boolean draw(PixelStrip strip);
	
	public void setBg(Animation anim) {
		background = anim;
		if (background !=null) {
			background.reset(g_strip);
			background.g_fade = 255;
		}
	}
	
	public void setDimness(int dim) {
		if (background != null) {
			background.g_fade = dim;
		}
	}

	public void prepare(PixelStrip strip) {
		g_strip = strip;
		setBg(new Fire());
	}
	
	public void draw_bg() {
		if (background!=null) {
			background.draw(g_strip);
		}
	}
	
}
