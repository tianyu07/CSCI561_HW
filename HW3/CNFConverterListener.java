// Generated from CNFConverter.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CNFConverterParser}.
 */
public interface CNFConverterListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CNFConverterParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(CNFConverterParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link CNFConverterParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(CNFConverterParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link CNFConverterParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(CNFConverterParser.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CNFConverterParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(CNFConverterParser.StatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CNFConverterParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(CNFConverterParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CNFConverterParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(CNFConverterParser.ExprContext ctx);
}