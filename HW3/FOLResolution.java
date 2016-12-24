import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

public class FOLResolution {
	
	private ArrayList<Term> querySet;
	private Map<Integer, ArrayList<Term>> KB;
	private Map<String, ArrayList<Integer>> predIndexMap;
	
	public FOLResolution(ArrayList<Term> querySet, Map<Integer, ArrayList<Term>> KB, 
			Map<String, ArrayList<Integer>> predIndexMap) {
		this.querySet = querySet;
		this.KB = KB;
		this.predIndexMap = predIndexMap;
	}

	//resolve one query, add to KB and predIndexMap and remove them after resolution
	public ArrayList<String> execute() {
		ArrayList<String> returnList = new ArrayList<String>();
		for (Term curr: querySet) {
			boolean hasAdded = false;
			curr.isPositive = curr.isPositive ? false : true;
			ArrayList<Term> newQuery = new ArrayList<Term>();
			newQuery.add(curr);
			KB.put(KB.size(), newQuery);
			String key = curr.isPositive ? curr.predicate : "~" + curr.predicate;
			if (!predIndexMap.containsKey(key)) {
				ArrayList<Integer> value = new ArrayList<Integer>();
				value.add(KB.size()-1);
				predIndexMap.put(key, value);
				hasAdded = true;
			}
			else {
				predIndexMap.get(key).add(KB.size()-1);
			}
			int KBOriginalSize = KB.size();
			returnList.add(resolution(curr));
			Reset(KBOriginalSize);

			KB.remove(KB.size()-1);
			if (hasAdded) {
				predIndexMap.remove(key);
			}  				
			else {
				predIndexMap.get(key).remove(Integer.valueOf(KB.size()));
			}
		}
		return returnList;
	}

	//Use curr(query) to resolve with every knowledge in KB, store results in a new KB map
	//return TRUE or FALSE by resolution results by resolve()
	private String resolution(Term curr) {
		Set<ArrayList<Term>> oldKnowledge = new HashSet<ArrayList<Term>>();
		//oldKnowledge include all knowledge records in old KB and new added KB
		for (int i = 0; i < KB.size(); i++) {
			oldKnowledge.add(KB.get(i));
		}
		int newKBInitial = 0;
		while (true) {		
			int KBsize = KB.size();
			if (KBsize == newKBInitial) {
				return "FALSE";	//KB doesn't grow anymore, end loop
			}
					
			//newKnowledge include new generated knowledge distinct from all records of oldKnowledge in one loop
			Set<ArrayList<Term>> newKnowledge = new HashSet<ArrayList<Term>>();
			for (int i = 0; i < KBsize; i++) {
				ArrayList<Term> resolvePred = KB.get(i);	//a whole knowledge got from KB such as ~H(0)|F(0)
				
				//Separate each of its predicate and search for indexes of KB which has predicates that can resolve with it.
				//for ~H(0), indexList is H [5, 6, 7, 14]; for F(0), indexList is ~F [1, 3]
				for (int j = 0; j < resolvePred.size(); j++) {
					String clause1 = resolvePred.get(j).isPositive ? resolvePred.get(j).predicate : "~" + resolvePred.get(j).predicate;
					ArrayList<Integer> indexList = predIndexMap.get(negation(clause1));
					if (indexList != null) {
						for (int k = 0; k < indexList.size(); k++) {
							if (indexList.get(k) >= newKBInitial) {
								if (KB.get(i).size() == 1 || KB.get(indexList.get(k)).size() == 1) {
									ArrayList<Term> result = resolve(KB.get(i), KB.get(indexList.get(k)));
									if (result != null && result.size() == 0)	{
										//if resolution result is empty, reset KB and index map and resolve next query
										return "TRUE";
									}
									//if resolved clause doesn't appear ever, add it to newKnowledge, and update oldKnowledge
									if (result != null) {
										boolean hasRepeated = false;
										for(ArrayList<Term> currKnowledge: oldKnowledge){
											if (isSameClause(currKnowledge, result)) {
												hasRepeated = true;
												break;
											}
										}
										if (!hasRepeated) {
											oldKnowledge.add(result);
											newKnowledge.add(result);
										}
									}
								}		
							}							
						}
					}
				}
			}
			newKBInitial = KBsize;	//Record the current size, start from here in the next loop

			//Update KB and predIndexMap after one resolution loop, add new resolution clauses and indexes
			for (ArrayList<Term> addKnowledge: newKnowledge) {
				KB.put(KBsize, addKnowledge);
				for (int i = 0; i < addKnowledge.size(); i++) {
					String clause = addKnowledge.get(i).isPositive ? addKnowledge.get(i).predicate : "~" + addKnowledge.get(i).predicate;
					predIndexMap.get(clause).add(KBsize);
				}
				KBsize++;
			}
		}		
	}

	//Distinguish if two clauses are different clauses, return true if they are same, false if they are different
	//A(x,y) and A(x,y)/A(m,n) are same clauses; A(x,y) and A(Alice,y) are different clauses
	private boolean isSameClause(ArrayList<Term> currKnowledge, ArrayList<Term> result) {		
		if (currKnowledge.size() == result.size()) {
			Factoring(currKnowledge);
			Factoring(result);
			currKnowledge = ClauseSort(currKnowledge);
			result = ClauseSort(result);
			for (int i = 0; i < currKnowledge.size(); i++) {
				Term oldTerm = currKnowledge.get(i);
				Term newTerm = result.get(i);
				if (oldTerm.isPositive == newTerm.isPositive) {
					if (oldTerm.predicate.equals(newTerm.predicate)) {
						ArrayList<String> oldContent = oldTerm.content;
						ArrayList<String> newContent = newTerm.content;
						if (oldContent.size() == newContent.size()) {
							for (int j = 0; j < oldContent.size(); j++) {
								if (!isNumeric(oldContent.get(j)) && !isNumeric(newContent.get(j)) && !oldContent.get(j).equals(newContent.get(j))) {
									return false;
								} 
								else if ((!isNumeric(oldContent.get(j)) && isNumeric(newContent.get(j))) || (isNumeric(oldContent.get(j)) && !isNumeric(newContent.get(j)))) {
									return false;
								}
							}
						}
						else return false;
					}
					else return false;
				}
				else return false;  
			}	
		}
		else return false;

		return true;
	}

	private void Factoring(ArrayList<Term> knowledge) {
		Map<String, ArrayList<Integer>> factoringMap = SetFactoringMap(knowledge);
		for(Map.Entry<String, ArrayList<Integer>> curr: factoringMap.entrySet()){
			if (curr.getValue().size() > 1) {
				for (int i = 1; i < curr.getValue().size(); i++) {
					if (isSameTerm(knowledge.get(curr.getValue().get(i-1)), knowledge.get(curr.getValue().get(i)))) {
						knowledge.remove(curr.getValue().get(i));
						factoringMap = SetFactoringMap(knowledge);
					}		
				}
			}
		}
	}

	private Map<String, ArrayList<Integer>> SetFactoringMap(ArrayList<Term> knowledge) {
		Map<String, ArrayList<Integer>> factoringMap = new HashMap<String, ArrayList<Integer>>();
		for (int i = 0; i < knowledge.size(); i++) {
			String predicateKey = knowledge.get(i).isPositive == true ? knowledge.get(i).predicate : "~" + knowledge.get(i).predicate;
			if (factoringMap.containsKey(predicateKey)) {
				factoringMap.get(predicateKey).add(i);
			}
			else {
				ArrayList<Integer> val = new ArrayList<Integer>();
				val.add(i);
				factoringMap.put(predicateKey, val);
			}
		}
		return factoringMap;
	}

	private boolean isSameTerm(Term term1, Term term2) {
		if (term1.isPositive != term2.isPositive) 
			return false;
		
		if (!term1.predicate.equals(term2.predicate)) 
			return false;
			
		if (term1.content.size() != term2.content.size())
			return false;

		for (int i = 0; i < term1.content.size(); i++) {
			if (!term1.content.get(i).equals(term2.content.get(i))) {
				return false;
			}
		}
		
		return true;
	}

	private ArrayList<Term> ClauseSort(ArrayList<Term> knowledge) {
		Map<String, Integer> clauseSortMap = new HashMap<String, Integer>();
		String[] array = new String[knowledge.size()];
		for (int i = 0; i < knowledge.size(); i++) {
			String predicateKey = knowledge.get(i).isPositive == true ? knowledge.get(i).predicate : "~" + knowledge.get(i).predicate;
			clauseSortMap.put(predicateKey, i);
			array[i] = predicateKey;
		}
		Arrays.sort(array);
		ArrayList<Term> returnArrlist = new ArrayList<Term>();
		for (int i = 0; i < knowledge.size(); i++) {
			Term temp = knowledge.get(clauseSortMap.get(array[i]));
			returnArrlist.add(temp);
		}
		return returnArrlist;
	}
	
	
	//Reset KB and index map to original state
	private void Reset(int KBOriginalSize) {
		for (int i = KB.size() - 1; i >= KBOriginalSize; i--) {
			KB.remove(i);
		}
		for(Map.Entry<String, ArrayList<Integer>> currIndex: predIndexMap.entrySet()){
			int indexArraySize = currIndex.getValue().size();
			for (int i = indexArraySize - 1; i >= 0; i--) {
				if (currIndex.getValue().get(i) >= KBOriginalSize) {
					currIndex.getValue().remove(i);
				}
			}		
		}
	}

	//resolve two clauses, return empty ArrayList if find contradiction, return null if cannot unify
	private ArrayList<Term> resolve(ArrayList<Term> clause1, ArrayList<Term> clause2) {
		ArrayList<Term> result = new ArrayList<Term>();
		for (int i = 0; i < clause1.size(); i++) {
			boolean isPositive1 = clause1.get(i).isPositive;
			String predicate1 = clause1.get(i).predicate;
			for (int j = 0; j < clause2.size(); j++) {
				if (isPositive1 != clause2.get(j).isPositive && predicate1.equals(clause2.get(j).predicate)) {
					result = Unify(clause1, clause2, i, j);
					return result;
				}
			}
		}
		return result;	
	}
	
	private ArrayList<Term> Unify(ArrayList<Term> clause_1, ArrayList<Term> clause_2, int position1, int position2) {
		ArrayList<Term> resolvedClause = new ArrayList<Term>();
		ArrayList<Term> clause1 = new ArrayList<Term>();
		ArrayList<Term> clause2 = new ArrayList<Term>();
		for (int i = 0; i < clause_1.size(); i++) {
			Term temp = new Term();
			temp.isPositive = clause_1.get(i).isPositive;
			ArrayList<String> copyContent = new ArrayList<String>();
			ArrayList<String> originalContent = clause_1.get(i).content;
			for (int j = 0; j < originalContent.size(); j++) {
				String copyStr = new String(originalContent.get(j));
				copyContent.add(copyStr);
			}
			temp.content = copyContent;
			String copyPred = new String(clause_1.get(i).predicate);
			temp.predicate = copyPred;
			clause1.add(temp);
		}

		for (int i = 0; i < clause_2.size(); i++) {
			Term temp = new Term();
			temp.isPositive = clause_2.get(i).isPositive;
			ArrayList<String> copyContent = new ArrayList<String>();
			ArrayList<String> originalContent = clause_2.get(i).content;
			for (int j = 0; j < originalContent.size(); j++) {
				String copyStr = new String(originalContent.get(j));
				copyContent.add(copyStr);
			}
			temp.content = copyContent;
			String copyPred = new String(clause_2.get(i).predicate);
			temp.predicate = copyPred;
			clause2.add(temp);
		}

		if (clause1.get(position1).content.size() != clause2.get(position2).content.size()) 
			return null;	
		
		for (int i = 0; i < clause1.get(position1).content.size(); i++) {
			if (!(isNumeric(clause1.get(position1).content.get(i)) || isNumeric(clause2.get(position2).content.get(i))) 
					&& !clause1.get(position1).content.get(i).equals(clause2.get(position2).content.get(i))) {
				return null;
			}
		}
		
		ArrayList<String> content1 = new ArrayList<String>();
		for (int i = 0; i < clause1.size(); i++) {
			if (i != position1) {
				resolvedClause.add(clause1.get(i));
			}		
			else {
				content1.addAll(clause1.get(i).content);
			}
		}
		
		ArrayList<String> content2 = new ArrayList<String>();
		for (int i = 0; i < clause2.size(); i++) {
			if (i != position2)
				resolvedClause.add(clause2.get(i));
			else {
				content2.addAll(clause2.get(i).content);
			}
		}
		
		char c = 'a';
		Map<String, String> unificationMap = new HashMap<String, String>();
		for (int i = 0; i < content1.size(); i++) {
			if (isNumeric(content1.get(i)) && isNumeric(content2.get(i))) {
				if (unificationMap.containsKey(content1.get(i)) || unificationMap.containsKey(content2.get(i))) {
					if (unificationMap.containsKey(content1.get(i)) && !unificationMap.containsKey(content2.get(i))) {
						unificationMap.put(content2.get(i), unificationMap.get(content1.get(i)));
					}
					else if (unificationMap.containsKey(content2.get(i)) && !unificationMap.containsKey(content1.get(i))) {
						unificationMap.put(content1.get(i), unificationMap.get(content2.get(i)));
					}
				}
				else {
					unificationMap.put(content1.get(i), c+"");
					if (!content1.get(i).equals(content2.get(i)))
						unificationMap.put(content2.get(i), c+"");
					c++;
				}
			}
			else if (!isNumeric(content1.get(i)) && isNumeric(content2.get(i))) {
				if (unificationMap.containsKey(content2.get(i))) {
					if (!unificationMap.get(content2.get(i)).equals(content1.get(i))) {
						return null;
					}
				}
				else {
					unificationMap.put(content2.get(i), content1.get(i));
				}		
			}
			else if (isNumeric(content1.get(i)) && !isNumeric(content2.get(i))) {
				if (unificationMap.containsKey(content1.get(i))) {
					if (!unificationMap.get(content1.get(i)).equals(content2.get(i))) {
						return null;
					}
				}
				else {
					unificationMap.put(content1.get(i), content2.get(i));
				}		
			}
		}

		if (resolvedClause.size() == 0)	return resolvedClause;
		
		for (int i = 0; i < resolvedClause.size(); i++) {
			ArrayList<String> curr = resolvedClause.get(i).content;
			for (int j = 0; j < curr.size(); j++) {
				if (unificationMap.containsKey(curr.get(j))) {
					curr.add(j, unificationMap.get(curr.get(j)));
					curr.remove(j+1);
				}
				else if (isNumeric(curr.get(j))) {
					unificationMap.put(curr.get(j), c+"");					
					curr.remove(j);
					curr.add(j, c+"");
					c++;
				}
			}
		}
			
		resolvedClause = ToolClass.standardizeTermArrList(resolvedClause);
		return 	resolvedClause;
	}
	
	private String negation(String str) {		
		return str.charAt(0) == '~' ? str.substring(1) : "~" + str;
	}
	
	public boolean isNumeric(String s) {  
   	 	return s.matches("[-+]?\\d*\\.?\\d+");  
	} 
}