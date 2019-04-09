package models;

public class Cube implements Comparable<Cube>{
	public int height; //row
	public int width; //col

	public Cube() {}
	
	public Cube(int h, int w) {
		super();
		this.height=h;
		this.width=w;
	}
	
	public Cube(Cube cube) {
		this.height=cube.height;
		this.width=cube.width;
	}

	@Override
	public String toString() {
		return "(" + height + " x " + width+")";
	}
	
	@Override
	public int compareTo(Cube cube) {
		if((this.height*this.width)>(cube.height*cube.width))	return -1;
		if((this.height*this.width)<(cube.height*cube.width))	return 1;
		else	return 0;
	}
}
