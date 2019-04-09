package models;

import java.util.TreeSet;

public class Population {
	private TreeSet<Chromosome> chroms = null;
	public static int pop_size;
	public int sum_of_fitnesses;
	
	public Population() {
		if(this.chroms==null)
			this.chroms=new TreeSet<>();
		else
			clearPop();
	}
	
	public void clearPop() {
		this.chroms.clear();
		this.sum_of_fitnesses=0;
	}
	
	public boolean addToChroms(Chromosome c, Board b) {
		if(chromsContains(c))	
			return false;
		c.calcChromFitAndParts(b);
		this.chroms.add(c);
		return true;
	}
	
	public boolean chromsContains(Chromosome c) {
		if(c==null) {
			System.err.println("chromsContains can't work when Chromosome is null");
			return false;
		}
		return this.chroms.contains(c);
	}
	
	public TreeSet<Chromosome> getChroms(){
		return this.chroms;
	}
	
	public void calcSumOfFitnesses() {
		this.sum_of_fitnesses=0;
		for (Chromosome g : this.chroms)
			this.sum_of_fitnesses+=g.fitness;
	}
	
	public double getAVGFitness() {
		return (double)this.sum_of_fitnesses/(double)pop_size;
	}
	
	public Chromosome getBestChrom() {
		Chromosome best_chrom = chroms.first();
		for (Chromosome chrom : chroms)
			if(chrom.isBetterChrom(best_chrom))
				best_chrom=chrom;
		return best_chrom;
	}
}
