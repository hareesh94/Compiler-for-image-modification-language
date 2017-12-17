package cop5556fa17;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeCheckVisitor.SemanticException;

import static cop5556fa17.Scanner.Kind.*;

public class TypeCheckTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}
	
	
	/**
	 * Scans, parses, and type checks given input String.
	 * 
	 * Catches, prints, and then rethrows any exceptions that occur.
	 * 
	 * @param input
	 * @throws Exception
	 */
	void typeCheck(String input) throws Exception {
		show(input);
		try {
			Scanner scanner = new Scanner(input).scan();
			ASTNode ast = new Parser(scanner).parse();
			show(ast);
			ASTVisitor v = new TypeCheckVisitor();
			ast.visit(v, null);
		} catch (Exception e) {
			show(e);
			throw e;
		}
	}

	/**
	 * Simple test case with an almost empty program.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSmallest() throws Exception {
		String input = "n"; //Smallest legal program, only has a name
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the Scanner
		Parser parser = new Parser(scanner); // Create a parser
		ASTNode ast = parser.parse(); // Parse the program
		TypeCheckVisitor v = new TypeCheckVisitor();
		String name = (String) ast.visit(v, null);
		show("AST for program " + name);
		show(ast);
	}

	 @Test
	 public void testProgName() throws Exception {
	 String input = "prog";
	 typeCheck(input);
	 }

	 @Test
	 public void testExpression_Binary() throws Exception {
	 String input = "prog";
	 typeCheck(input);
	 }
	
	/**
	 * This test should pass with a fully implemented assignment
	 * @throws Exception
	 */
	 @Test
	 public void testDec1() throws Exception {
	 String input = "prog int k = 42;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail1() throws Exception {
	 String input = "prog int j=1; image k <- @j+2;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail2() throws Exception {
	 String input = "prog int un = +10;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail3() throws Exception {
	 String input = "prog int k1; int k1;";              //check-expected to throw exception.
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail4() throws Exception {
	 String input = "prog boolean k = 3 >= 4;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail5() throws Exception {
	 String input = "prog boolean k = 3 == 4;";
	 typeCheck(input);
	 }
	 @Test
	 public void testfail6() throws Exception {
	 String input = "prog boolean k = 3 != 4;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail7() throws Exception {
	 String input = "prog boolean ident1; boolean ident2; boolean k = ident1 & ident2 | ident1;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail8() throws Exception {
	 String input = "prog int k = 45 / 56;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail9() throws Exception {
	 String input = "prog int k = 5 % 10;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail10() throws Exception {
	 String input = "prog int k; boolean k;";
	 thrown.expect(SemanticException.class);

	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail11() throws Exception {
	 String input = "prog boolean k = 5 == 6 ? 1 < 2 : 1 > 3;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail12() throws Exception {
	 String input = "prog int k = 5 > 6 ? 1 : 0;";
	 typeCheck(input);
	 }
	 
	 
	 @Test
	 public void testfail13() throws Exception {
	 String input = "p int n; n = sin(30)/cos(40);\n";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail14() throws Exception {
	 String input = "prog file f1=\"file_name\";image [x+y,y] img <-f1;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail15() throws Exception {
	 String input = "prog file f = \"file\"; image img <- f; img -> SCREEN;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail16() throws Exception {
	 String input = "prog file s = \"some source\"; image i; i -> s;";
	 typeCheck(input);
	 }
	 
	 @Test
	 public void testfail17() throws Exception {
	 String input = "prog boolean k = 5 > 6 ? true : false;";
	 typeCheck(input);
	 }
	 /**
	  * This program does not declare k. The TypeCheckVisitor should
	  * throw a SemanticException in a fully implemented assignment.
	  * @throws Exception
	  */
	 @Test
	 public void testUndec() throws Exception {
	 String input = "prog k = 42;";
	 thrown.expect(SemanticException.class);
	 typeCheck(input);
	 }


}
