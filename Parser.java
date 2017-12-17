package cop5556fa17;



import java.util.ArrayList;
import java.util.Arrays;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;
import cop5556fa17.AST.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;  
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		Token firstToken = t;
		Token f = t;
		ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		match(Kind.IDENTIFIER);
		while(t.kind.equals(KW_int) || t.kind.equals(KW_boolean) || t.kind.equals(KW_image)|| t.kind.equals(KW_url)
				|| t.kind.equals(KW_file) || t.kind.equals(Kind.IDENTIFIER)){
			if(t.kind.equals(KW_int) || t.kind.equals(KW_boolean) || t.kind.equals(KW_image)|| t.kind.equals(KW_url)
					|| t.kind.equals(KW_file)){
			Declaration declaration = declaration();
			decsAndStatements.add(declaration);
			match(Kind.SEMI);
			}
			else if(t.kind.equals(Kind.IDENTIFIER)){
				Statement statement = statement(); 
				decsAndStatements.add(statement);
				match(Kind.SEMI);
			}
		}
		return new Program(firstToken, f, decsAndStatements);
	}

//Declaration :: = VariableDeclaration | ImageDeclaration | SourceSinkDeclaration
	Declaration declaration() throws SyntaxException{
		if(t.kind.equals(KW_int) || t.kind.equals(KW_boolean)){
			Declaration_Variable variableDeclaration = variableDeclaration();
			return variableDeclaration;
		}
		
		else if(t.kind.equals(KW_image)){
			Declaration_Image imageDeclaration = imageDeclaration();
			return imageDeclaration;
		}
		
		else if(t.kind.equals(KW_url) || t.kind.equals(KW_file)){
			Declaration_SourceSink sourceSinkDeclaration = sourceSinkDeclaration();
			return sourceSinkDeclaration;
		}
		else throw new SyntaxException(t, "This token not expected in declarartion");
	}

//VariableDeclaration ::= VarType IDENTIFIER ( = Expression | ε )
	Declaration_Variable variableDeclaration() throws SyntaxException{
		Token firstToken = t;
		Token t1 = varType();
		Token t2 = t;
		Expression expression = null;
		match(Kind.IDENTIFIER);
		if(t.kind.equals(Kind.OP_ASSIGN)){
			consume();
		    expression = expression();
		}
		return new Declaration_Variable(firstToken, t1, t2, expression);
	}

//VarType ::= KW_int | KW_boolean
	Token varType() throws SyntaxException{
		Token t1 = t;
	if(t.kind.equals(Kind.KW_int) || t.kind.equals(Kind.KW_boolean)){
		consume();
	}
	else 
		throw new SyntaxException(t, "This token not accepted in varType");
	return t1;
	}
	
//ImageDeclaration ::= KW_image (LSQUARE Expression COMMA Expression RSQUARE | ε) IDENTIFIER ( OP_LARROW Source | ε )	
	Declaration_Image imageDeclaration() throws SyntaxException{
		Token firstToken = t;
		Expression e1 = null;
		Expression e2 = null;
		Token t1;
		Source s = null;
	   consume();
	 if(t.kind.equals(Kind.LSQUARE)){
		 consume();
		 e1 = expression();
		 match(Kind.COMMA);
		 e2 = expression();
		 match(Kind.RSQUARE);
	 }
	 t1 = t;
	 match(Kind.IDENTIFIER);
	 if(t.kind.equals(Kind.OP_LARROW)){
		 consume();
		 s = source();
	 }
	return new Declaration_Image(firstToken, e1, e2, t1, s);
	 }

//Source ::= STRING_LITERAL |  OP_AT Expression | IDENTIFIER
	Source source() throws SyntaxException{
		Token firstToken = t;
		if(t.kind.equals(Kind.STRING_LITERAL)){
			String s = t.getText();
			consume();
			return new Source_StringLiteral(firstToken, s);
		}
		else if(t.kind.equals(Kind.IDENTIFIER)){
			Token t1 = t;
			consume();
			return new Source_Ident(firstToken, t1);
		}
		else if(t.kind.equals(Kind.OP_AT)){
			Expression e;
			consume();
			e = expression();
			return new Source_CommandLineParam(firstToken, e);
		}
		
		else 
			throw new SyntaxException(t, "This token is not accepted in source");
	}

//SourceSinkDeclaration ::= SourceSinkType IDENTIFIER OP_ASSIGN Source	
	Declaration_SourceSink sourceSinkDeclaration() throws SyntaxException{
		Token firstToken = t;
		Token t1;
		Token t2;
		Source s;
		t1 = sourceSinkType();
		t2 = t;
		match(Kind.IDENTIFIER);
		match(Kind.OP_ASSIGN);
		s = source();
		return new Declaration_SourceSink(firstToken, t1, t2, s);
	}

//SourceSinkType := KW_url | KW_file
	Token sourceSinkType() throws SyntaxException{
		Token t1 = t;
		consume();
		return t1;
	}

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	public Expression expression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Expression e2 = null;
		e0 = orExpression();
		if(t.kind.equals(Kind.OP_Q)){
			consume();
			e1 = expression();
			match(Kind.OP_COLON);
			e2 = expression();
			return new Expression_Conditional(firstToken, e0, e1, e2);
			}
		return e0;
	}
	
//OrExpression ::= AndExpression ( OP_OR AndExpression)*
	Expression orExpression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = andExpression();
		while(t.kind.equals(Kind.OP_OR)){
			Token op = t;
			consume();
			e1 = andExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
//AndExpression ::= EqExpression ( OP_AND EqExpression )*	
	Expression andExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = eqExpression();
		while(t.kind.equals(Kind.OP_AND)){
			Token op = t;
			consume();
			e1 = eqExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
//EqExpression ::= RelExpression ( (OP_EQ | OP_NEQ ) RelExpression )*
	Expression eqExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = relExpression();
		while(t.kind.equals(Kind.OP_EQ) || t.kind.equals(Kind.OP_NEQ)){
			Token op = t;
			consume();
			e1 = relExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
//RelExpression ::= AddExpression ( ( OP_LT | OP_GT | OP_LE | OP_GE ) AddExpression)*
	Expression relExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Token op = null;
		e0 = addExpression();
		while(t.kind.equals(Kind.OP_LT) || t.kind.equals(Kind.OP_GT) || t.kind.equals(Kind.OP_LE) || t.kind.equals(Kind.OP_GE)){
			op = t;
			consume();
			e1 = addExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
//AddExpression ::= MultExpression ( (OP_PLUS | OP_MINUS ) MultExpression )*
	Expression addExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = multExpression();
		while(t.kind.equals(Kind.OP_PLUS) || t.kind.equals(Kind.OP_MINUS)){
			Token op = t;
			consume();
			e1 = multExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}

//MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV | OP_MOD ) UnaryExpression )*
	Expression multExpression() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = unaryExpression();
		while(t.kind.equals(Kind.OP_TIMES) || t.kind.equals(Kind.OP_DIV) || t.kind.equals(Kind.OP_MOD)){
			Token op = t;
			consume();
			e1 = unaryExpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
	}
	
//UnaryExpression ::= OP_PLUS UnaryExpression |  OP_MINUS UnaryExpression | UnaryExpressionNotPlusMinus
	Expression unaryExpression() throws SyntaxException{
		Token firstToken = t;
		if(t.kind.equals(Kind.OP_PLUS) || t.kind.equals(Kind.OP_MINUS)){
			Token op = t;
			consume();
			Expression e = unaryExpression();
			return new Expression_Unary(firstToken, op, e);
		}
		
		else {
			Expression exp = unaryExpressionNotPlusMinus();
			return exp;
		}
	}

/* UnaryExpressionNotPlusMinus ::= OP_EXCL UnaryExpression | Primary
| IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X | KW_Y | KW_Z |
KW_A | KW_R | KW_DEF_X | KW_DEF_Y */
	Expression unaryExpressionNotPlusMinus() throws SyntaxException{
		Token firstToken = t;
		if(t.kind.equals(Kind.OP_EXCL)){
			Token op = t;
			consume();
			Expression e = unaryExpression();
			return new Expression_Unary(firstToken, op, e);
		}
		
		else if(t.kind.equals(Kind.KW_x) || t.kind.equals(Kind.KW_y) || t.kind.equals(Kind.KW_r) || t.kind.equals(Kind.KW_a) ||
				t.kind.equals(Kind.KW_X) || t.kind.equals(Kind.KW_Y) || t.kind.equals(Kind.KW_Z) || t.kind.equals(Kind.KW_A) ||
				t.kind.equals(Kind.KW_R) || t.kind.equals(Kind.KW_DEF_X) || t.kind.equals(Kind.KW_DEF_Y)){
			Kind k = t.kind;
			consume();
			return new Expression_PredefinedName(firstToken, k);
		}
		
		else if(t.kind.equals(Kind.IDENTIFIER)){
			Expression e = identOrPixelSelectorExpression();
			return e;
		}
		
		else if(t.kind.equals(Kind.INTEGER_LITERAL) || t.kind.equals(Kind.LPAREN) || t.kind.equals(Kind.KW_sin) ||
				t.kind.equals(Kind.KW_cos) || t.kind.equals(Kind.KW_atan) || t.kind.equals(Kind.KW_abs) || 
				t.kind.equals(Kind.KW_cart_x) || t.kind.equals(Kind.KW_cart_y) ||
				t.kind.equals(Kind.KW_polar_a) || t.kind.equals(Kind.KW_polar_r) || t.kind.equals(Kind.BOOLEAN_LITERAL)){
			Expression e = primary();
			return e;
		}	
		else 
			throw new SyntaxException(t, "Unexpected Token in expression");
	}
	
//IdentOrPixelSelectorExpression::= IDENTIFIER LSQUARE Selector RSQUARE | IDENTIFIER
	Expression identOrPixelSelectorExpression() throws SyntaxException{  
		Token firstToken = t;
		Token t1 = t;
		Index i = null;
		match(Kind.IDENTIFIER);
		if(t.kind.equals(Kind.LSQUARE)){
			consume();
			i = selector();
			match(Kind.RSQUARE);
			return new Expression_PixelSelector(firstToken, t1, i);
		}
		return new Expression_Ident(firstToken, t1);
	}
	
//Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication | BOOLEAN_LITERAL
	Expression primary() throws SyntaxException{
		Token firstToken = t;
		if(t.kind.equals(Kind.INTEGER_LITERAL)){
			int value = t.intVal();
			consume();
			return new Expression_IntLit(firstToken, value);
		}
	
		else if(t.kind.equals(Kind.LPAREN)){
			consume();
			Expression e = expression();
			match(Kind.RPAREN);
			return e;
		}
		
		else if(t.kind.equals(Kind.BOOLEAN_LITERAL)){
			String s = t.getText();
			boolean value = Boolean.parseBoolean(s);
			consume();
			return new Expression_BooleanLit(firstToken, value);
		}
		
		else {
			Expression e = functionApplication();
			return e;
		}
	}

//FunctionApplication ::= FunctionName LPAREN Expression RPAREN | FunctionName LSQUARE Selector RSQUARE	
	Expression functionApplication() throws SyntaxException{
		Token firstToken = t;
		Kind t1;
		Expression e = null;
		Index i = null;
		t1 = functionName();
		if(t.kind.equals(Kind.LPAREN)){
			consume();
			e = expression();
			match(Kind.RPAREN);
			return new Expression_FunctionAppWithExprArg(firstToken, t1, e);
		}
		
		else if(t.kind.equals(Kind.LSQUARE)){
			consume();
			i = selector();
			match(Kind.RSQUARE);
			return new Expression_FunctionAppWithIndexArg(firstToken, t1, i);
		}
		
		else 
			throw new SyntaxException(t, "Token not accepted");
		}

//FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | LW_polar_r
	Kind functionName() throws SyntaxException{
		if(t.kind.equals(Kind.KW_sin) || t.kind.equals(Kind.KW_cos) || t.kind.equals(Kind.KW_atan) ||
				t.kind.equals(Kind.KW_abs) || t.kind.equals(Kind.KW_cart_x) || t.kind.equals(Kind.KW_cart_y) ||
				t.kind.equals(Kind.KW_polar_a) || t.kind.equals(Kind.KW_polar_r)){
			Kind t1 = t.kind;
			consume();
			return t1;
		}
		
		else 
			throw new SyntaxException(t, "Not the expected token in funtionName");
		}
	
//Selector ::= Expression COMMA Expression	
	Index selector() throws SyntaxException{
		Token firstToken = t;
		Expression e1;
		Expression e2;
		e1 = expression();
		match(Kind.COMMA);
		e2 = expression();
		return new Index(firstToken,e1,e2);
	}
	
//Statement ::= AssignmentStatement | ImageOutStatement | ImageInStatement
	Statement statement() throws SyntaxException{
		Token firstToken = t;
		Token t1 = t;
		LHS l = null;
		Index i = null;
		consume();
		if(t.kind.equals(Kind.OP_RARROW)){
			consume();
			Sink s = sink();
			return new Statement_Out(firstToken, t1, s);
		}
		
		else if(t.kind.equals(Kind.OP_LARROW)){
			consume();
			Source s = source();
			return new Statement_In(firstToken, t1, s);
		}
		
		else if(t.kind.equals(Kind.LSQUARE)){
			consume();
			i = lhsSelector();
			match(Kind.RSQUARE);
		    l = new LHS(firstToken, t1, i);
		}
		l = new LHS(firstToken, t1, i);
		match(Kind.OP_ASSIGN);
		Expression e = expression();
		return new Statement_Assign(firstToken, l, e);
		}
	
//LhsSelector ::= LSQUARE ( XySelector | RaSelector ) RSQUARE
	Index lhsSelector() throws SyntaxException{
		Index i = null;
		match(Kind.LSQUARE);
		if(t.kind.equals(Kind.KW_x)){
			i = xySelector();
		}
		
		else if(t.kind.equals(Kind.KW_r)){
			i = raSelector();
		}
		match(Kind.RSQUARE);
		return i;
	}

//XySelector ::= KW_x COMMA KW_y
	Index xySelector() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = new Expression_PredefinedName(firstToken, t.kind);
		consume();
		match(Kind.COMMA);
		e1 = new Expression_PredefinedName(firstToken, t.kind);
		match(Kind.KW_y);
		return new Index(firstToken, e0, e1);
	}

//RaSelector ::= KW_r , KW_A
	Index raSelector() throws SyntaxException{
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = new Expression_PredefinedName(firstToken, t.kind);
		consume();
		match(Kind.COMMA);
		e1 = new Expression_PredefinedName(firstToken, t.kind);
		match(Kind.KW_a);
		return new Index(firstToken, e0, e1);
	}

//Sink ::= IDENTIFIER | KW_SCREEN
	Sink sink() throws SyntaxException{
		Token firstToken = t;
		Token t1 = t;
		if(t.kind.equals(Kind.IDENTIFIER)){
			consume();
			return new Sink_Ident(firstToken, t1);
		}
		else if(t.kind.equals(Kind.KW_SCREEN)){
			consume();
			return new Sink_SCREEN(firstToken);
		}
		else 
			throw new SyntaxException(t, "This token is not accepted in sink");
	}
	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
	private Token match(Kind kind) throws SyntaxException {
		if (t.kind.equals(kind)) {
			return consume();
		}
		String message =  "Expected" +kind +"but saw" +t.kind +"at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
	private Token consume() throws SyntaxException {
		Token temp = t;
		t = scanner.nextToken();
		return temp;
	}
}