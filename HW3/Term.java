import java.util.ArrayList;

public class Term {
	public String predicate;
	public ArrayList<String> content;
	public boolean isPositive;
	
	public Term() {
		content = new ArrayList<String>(); 
		predicate = "";
		isPositive = true;
	}
	
	public String toString() {
		String content2String = "";
		for (int i = 0; i < content.size() - 1; i++) {
			content2String += content.get(i) + ",";
		}
		content2String += content.get(content.size() - 1);
		String s = isPositive == true? "" : "~";
		return s + predicate + "(" + content2String + ")";
	}
	
}