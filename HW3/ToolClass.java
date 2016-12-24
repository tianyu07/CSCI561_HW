import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class ToolClass{
	private static int distinguishNum = 0;
	
	public static Term storeAsTerm(String s) {
		if (s.length() == 0) {
			return null;
		}
		while (s.charAt(0) == '(') {
			s = s.substring(1,s.length()-1);
		}
		Term temp = new Term();
		int predicateOccur = s.indexOf('(');
		int contentOccur = s.indexOf(')');
		if (s.charAt(0) == '~') {
			temp.isPositive = false;
			temp.predicate = s.substring(1, predicateOccur);
		}
		else
			temp.predicate = s.substring(0, predicateOccur);
		String con = s.substring(predicateOccur+1, contentOccur);
		String[] content = con.split(",");
		for (int i = 0; i < content.length; i++) {
			temp.content.add(content[i]);
		}
		return temp;
	}

	public static String standardize(String CNFClause) {
		String returnStr = "";
		Map<Character, Integer> variableIndex = new HashMap<Character, Integer>();
		if (!CNFClause.contains(" ")) {
			returnStr = standardizeOneTerm(CNFClause, variableIndex);
		} 
		else {
			String[] clause = CNFClause.split(" ");
			for (int i = 0; i < clause.length; i++) {
				returnStr += standardizeOneTerm(clause[i], variableIndex) + " ";
			}
		}	
		return returnStr;
	}

	public static ArrayList<Term> standardizeTermArrList(ArrayList<Term> resolvedClause) {
		String returnStr = "";
		ArrayList<Term> returnArr = new ArrayList<Term>();
		Map<Character, Integer> variableIndex = new HashMap<Character, Integer>();
		for (int i = 0; i < resolvedClause.size(); i++) {
			returnStr = standardizeOneTerm(resolvedClause.get(i).toString(), variableIndex);
			returnArr.add(storeAsTerm(returnStr));
		}
		return returnArr;		
	}

	private static String standardizeOneTerm(String clause, Map<Character, Integer> variableIndex) {
		int predicateOccur = clause.indexOf('(');
		int contentOccur = clause.indexOf(')');
		String[] content = clause.substring(predicateOccur+1, contentOccur).split(",");
		String con = "";
		for (int j = 0; j < content.length; j++) {
			if (content[j].length() == 1 && Character.isLowerCase(content[j].charAt(0))) {
				if (!variableIndex.containsKey(content[j].charAt(0))) {
					variableIndex.put(content[j].charAt(0), distinguishNum);
					distinguishNum++;
				}			
				content[j] = "" + variableIndex.get(content[j].charAt(0));
			}
			con += j == content.length - 1? content[j] : content[j] + ",";
		}
		return clause.substring(0, predicateOccur) + "(" + con + ")";
	}

	public static boolean isNumeric(String s) {  
   	 	return s.matches("[-+]?\\d*\\.?\\d+");  
	} 
	
	
}