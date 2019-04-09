package models;

public class CubeGA extends Cube  {
	public int amount;
	
	public CubeGA(CubeGA cubeGA) {
		this.height=cubeGA.height;
		this.width=cubeGA.width;
		this.amount=cubeGA.amount;
	}
	
	public CubeGA(Cube cube, int amnt) {
		super(cube);
		this.amount = amnt;
	}

	@Override
	public String toString() {
		return "[(" + height + " x " + width+"), amount=" + amount+"]";
	}	
}
