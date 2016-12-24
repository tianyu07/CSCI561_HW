import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Initialization {
	private ArrayList<String> arrlist;
	private ArrayList<Term> querySet;
	private Set<Term> factsSet;
	private Set<String> sentenceSet;
	private int qlength;
	private int flength;
	
	public Initialization(ArrayList<String> arrlist) {
		this.arrlist = arrlist;
		this.querySet = new ArrayList<Term>();
		this.factsSet = new HashSet<Term>();
		this.sentenceSet = new HashSet<String>();
		this.qlength = Integer.parseInt(arrlist.get(0));
		this.flength = Integer.parseInt(arrlist.get(qlength+1));
		separateQuery();
		separateFactSentence();
	}

	private void separateQuery() {
		if (qlength == 0) System.out.println("Error: no query.");
		for (int i = 1; i <= qlength; i++) {
			String s = arrlist.get(i);
			Term temp = ToolClass.storeAsTerm(s);
			querySet.add(temp);
		}
	}

	private void separateFactSentence() {
		if (flength == 0) System.out.println("Error: no knowledge base.");
		for (int i = qlength+2; i < flength+qlength+2; i++) {
			String s = arrlist.get(i);
			if (isFact(s)) {
				Term temp = ToolClass.storeAsTerm(s);
				factsSet.add(temp);
			}
			else
				sentenceSet.add(s);		
		}
	}

	private boolean isFact(String s) {
		if (s.contains("=>") || s.contains("&") || s.contains("|"))
			return false;
		while (s.charAt(0) == '(') {
			s = s.substring(1,s.length()-1);
			if (s.charAt(0) == '~') 
				s = s.substring(1);
		}
		String[] content = s.substring(s.indexOf('(')+1, s.indexOf(')')).split(",");
		for (int i = 0; i < content.length; i++) 
			if (content[i].length() == 1 && Character.isLowerCase(content[i].charAt(0)))
				return false;
		return true;		
	}

	public ArrayList<Term> getQuery() {		
		return querySet;
	}

	public Set<Term> getFact() {
		return factsSet;
	}

	public Set<String> getSentence() {
		return sentenceSet;
	}

	
}