import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.lang.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CNFConverter{

    public static Set<String> getCNFClause(Set<String> sentenceSet) throws Exception{

    	FileWriter outputFile = new FileWriter("output.txt");
		BufferedWriter bw = new BufferedWriter(outputFile);
		for(String curr: sentenceSet){
  			bw.write(curr + "\n");
		}
		bw.close();

		ANTLRFileStream inputFile = new ANTLRFileStream("output.txt");
	    CNFConverterLexer lexer = new CNFConverterLexer(inputFile);	    
		CommonTokenStream tokens = new CommonTokenStream(lexer); 
	    CNFConverterParser parser = new CNFConverterParser(tokens);
	    ParseTree tree = parser.prog();
		ParseTreeWalker walker = new ParseTreeWalker();
		CNFConverter_Eliminate eliminator = new CNFConverter_Eliminate(); 
		walker.walk(eliminator, tree);       // walk parse tree 

		FileWriter outputFile1 = new FileWriter("output.txt");
		BufferedWriter bw1 = new BufferedWriter(outputFile1);
		for(String curr: eliminator.KB){
  			bw1.write(curr + "\n");
		}
		bw1.close();

		ANTLRFileStream inputFile2 = new ANTLRFileStream("output.txt");
	    CNFConverterLexer lexer2 = new CNFConverterLexer(inputFile2);	    
		CommonTokenStream tokens2 = new CommonTokenStream(lexer2); 
	    CNFConverterParser parser2 = new CNFConverterParser(tokens2);
	    ParseTree tree2 = parser2.prog();
		ParseTreeWalker walker2 = new ParseTreeWalker();
		CNFConverter_Negate negator = new CNFConverter_Negate(); 
		walker2.walk(negator, tree2); 
		
		FileWriter outputFile2 = new FileWriter("output.txt");
		BufferedWriter bw2 = new BufferedWriter(outputFile2);
		for(String curr: negator.KB){
  			bw2.write(curr + "\n");
		}
		bw2.close();

		Set<String> KnowledgeBase = negator.KB;
		int count = 0;


		//Need to loop enough time until KB doesn't change anymore
		while (true) {
			ANTLRFileStream inputFile3 = new ANTLRFileStream("output.txt");
			CNFConverterLexer lexer3 = new CNFConverterLexer(inputFile3);	    
			CommonTokenStream tokens3 = new CommonTokenStream(lexer3); 
		    CNFConverterParser parser3 = new CNFConverterParser(tokens3);
		    ParseTree tree3 = parser3.prog();
			ParseTreeWalker walker3 = new ParseTreeWalker();
			CNFConverter_Distributer distributer = new CNFConverter_Distributer(); 
			walker3.walk(distributer, tree3);
						
			FileWriter outputFile3 = new FileWriter("output.txt");
			BufferedWriter bw3 = new BufferedWriter(outputFile3);
			for(String curr: distributer.KB){
	  			bw3.write(curr + "\n");
	  			if (KnowledgeBase.contains(curr)) {
	  				count++;
	  			}
			}
			bw3.close();

			if (KnowledgeBase.size() == count) {
				break;
			} 
			else {
				KnowledgeBase = distributer.KB;
				count = 0;
			}
		}

		FileWriter outputFile4 = new FileWriter("output.txt");
		BufferedWriter bw4 = new BufferedWriter(outputFile4);
		for(String curr: KnowledgeBase){
  			bw4.write(curr + "\n");
		}
		bw4.close();


		ANTLRFileStream inputFile4 = new ANTLRFileStream("output.txt");
		CNFConverterLexer lexer4 = new CNFConverterLexer(inputFile4);	    
		CommonTokenStream tokens4 = new CommonTokenStream(lexer4); 
	    CNFConverterParser parser4 = new CNFConverterParser(tokens4);
	    ParseTree tree4 = parser4.prog();
		ParseTreeWalker walker4 = new ParseTreeWalker();
		CNFConverter_Separator separator = new CNFConverter_Separator(); 
		walker4.walk(separator, tree4);

		FileWriter outputFile5 = new FileWriter("output.txt");
		BufferedWriter bw5 = new BufferedWriter(outputFile5);
		for(String curr: separator.KB){
  			bw5.write(curr + "\n");
		}
		bw5.close();

		ANTLRFileStream inputFile5 = new ANTLRFileStream("output.txt");
		CNFConverterLexer lexer5 = new CNFConverterLexer(inputFile5);	    
		CommonTokenStream tokens5 = new CommonTokenStream(lexer5); 
	    CNFConverterParser parser5 = new CNFConverterParser(tokens5);
	    ParseTree tree5 = parser5.prog();
		ParseTreeWalker walker5 = new ParseTreeWalker();
		CNFConverter_De_bracket deBracket = new CNFConverter_De_bracket(); 
		walker5.walk(deBracket, tree5);

		PrintWriter writer = new PrintWriter("output.txt");
		writer.print("");
		writer.close();

		return deBracket.KB;
    }
	
}

class CNFConverter_Negate extends CNFConverterBaseListener {
    	
    	Map<String, String> visitedMap = new HashMap<String, String>();
    	Map<String, Integer> negativeMap = new HashMap<String, Integer>(); 
    	private int count = 0;   	
		Set<String> KB = new HashSet<String>();
		String props = "";
		String visitedExpr0 = "";
		String visitedExpr1 = "";
		
		//In enterExpr, to every ctx we count the num of "~" before it
		@Override 
		public void enterExpr(CNFConverterParser.ExprContext ctx) { 
			negativeMap.put(ctx.getText(), count);
			if (ctx.NOT() != null) {
				count++;
			}
		}

		@Override
		public void exitExpr(CNFConverterParser.ExprContext ctx) {

			//In the lowest level, defining if ctx has odd number of "~" before it
			//If yes, add (~) to it; if no, do nothing
 			if (ctx.expr(0) == null) {
				if (negativeMap.get(ctx.getText()) % 2 == 0) {
					visitedMap.put(ctx.getText(), ctx.getText());
				}	
				else {
					visitedMap.put(ctx.getText(), "(~" + ctx.getText() + ")");			
				}
			}
			//Negate the expr by change "&" "|" 
			else if (ctx.expr(0) != null && ctx.getChildCount() == 5) {
				if (negativeMap.get(ctx.getText()) % 2 == 0) {
					visitedExpr0 = visitedMap.get(ctx.expr(0).getText());
					visitedExpr1 = visitedMap.get(ctx.expr(1).getText());
					if (ctx.AND() != null) {
						props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "&" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
					} 
					else if (ctx.OR() != null) {
						props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
					}
					visitedMap.put(ctx.getText(), props);
				}	
				else {
					visitedExpr0 = visitedMap.get(ctx.expr(0).getText());
					visitedExpr1 = visitedMap.get(ctx.expr(1).getText());
					if (ctx.AND() != null) {
						props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
					} 
					else if (ctx.OR() != null) {
						props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "&" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
					}
					visitedMap.put(ctx.getText(), props);
				}
			}
			else if (ctx.NOT() != null) {
				//When walking over a "~", let count--
				count--;
				props = visitedMap.get(ctx.expr(0).getText());
				visitedMap.put(ctx.getText(), props);
			}
			else {
				props = visitedMap.get(ctx.expr(0).getText());
				visitedMap.put(ctx.getText(), props);
			}
			

		}

		@Override
		public void exitStat(CNFConverterParser.StatContext ctx) { 
			props = visitedMap.get(ctx.getChild(0).getText());
			KB.add(props);
			count = 0;
		} 
    }
class CNFConverter_Eliminate extends CNFConverterBaseListener {

		Map<String, String> visitedMap = new HashMap<String, String>();
		Set<String> KB = new HashSet<String>();
		String props = "";
		String visitedExpr0 = "";
		String visitedExpr1 = "";
		
		@Override
		public void exitExpr(CNFConverterParser.ExprContext ctx) {

			//If ctx has five children including "=>", then turn it to be ( (~expr(0)) | expr(1) )
			//expr(0) and expr(1) need to be got from visitedMap
			if (ctx.IMPLY() != null) {
				if (visitedMap.containsKey(ctx.expr(0).getText()) && visitedMap.containsKey(ctx.expr(1).getText())) {
					visitedExpr0 = visitedMap.get(ctx.expr(0).getText());
					visitedExpr1 = visitedMap.get(ctx.expr(1).getText());
					props = ctx.LEFTBRAC() + "(~" + visitedExpr0 + ")|" + visitedExpr1 + ctx.RIGHTBRAC();
					visitedMap.put(ctx.getText(), props);
				}
				else if (visitedMap.containsKey(ctx.expr(0).getText())) {
					visitedExpr0 = visitedMap.get(ctx.expr(0).getText());
					props = ctx.LEFTBRAC() + "(~" + visitedExpr0 + ")|" + ctx.expr(1).getText() + ctx.RIGHTBRAC();
					visitedMap.put(ctx.getText(), props);
				}
				else if (visitedMap.containsKey(ctx.expr(1).getText())) {
					visitedExpr1 = visitedMap.get(ctx.expr(1).getText());
					props = ctx.LEFTBRAC() + "(~" + ctx.expr(0).getText() + ")|" + visitedExpr1 + ctx.RIGHTBRAC();
					visitedMap.put(ctx.getText(), props);
				}
				else {
					props = ctx.LEFTBRAC() + "(~" + ctx.expr(0).getText() + ")|" + ctx.expr(1).getText() + ctx.RIGHTBRAC();
					visitedMap.put(ctx.getText(), props);
				}
			}
			//If ctx doesn't have "=>" then store the combined value to visitedMap
			else if (ctx.getChildCount() == 5 && ctx.IMPLY() == null) {
				if (visitedMap.containsKey(ctx.expr(0).getText())) {
					visitedExpr0 = visitedMap.get(ctx.expr(0).getText());
					visitedExpr1 = visitedMap.get(ctx.expr(1).getText());
					props = ctx.LEFTBRAC() + visitedExpr0 + ctx.getChild(2) + visitedExpr1 + ctx.RIGHTBRAC();
					visitedMap.put(ctx.getText(), props);
				}
			}
			//To those ctxs which have 2,3 or 4 children, as ( expr )  \  ~  expr(0)  \  A ( x )  , store their value directly into visitedMap
			else {
				visitedMap.put(ctx.getText(), ctx.getText());
			}
		}

		@Override
		public void exitStat(CNFConverterParser.StatContext ctx) { 
			props = visitedMap.get(ctx.getChild(0).getText());
			KB.add(props);
		} 
    }

class CNFConverter_De_bracket extends CNFConverterBaseListener {

		String arrlist = "";
		Set<String> KB = new HashSet<String>();
		
		@Override
		public void exitExpr(CNFConverterParser.ExprContext ctx) {
			if (ctx.getChildCount() == 2 || (ctx.getChildCount() == 4 && ctx.getParent().getChildCount() != 2)) {
				arrlist += ctx.getText() + " ";
			}
		}

		@Override
		public void exitStat(CNFConverterParser.StatContext ctx) { 
			if (ctx.getChild(0).getChildCount() == 4) {
				arrlist = ctx.getChild(0).getText();
			}
			KB.add(arrlist);
			arrlist = "";
		} 
	}	


class CNFConverter_Separator extends CNFConverterBaseListener {
    	
    	Map<String, String> visitedMap = new HashMap<String, String>();
		Set<String> KB = new HashSet<String>();
		String props = "";
		String props1 = "";
		String props2 = "";
		boolean hasSeparated = false;

		//separate CNF clauses into independent DNF clauses
		@Override
		public void enterExpr(CNFConverterParser.ExprContext ctx) {
			if (ctx.AND() != null) {
				if (!(ctx.expr(0).AND() != null || ctx.expr(1).AND() != null)) {
					KB.add(ctx.expr(0).getText());
					KB.add(ctx.expr(1).getText());
				}
				else if (ctx.expr(0).AND() != null && (!(ctx.expr(1).AND() != null))) {
					KB.add(ctx.expr(1).getText());
				}
				else if (ctx.expr(1).AND() != null && (!(ctx.expr(0).AND() != null))) {
					KB.add(ctx.expr(0).getText());
				}
				hasSeparated = true;
			}	
		}

		@Override
		public void exitStat(CNFConverterParser.StatContext ctx) { 
			if (!hasSeparated) 
				KB.add(ctx.getChild(0).getText());
			else 
				hasSeparated = false;	
		} 
	}

class CNFConverter_Distributer extends CNFConverterBaseListener {
    	
    	Map<String, String> visitedMap = new HashMap<String, String>();
		Set<String> KB = new HashSet<String>();
		String props = "";
		String props1 = "";
		String props2 = "";
		String visitedExpr02 = "";
		String visitedExpr03 = "";
		String visitedExpr12 = "";
		String visitedExpr13 = "";
		String visitedExpr023 = "";
		String visitedExpr123 = "";
		String visitedExpr012 = "";
		String visitedExpr013 = "";
		
		@Override
		public void exitExpr(CNFConverterParser.ExprContext ctx) {

			/*
				There are 8 forms of expression that need to do distribution, but only 3 forms are important and need to process: 
				
				1. ( expr(0) & expr(1) ) | ( expr(2) & expr(3) ) => ( exp02 & exp03 ) & ( exp12 & exp13 )
				2. ( expr(0) | expr(1) ) | ( expr(2) & expr(3) ) =>`( exp012 & exp013 )
				3. ( expr(0) & expr(1) ) | ( expr(2) | expr(3) ) => ( exp023 & exp123 ) 

				subclause exp02 == ( expr(0) | expr(2) )

			*/

			//The first situation
			if (ctx.getChildCount() == 5 && ctx.expr(0).getChildCount() == 5 && ctx.expr(1).getChildCount() == 5) {
				if (ctx.OR() != null) {
					if (ctx.expr(0).AND() != null && ctx.expr(1).AND() != null) {
						visitedExpr02 = "(" + visitedMap.get(ctx.expr(0).expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).expr(0).getText()) + ")";
						visitedExpr03 = "(" + visitedMap.get(ctx.expr(0).expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).expr(1).getText()) + ")";
						visitedExpr12 = "(" + visitedMap.get(ctx.expr(0).expr(1).getText()) + "|" + visitedMap.get(ctx.expr(1).expr(0).getText()) + ")";
						visitedExpr13 = "(" + visitedMap.get(ctx.expr(0).expr(1).getText()) + "|" + visitedMap.get(ctx.expr(1).expr(1).getText()) + ")";
						
						props1 = "(" + visitedExpr02 + "&" + visitedExpr03 + ")";
						visitedMap.put(ctx.expr(0).getText(), props1);

						props2 = "(" + visitedExpr12 + "&" + visitedExpr13 + ")";
						visitedMap.put(ctx.expr(1).getText(), props2);

						props = "(" + props1 + "&" + props2 + ")";
						visitedMap.put(ctx.getText(), props);

					}
					else if (ctx.expr(0).AND() != null && ctx.expr(1).OR() != null) {
						visitedExpr023 = "((" + visitedMap.get(ctx.expr(0).expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).expr(0).getText()) + ")" + "|" + visitedMap.get(ctx.expr(1).expr(1).getText()) + ")";
						visitedExpr123 = "((" + visitedMap.get(ctx.expr(0).expr(1).getText()) + "|" + visitedMap.get(ctx.expr(1).expr(0).getText()) + ")" + "|" + visitedMap.get(ctx.expr(1).expr(1).getText()) + ")";
						
						props1 = visitedExpr023;
						visitedMap.put(ctx.expr(0).getText(), props1);
						
						props2 = visitedExpr123;
						visitedMap.put(ctx.expr(1).getText(), props2);

						props = "(" + props1 + "&" + props2 + ")";
						visitedMap.put(ctx.getText(), props);
					}
					else if (ctx.expr(0).OR() != null && ctx.expr(1).AND() != null) {
						visitedExpr012 = "((" + visitedMap.get(ctx.expr(0).expr(0).getText()) + "|" + visitedMap.get(ctx.expr(0).expr(1).getText()) + ")" + "|" + visitedMap.get(ctx.expr(1).expr(0).getText()) + ")";
						visitedExpr013 = "((" + visitedMap.get(ctx.expr(0).expr(0).getText()) + "|" + visitedMap.get(ctx.expr(0).expr(1).getText()) + ")" + "|" + visitedMap.get(ctx.expr(1).expr(1).getText()) + ")";
						
						props1 = visitedExpr012;
						visitedMap.put(ctx.expr(0).getText(), props1);
						
						props2 = visitedExpr013;
						visitedMap.put(ctx.expr(1).getText(), props2);

						props = "(" + props1 + "&" + props2 + ")";
						visitedMap.put(ctx.getText(), props);
					}
					else {
						props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
						visitedMap.put(ctx.getText(), props);
					}					
				}
				else {
					props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "&" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
					visitedMap.put(ctx.getText(), props);
				}
			}

			//Second situation
			else if (ctx.getChildCount() == 5 && ctx.expr(0).getChildCount() == 5) {
				if (ctx.OR() != null) {
					if (ctx.expr(0).AND() != null) {
						props1 = "(" + visitedMap.get(ctx.expr(0).expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).getText()) + ")";
						props2 = "(" + visitedMap.get(ctx.expr(0).expr(1).getText()) + "|" + visitedMap.get(ctx.expr(1).getText()) + ")";
						props = "(" + props1 + "&" + props2 + ")";
						visitedMap.put(ctx.getText(), props);
 					}
					else {
						props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
						visitedMap.put(ctx.getText(), props);
					}
				}
				else {
					props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "&" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
					visitedMap.put(ctx.getText(), props);
				}
			}

			//Third situation
			else if (ctx.getChildCount() == 5 && ctx.expr(1).getChildCount() == 5) {
				if (ctx.OR() != null) {
					if (ctx.expr(1).AND() != null) {
						props1 = "(" + visitedMap.get(ctx.expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).expr(0).getText()) + ")";
						props2 = "(" + visitedMap.get(ctx.expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).expr(1).getText()) + ")";
						props = "(" + props1 + "&" + props2 + ")";
						visitedMap.put(ctx.getText(), props);
 					}
					else {
						props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "|" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
						visitedMap.put(ctx.getText(), props);
					}
				}
				else {
					props = ctx.LEFTBRAC() + visitedMap.get(ctx.expr(0).getText()) + "&" + visitedMap.get(ctx.expr(1).getText()) + ctx.RIGHTBRAC();
					visitedMap.put(ctx.getText(), props);
				}
			}

			//Other situations
			else {
				visitedMap.put(ctx.getText(), ctx.getText());
				
			}
		}

		@Override
		public void exitStat(CNFConverterParser.StatContext ctx) { 
			props = visitedMap.get(ctx.getChild(0).getText());
			KB.add(props);
		} 
	}
