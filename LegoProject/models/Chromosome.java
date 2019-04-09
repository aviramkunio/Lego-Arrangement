package models;

import java.util.ArrayList;

import application.CubeInGUI;

public class Chromosome implements Comparable<Chromosome> {
	private static int id_cnt = 0;
	public static int chrom_size = 0;
	
	public int id;
	private ArrayList<CubeGA> cubes;
	public ArrayList<CubeInGUI> cubes_in_GUI = null;
	public int fitness, num_of_parts;
	public int cross_point, mut_point;
	public Chromosome parent1, parent2, before_mut;
	
	private Chromosome(boolean isParent) {
		parent1=null;parent2=null;before_mut=null;
		cross_point=-1;
		mut_point=-1;
		if(!isParent) {
			id=id_cnt;
			id_cnt++;
		}
	}
	
	public Chromosome(Chromosome chrom, boolean isSameID, Board b) {
		this(isSameID);
		this.cubes = new ArrayList<>();
		for (CubeGA cubeGA : chrom.cubes) {
			CubeGA new_cube = new CubeGA(cubeGA);
			this.cubes.add(new_cube);
		}
		if(chrom.cubes_in_GUI == null)
			this.cubes_in_GUI=null;
		else {
			if(chrom.cubes_in_GUI.isEmpty()==false)
				this.cubes_in_GUI = new ArrayList<>();
			for (CubeInGUI cubeIG : chrom.cubes_in_GUI) {
				CubeInGUI new_cube = new CubeInGUI(cubeIG);
				this.cubes_in_GUI.add(new_cube);
			}
		}
		this.fitness=chrom.fitness;
		this.num_of_parts=chrom.num_of_parts;
		if(isSameID)
			this.id=chrom.id;
		calcFitness(b);
		calcNumOfParts();
	}
	
	public Chromosome(ArrayList<Cube> cubes, Board b) {
		this(false);
		this.cubes = new ArrayList<>();
		for (Cube cube : cubes)
			this.cubes.add(new CubeGA(cube, 0));
		calcFitness(b);
		calcNumOfParts();
	}
	
	/**
	 * Calculating chrom's {@linkplain Chromosome#fitness} and {@linkplain Chromosome#num_of_parts},
	 * and than checks if all cubes of chrom can be fitted into the board.
	 * If can't be fitted, the chrom's fitness will be equal to 0.
	 * @param chrom the {@linkplain Chromosome} to do all actions on. 
	 */
	public void calcChromFitAndParts(Board b) {
		this.calcNumOfParts();
		this.calcFitness(b);
		if(this.fitness>0)
			if (this.canBeOrderedInBoard(b.rows,b.cols)==false)
				this.fitness = 0;
	}
	
	public void setCubeAmount(int index, int amount, Board b) {
		this.cubes.get(index).amount=amount;
		calcChromFitAndParts(b);
	}
	
	public void setCubeAmountByParent(int index_child, Chromosome parent, int index_parent, Board b) {
		setCubeAmount(index_child, parent.cubes.get(index_parent).amount, b);
	}
	
	public int getSizeOfCubes() {
		return this.cubes.size();
	}
	
	public boolean compareCubeAmount(int index, int amount) {
		return this.cubes.get(index).amount==amount;
	}
	
	public void printCubesInChromosome() {
		for (int i = 0; i < Chromosome.chrom_size; i++)
			if (this.cubes.get(i).amount>0)
				System.out.print(this.cubes.get(i)+" ");
	}
	
	public void calcFitness(Board b) {
		this.fitness=0;
		for (CubeGA cubeGA : this.cubes)
			this.fitness+=(cubeGA.amount*cubeGA.height*cubeGA.width);
		if(this.fitness>(b.cols*b.rows))
			this.fitness=0;	
	}
	
	public void calcNumOfParts() {
		this.num_of_parts = 0;
		for (CubeGA cubeGA : this.cubes)
			this.num_of_parts+=cubeGA.amount;
	}
	
	public boolean canBeOrderedInBoard(int rows, int cols) {
		/*if(this.fitness==0 || this.fitness>rows*cols)
			return false;*/
		int[][] mat = new int[rows][cols];
		
		cubes_in_GUI = new ArrayList<>();
		boolean cube_placed = false;
		int cnt = 0;
		for (CubeGA cubeGA : this.cubes) {
			for (int i = 0; i < cubeGA.amount; i++) {
				cube_placed = false;
				
				for (int row = 0; row < mat.length && !cube_placed; row++) {
					for (int col = 0; col < mat[row].length && !cube_placed; col++) {
						if(mat[row][col]==0)
							cube_placed=checkAndUpdateSubMat(mat,row,col,cubeGA.height,cubeGA.width, cnt);
						if(cube_placed==true) {
							cnt++;
							cubes_in_GUI.add(new CubeInGUI(new Cube(cubeGA), col, row));
						}
					}
				}
				if(cube_placed==false) {
					cubes_in_GUI = null;
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkAndUpdateSubMat(int[][] mat, int init_row, int init_col, int rows, int cols, int cnt) {
		if(init_row+rows>mat.length)		return false;
		if(init_col+cols>mat[0].length)	return false;
		for (int i = init_row; i < init_row+rows; i++)
			for (int j = init_col; j < init_col+cols; j++)
				if(mat[i][j]==1)
					return false;
		cnt++;
		for (int i = init_row; i < init_row+rows; i++)
			for (int j = init_col; j < init_col+cols; j++)
				mat[i][j]=cnt;
		return true;
	}
	
	/**
	 * Checks if <b>this</b> has bigger fitness than <b><i>other_chrom</i></b>'s.<br>
	 * If their fitness is equal, so checks if <b>this</b> has less number of parts than <b><i>other_chrom</i></b>'s
	 * @param other_chrom the {@linkplain Chromosome} which <b>this</b> will be compared to
	 * @return true if <b>this</b> is better than <b><i>other_chrom</i></b>, else false
	 */
	public boolean isBetterChrom(Chromosome other_chrom){
		if (this.fitness > other_chrom.fitness)
			return true;
		else if (this.fitness == other_chrom.fitness && this.num_of_parts<other_chrom.num_of_parts)
			return true;
		return false;
	}
	
	/**
	 * Checks if
	 * <b>this</b> has the same fitness as <b><i>other_chrom</i></b>'s and
	 * <b>this</b> has the same number of parts as <b><i>other_chrom</i></b>'s
	 * @param other_chrom the {@linkplain Chromosome} which <b>this</b> will be compared to
	 * @return true if <b>this</b> is "the same" as <b><i>other_chrom</i></b>, else false
	 */
	public boolean isEqualFitAndParts(Chromosome other_chrom){
		if (this.fitness == other_chrom.fitness && this.num_of_parts==other_chrom.num_of_parts)
			return true;
		return false;
	}
	
	public String getCubesSizesAsString() {
		String str = "";
		for (int i = 0; i < cubes.size(); i++) {
			CubeGA cubeGA = cubes.get(i);
			str+="("+cubeGA.height+"x"+cubeGA.width+") ";
		}
		return str;
	}
	
	public String getCubesAmountAsString() {
		String str = "";
		for (CubeGA cubeGA : cubes)
			str+=cubeGA.amount+" ";
		return str;
	}
	
	@Override
	public String toString() {
		String str = "[id="+id+" ";
		for (CubeGA cubeGA : cubes)
			str+=cubeGA.amount+" ";
		str+="\tFitness=" + fitness + "\tParts=" + num_of_parts+"]";
		return str;
	}

	/**
	 * @return 1 if this better than arg0, 
	 * 0 if equal in amounts of all cubes, -1 if arg0 is better or equal fitness and parts than this, and -2 if error
	 */
	@Override
	public int compareTo(Chromosome chrom) {
		if(this.cubes.size()!=chrom.cubes.size()){
			System.err.println("Different sizes");
			return -2;
		}
		
		for (int i = 0; i < this.cubes.size(); i++) {
			CubeGA this_cube = this.cubes.get(i), chrom_cube = chrom.cubes.get(i);
			if(this_cube.height!=chrom_cube.height || this_cube.width!=chrom_cube.width) {
				System.err.println("Different height and/or width");
				return -2;
			}
			if(this_cube.amount!=chrom_cube.amount) {
				if(this.isBetterChrom(chrom))
					return -1;
				else
					return 1;
			}
		}
		return 0;
	}
}
