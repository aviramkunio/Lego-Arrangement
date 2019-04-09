package application;

import models.Cube;

public class CubeInGUI extends Cube {
	public int x,y;
	
	public CubeInGUI(Cube c, int x, int y) {
		super(c);
		this.x = x;
		this.y = y;
	}
	
	public CubeInGUI(CubeInGUI cig) {
		this.height=cig.height;
		this.width=cig.width;
		this.x=cig.x;
		this.y=cig.y;
	}

	@Override
	public String toString() {
		return "[(" + height + " x " + width+"), x=" + x + ", y=" + y+"]";
	}
}
