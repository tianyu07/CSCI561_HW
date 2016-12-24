import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KnowledgeBase {
	private Set<Term> factsSet;
	private Set<String> CNFClause;
	private Map<String, ArrayList<Integer>> predIndexMap;
	private Map<Integer, ArrayList<Term>> KB;
	
	public KnowledgeBase(Set<Term> factsSet, Set<String> CNFClause) {
		this.factsSet = factsSet;
		this.CNFClause = CNFClause;
		this.predIndexMap = new HashMap<String, ArrayList<Integer>>();
		this.KB = new HashMap<Integer, ArrayList<Term>>();
		setKB();
	}
	
	private void setKB() {
		int count = 0;
		for (String curr: CNFClause) {
			ArrayList<Term> value = separateTerm(ToolClass.standardize(curr));
			setIndex(value, count);			
			KB.put(count, value);
			count++;
		}
		for (Term term: factsSet) {
			ArrayList<Term> temp = new ArrayList<Term>();
			temp.add(term);
			setIndex(temp, count);
			KB.put(count, temp);
			count++;
		}
	}
	
	//Separate clause converted by CNFConverter and store to an arraylist
	private ArrayList<Term> separateTerm(String clause) {
		ArrayList<Term> value = new ArrayList<Term>();
		if (!clause.contains(" ")) {
			value.add(ToolClass.storeAsTerm(clause));
			return value;
		}
		String[] predicates = clause.split(" ");
		for (int i = 0; i < predicates.length; i++) {
			value.add(ToolClass.storeAsTerm(predicates[i]));
		}
		return value;
	}

	//List all terms existed and record their existences in KB with an arraylist
	private void setIndex(ArrayList<Term> value, int count) {	
		for (int i = 0; i < value.size(); i++) {
			if (value.get(i) != null) {
				ArrayList<Integer> indexArr = new ArrayList<Integer>();
				String key = value.get(i).isPositive? value.get(i).predicate : "~" + value.get(i).predicate;			
				if (predIndexMap.containsKey(key)) 
					indexArr = predIndexMap.get(key);
				indexArr.add(count);
				predIndexMap.put(key, indexArr);
			}
		}
	}


	public Map<Integer, ArrayList<Term>> getKB() {	
		return KB;
	}

	public Map<String, ArrayList<Integer>> getPredIndex() {
		return predIndexMap;
	}

	
	
}