package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AlgorithmLego {
	private Board board;
	private ArrayList<Cube> cubes_types;
	private Population cur_gen, next_gen;
	private ArrayList<Population> all_gens;
	private Chromosome best_chrom_in_all_gens;
	private Xover_Mut xover_mut;
	
	private String str = "", temp_str = "";
	//private int max_cube_size;
	private final int max_gens = 50;
	
	public Chromosome getBestChromInAllGens() {
		return this.best_chrom_in_all_gens;
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	public AlgorithmLego(Board b, ArrayList<Cube> cubes_types, int pop_size) throws Exception {
		if(b==null || b.cols==0 || b.rows==0 || cubes_types==null || cubes_types.isEmpty() || pop_size==0)
			errMsg("Input wrong!!");
		this.board=b;
		this.cubes_types=cubes_types;
		Population.pop_size=pop_size;
		this.analayzeInput();
		this.makeGenrZero();
		this.getOptimalOrder();
	}
	
	public void makeGenrZero() throws Exception {
		all_gens = new ArrayList<>();
		this.generatePopulationZero();

		System.out.println("****************************************************************************************");
		System.out.println("Generation Zero:");
		System.out.println("----------------");
		System.out.println("Population:");
		int i = 1;
		for (Chromosome chrom : this.cur_gen.getChroms()) {
			chrom.calcFitness(board);	chrom.calcNumOfParts();
			System.out.println(i + " - " + chrom.toString());
			i++;
		}
		
		this.cur_gen.calcSumOfFitnesses();
		System.out.println("Average fitness of initial generation: " + this.cur_gen.getAVGFitness());
		System.out.println("Total fitness score of initial generation: " + this.cur_gen.sum_of_fitnesses);
		
		all_gens.add(this.cur_gen);
		
		if(max_gens>1)		
			makeFurtherGenerations();
	}
		
	/** Generates new random {@linkplain Population} 
	 * @throws Exception */
	private void generatePopulationZero() throws Exception {
		for (int i = 0; i < Population.pop_size; i++) {
			Chromosome chrom;
			int cnt = 2000000;
			do{
				chrom = makeChromosome();
				chrom.calcChromFitAndParts(board);
				cnt--;
			}while ((chrom.fitness==0||this.cur_gen.addToChroms(chrom, board)==false)&&cnt>0);
			if(cnt==0 && this.cur_gen.getChroms().size()!=Population.pop_size)
				throw new Exception("Can't create "+Population.pop_size+" chromosomes in selected board size and cubes types");
		}
		this.cur_gen.calcSumOfFitnesses();
	}
	
	/** @return New random {@linkplain Chromosome} - for each {@linkplain CubeGA} generates random amount */
	private Chromosome makeChromosome() {
		Chromosome c = new Chromosome(cubes_types, board);
		for (int i = 0; i < c.getSizeOfCubes(); i++) {
			/*	The best order is to take the biggest cube,
				and to put it (rows*cols)/cube_size times on the board*/
			int max_amount = (int)((double)(this.board.rows*this.board.cols)/*/(double)max_cube_size*/);
			int rand_amount = (new Random()).nextInt(max_amount+1);
			c.setCubeAmount(i, rand_amount,board);
		}
		return c;
	}
	
	/** Initializing all the data, and generating random {@linkplain #prob_xover}, and {@linkplain #prob_mutation} */
	private void analayzeInput() {
		this.cur_gen = new Population();
		Collections.sort(this.cubes_types);
		Chromosome.chrom_size = this.cubes_types.size();

		//max_cube_size=this.cubes_types.get(0).height*this.cubes_types.get(0).width;
		
		Random r = new Random();
		double xover_prob =((double)r.nextInt(90-60+1)+60)/100;
		
		double pop_prob = 1/(double)Population.pop_size, chrom_prob = 1/(double)Chromosome.chrom_size;
		int a = (int) (100*Math.max(pop_prob, chrom_prob)),b=(int) (100*Math.min(pop_prob, chrom_prob));
		
		double mutation_prob = ((double)r.nextInt(a-b+1)+b)/100;
		
		this.xover_mut = new Xover_Mut(xover_prob, mutation_prob);
	}
	
	private void makeFurtherGenerations() throws Exception {
		next_gen=new Population();
		for (int i = 1; i < max_gens; i++) {
			
			if(i > 4) {
				this.all_gens.get(i - 1).calcSumOfFitnesses();
				this.all_gens.get(i - 2).calcSumOfFitnesses();
				this.all_gens.get(i - 3).calcSumOfFitnesses();
				
				int s1 = this.all_gens.get(i - 1).sum_of_fitnesses,
						s2 = this.all_gens.get(i - 2).sum_of_fitnesses,
								s3 = this.all_gens.get(i - 3).sum_of_fitnesses;

				// If the last 3 sum_of_fitnesses equal, stop
				if (s1 == s2 && s2 == s3) {
					System.out.println("\r\nStopped - last 3 generations have the same sum_of_fitnesses");
					i=max_gens;
					break;
				}
			}
		
			// Reset counters and flags
			this.xover_mut.resetAll();

			
			// Print population to console
			str="";
			str+="\r\n===============================================================================================\r\n\r\n\r\n";
			String str_gen = "Generation " + i + ":";
			str+=str_gen+"\r\n";
			for (int j = 0; j < str_gen.length(); j++)	str+="-";
			
			str+="\r\nPopulation:\r\n";
			
			// Generate next generation
			int cnt = 200;
			do {
				this.next_gen.clearPop();
				temp_str="";
				for (int j = 0; j < Population.pop_size / 2; j++)
					this.generateNextPopulation();
				this.next_gen.calcSumOfFitnesses();
				cnt--;
			}while(this.next_gen.sum_of_fitnesses<this.all_gens.get(i - 1).sum_of_fitnesses && cnt>0);
			if(cnt==0)
				throw new Exception("Error occured");
			str+=temp_str;

			this.cur_gen = new Population();
			for (Chromosome chrom : this.next_gen.getChroms())
				this.cur_gen.addToChroms(chrom, board);
			// Clear next_gen
			this.next_gen = new Population();
			this.cur_gen.calcSumOfFitnesses();
			all_gens.add(this.cur_gen);
			
			str+="Average fitness of generation " + i + ": " + this.cur_gen.getAVGFitness()+"\r\n";
			str+="Total fitness score of generation " + i + ": "+ this.cur_gen.sum_of_fitnesses+"\r\n";

			// Output crossover summary
			str+=this.xover_mut.getAllOffSpringsToString(this.cur_gen, board);
			
			System.out.println(str);
			
			for (Chromosome chrom : this.cur_gen.getChroms()) {
				chrom.parent1=null;
				chrom.parent2=null;
				chrom.before_mut=null;
			}
		}
	}
	
	private void generateNextPopulation() {
		Chromosome parent1, parent2;		

		//If crossover occurring, do it until the two chromosomes are valid, and different
		do {
			// Get pair of chromosomes for generating next population
			parent1 = selectChrom();
			parent2 = selectChrom();
		}while (parent1.compareTo(parent2)==0 || crossoverChroms(new Chromosome(parent1, true, board), new Chromosome(parent2, true, board))==false);
	}
	
	/**
	 * Performing crossover on the 2 inputs, in probability of {@linkplain Xover_Mut#prob_xover} which is between 0.6 to 0.9
	 * @param parent1 The first parent to crossover
	 * @param parent2 The second parent to crossover
	 * @return The best 2 genes between all 4 options (parent1, parent2, child1, child2).
	 * Best gene is decided by {@link #getBestChrom(ArrayList)}
	 */
	private boolean crossoverChroms(Chromosome parent1, Chromosome parent2) {
		Chromosome child1, child2; 
		
		double rand_crossover = Math.random();
		// if true, perform crossover
		if (parent1.compareTo(parent2)!=0 && rand_crossover <= this.xover_mut.prob_xover) {
			this.xover_mut.xover_done_cnt++;
			
			//copy parent's chromosomes (cubes amounts) to children
			child1 = new Chromosome(parent1, false, board);
			child2 = new Chromosome(parent2, false, board);
			
			//generate random crossover point between 1 to Chromosome.chrom_size-1
			Random generator = new Random();
			int cross_point = generator.nextInt(Chromosome.chrom_size-1) + 1;
			
			/*i=[cross_point->Chromosome.chrom_size-1]. 
			 replacing all the amounts of cubes of the child in i range with 
			  the amounts of the parent which child hasn't got any amount from*/
			for (int i = cross_point; i < Chromosome.chrom_size; i++) {
				child1.setCubeAmountByParent(i,parent2,i,board);
				child2.setCubeAmountByParent(i,parent1,i,board);
			}
			child1.cross_point=child2.cross_point=cross_point;
			
			child1.parent1=new Chromosome(parent1, true, board);	child1.parent2=new Chromosome(parent2, true, board);
			child2.parent1=new Chromosome(parent1, true, board);	child2.parent2=new Chromosome(parent2, true, board);
			
			ArrayList<Chromosome> final_chroms = new ArrayList<>();
			ArrayList<Chromosome> childs = new ArrayList<>();
			childs.add(child1);		childs.add(child2);	
			
			/*Mutating according to mut_prob, and if child been mutated,
			  we will consider the mutated chromosome as our child
			 */
			for (int i = 0; i < childs.size(); i++) {
				Chromosome before = childs.get(i);
				Chromosome after = mutateChrom(before);
				if(after==null) {
					before.calcChromFitAndParts(board);
					if(before.fitness>0)//if child is valid
						final_chroms.add(before);
				}
				else {
					after.calcChromFitAndParts(board);
					if(after.fitness==0)//if after mutation child isn't valid
						continue;
					after.before_mut=new Chromosome(before, true, board);
					final_chroms.add(after);
				}
			}
			if(final_chroms.isEmpty()==false) {
				final_chroms.add(parent1);	final_chroms.add(parent2);
				Collections.sort(final_chroms);
				Chromosome[] bests = new Chromosome[2];
				bests = find2BestChromsNotInPop(new ArrayList<>(final_chroms));
				
				//add the 2 best chromosomes into the next generation
				if(bests!=null && bests[0]!=null && bests[1]!=null && 
						!this.next_gen.chromsContains(bests[0]) && !this.next_gen.chromsContains(bests[1]) &&
						bests[0].compareTo(bests[1])!=0) {
					this.next_gen.addToChroms(bests[0], board);
					this.next_gen.addToChroms(bests[1], board);
					temp_str+="\t"+bests[0]+"\r\n";
					temp_str+="\t"+bests[1]+"\r\n";					
					
					return true;
				}
			}
			
		}
		// Otherwise, keep the parents
		if(this.next_gen.chromsContains(parent1) || this.next_gen.chromsContains(parent2))
			return false;
		this.next_gen.addToChroms(parent1, board);
		this.next_gen.addToChroms(parent2, board);
		temp_str+="\t"+parent1+"\r\n";
		temp_str+="\t"+parent2+"\r\n";
		return true;
	}
	
	private Chromosome[] find2BestChromsNotInPop(ArrayList<Chromosome> arrlist) {
		Collections.sort(arrlist);
		Chromosome[] bests = new Chromosome[2];
		bests[0]=bests[1]=null;
		for (int i = 0; i < bests.length; i++) {
			if(arrlist.isEmpty())
				return null;
			do{
				bests[i] = arrlist.get(0);
				if(arrlist.remove(bests[i])==false)
					errMsg("No chrom " + bests[i]);
			}while(bests[i]!=null && this.next_gen.chromsContains(bests[i]) && arrlist.isEmpty()==false);
			if(bests[i]==null || this.next_gen.chromsContains(bests[i]))
				return null;
		}
		
		return bests;
	}
	
	/**
	 * Performing mutation on <b><i>chrom_before</i></b>, 
	 * in probability of {@linkplain Xover_Mut#prob_mutation} 
	 * which is between (1/{@linkplain Population#pop_size}) to (1/{@linkplain Chromosome#chrom_size})
	 * @param chrom_before the chromosome which being mutated
	 * @return the mutated chromosome if randomly decided to mutate it, else null
	 */
	private Chromosome mutateChrom(Chromosome chrom_before) {
		// Decide if mutation is to be used
		double rand_mutation = Math.random();
		if (rand_mutation <= this.xover_mut.prob_mutation) {
			this.xover_mut.mutation_done_cnt++;
			
			// If so, perform mutation
			Random generator = new Random();
			int mut_point = 0;

			// Mutate chrom
			Chromosome chrom_after= new Chromosome(chrom_before, false, board);
			mut_point = generator.nextInt(Chromosome.chrom_size);
			int max_amount = (int)((double)(this.board.rows*this.board.cols)/*/(double)max_cube_size*/);
			int new_amount;
			do {
				new_amount = generator.nextInt(max_amount+1);
			}while(chrom_after.compareCubeAmount(mut_point, new_amount));
			
			//The Mutation
			chrom_after.setCubeAmount(mut_point, new_amount,board);
			chrom_after.mut_point=mut_point;
			
			chrom_after.calcChromFitAndParts(board);
			return chrom_after;
		}
		return null;
	}

	/**
	 * 
	 * @return chromosome randomly between all chromosomes in {@link #cur_gen}
	 */
	private Chromosome selectChrom() {
		// Generate random number between 0 and total_fitness_of_generation
		double rand = Math.random() * this.cur_gen.sum_of_fitnesses;
		ArrayList<Chromosome> al = new ArrayList<>(this.cur_gen.getChroms());

		// Use random number to select chrom based on fitness level
		for (int i = 0; i < Population.pop_size; i++) {
			if (rand <= al.get(i).fitness)
				return al.get(i);
			rand = rand - al.get(i).fitness;
		}

		// Not reachable; default return value
		return null;
	}
	
	/**
	 * Prints and returns the best {@linkplain models.Chromosome} between all generations bests, 
	 * and sets {@link #best_chrom_in_all_gens} = best_chrom
	 */
	private void getOptimalOrder() {

		System.out.println("\r\nOptimal list of lego cubes to include in our lego board: ");
		
		this.best_chrom_in_all_gens = this.cur_gen.getBestChrom();
		
		this.best_chrom_in_all_gens.printCubesInChromosome();
	}
	
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
		
	public void errMsg(String s) {
		System.err.println(s);
		System.exit(1);
	}
}
