package models;

public class Xover_Mut {
	public double prob_xover, prob_mutation;
	public int xover_done_cnt, mutation_done_cnt;
	/*public ArrayList<Chromosome> xovered;
	public TreeMap<Chromosome, Chromosome> parent_mutation;*/
	
	public Xover_Mut(double xover_prob, double mutation_prob) {
		super();
		this.prob_xover = xover_prob;
		this.prob_mutation = mutation_prob;
		resetAll();
	}
	
	public void resetAll() {
		this.xover_done_cnt=0;
		this.mutation_done_cnt=0;
		/*this.xovered = new ArrayList<>();
		this.parent_mutation = new TreeMap<>();*/
	}
	
	public String getAllOffSpringsToString(Population gen, Board b) {
		int cnt_xover = 0, cnt_mut = 0;
		String str_xover = "", str_mut = "", final_str = "";
		for (Chromosome chrom : gen.getChroms()) {
			if(chrom.before_mut!=null) {
				cnt_mut++;
				
				chrom.calcFitness(b);	chrom.calcNumOfParts();
				str_mut+="    Mutation point="+chrom.mut_point;
				str_mut+="    [After: ";
				str_mut+="id="+chrom.id+" ";
				str_mut+= chrom.getCubesAmountAsString();
				str_mut+= "(F="+chrom.fitness+")(P="+chrom.num_of_parts+")]";
				
				chrom=chrom.before_mut;
				chrom.calcFitness(b);	chrom.calcNumOfParts();
				str_mut+="    [Before: ";
				str_mut+="id="+chrom.id+" ";
				str_mut+= chrom.getCubesAmountAsString();
				str_mut+= "(F="+chrom.fitness+")(P="+chrom.num_of_parts+")]";
				str_mut+="\r\n";
			}
			if(chrom.parent1!=null && chrom.parent2!=null) {
				cnt_xover++;
				
				chrom.calcFitness(b);	chrom.calcNumOfParts();
				str_xover+="    Xover point="+chrom.cross_point;
				str_xover+= "    [id="+chrom.id+" ";
				str_xover+= chrom.getCubesAmountAsString();
				str_xover+= "(F="+chrom.fitness+")(P="+chrom.num_of_parts+")]";
				
				Chromosome[] parents = new Chromosome[]{chrom.parent1,chrom.parent2};
				for (int i = 0; i < parents.length; i++) {
					parents[i].calcFitness(b);	parents[i].calcNumOfParts();
					str_xover+= "    [Parent" + (i+1)+": ";
					str_xover+= "id="+parents[i].id+" ";
					str_xover+= parents[i].getCubesAmountAsString();
					str_xover+= "(F="+parents[i].fitness+")(P="+parents[i].num_of_parts+")]";
				}
				str_xover+="\r\n";
			}
		}
		int len_xover = str_xover.length();
		int len_mut = str_mut.length();
		if(str_xover.contains("\r\n"))	{len_xover = str_xover.split("\r\n")[0].length();}
		if(str_mut.contains("\r\n"))	{len_mut = str_mut.split("\r\n")[0].length();}
		int len = Math.max(len_xover, len_mut);
		len +=4*4;
		for (int j = 0; j < len; j++) {final_str+="~";}
		
		final_str+="\r\nCrossover occurred " + xover_done_cnt + " times,";
		final_str+=" and " + cnt_xover + " inserted to next generation";
		if(cnt_xover>0) {
			final_str+=":\r\n";
			final_str+=str_xover+"\r\n";
		}
		
		final_str+="\r\nMutation occurred " + mutation_done_cnt + " times,";
		final_str+=" and "+cnt_mut+" inserted to next generation";
		if(cnt_mut>0) {
			final_str+=":\r\n";
			final_str+=str_mut+"\r\n";
		}
		return final_str;
	}
	
	/*public void addXoveredChrom(Chromosome child) {
		xovered.add(child);
	}
	
	public void addMutatedChrom(Chromosome after, Chromosome before) {
		parent_mutation.put(after, before);
	}
	
	public String getAllOffSpringsAsStr(Population gen) {
		String str = "";
		ArrayList<Chromosome> al = new ArrayList<>(this.xovered);
		//for (Chromosome chrom : gen.getChroms()) {
			//if(al.contains(chrom)) {
		for (Chromosome chrom : al) {
				str+= "    [Offspring: id="+chrom.id;
				str+= "   genes=" + chrom.getCubesAmountAsString();
				str+= "(F="+chrom.fitness+")(P="+chrom.num_of_parts+")]";
				
				Chromosome[] parents = new Chromosome[]{chrom.parent1,chrom.parent2};
				for (int i = 0; i < parents.length; i++) {
					str+= "    [Parent" + (i+1) + ": id="+parents[i].id;
					str+= "   genes=" +parents[i].getCubesAmountAsString();
					str+= "(F="+parents[i].fitness+")(P="+parents[i].num_of_parts+")]";
				}
				str+="\r\n";
				al.remove(chrom);
			}
		//}
		return str;
	}*/
}
