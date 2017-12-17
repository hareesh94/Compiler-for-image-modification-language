package cop5556fa17;

import java.net.URL;
import java.util.HashMap;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {
	

	HashMap<String,Declaration> decMap =  new HashMap<String,Declaration>();
	HashMap<String,Type> typeMap = new HashMap<String,Type>();
	
		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		

	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		
		String name = declaration_Variable.name;
		if(typeMap.containsKey(name) == true){
			throw new SemanticException(declaration_Variable.firstToken, "Error in map");
		}
		else {
			declaration_Variable.setType(TypeUtils.getType(declaration_Variable.type));
			if(declaration_Variable.getE() != null){ 
				declaration_Variable.getE().visit(this, null);
				Type t = declaration_Variable.getE().getType();
				if(declaration_Variable.getType() != t){
					throw new SemanticException(declaration_Variable.firstToken, "Error in Dec_variable");
				}
			}
			
			decMap.put(name, declaration_Variable);
			typeMap.put(name, TypeUtils.getType(declaration_Variable.type));
		}
		return declaration_Variable.getType();
		
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		expression_Binary.getE0().visit(this, null);
		expression_Binary.getE1().visit(this, null);
		Type t1 = expression_Binary.getE0().getType();
		Type t2 = expression_Binary.getE1().getType();
		Kind op = expression_Binary.getOp();
		if(t1 == t2){
			if(op == Kind.OP_EQ || op == Kind.OP_NEQ) {
				expression_Binary.setType(Type.BOOLEAN);
				return expression_Binary.getType();
			}
			else if((op == Kind.OP_GE || op == Kind.OP_GT || op == Kind.OP_LT || op == Kind.OP_LE) && 
					(t1 == Type.INTEGER)) {
				expression_Binary.setType(Type.BOOLEAN);
				return expression_Binary.getType();
			}
			else if((op == Kind.OP_OR || op == Kind.OP_AND) && (t1 == Type.INTEGER || t1 == Type.BOOLEAN)){
				expression_Binary.setType(t1);
				return expression_Binary.getType();
			}
			else if((op == Kind.OP_PLUS || op == Kind.OP_MINUS || op == Kind.OP_DIV || op == Kind.OP_TIMES ||
					op == Kind.OP_POWER || op == Kind.OP_MOD) && (t1 == Type.INTEGER)) {
				expression_Binary.setType(Type.INTEGER);
				return expression_Binary.getType();
			}
		}
		throw new SemanticException(expression_Binary.firstToken, "Not correct type in Expression_Binary");

	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		expression_Unary.getE().visit(this, null);
		Type t = expression_Unary.getE().getType();
		Kind op = expression_Unary.getOp();
		if((op == Kind.OP_EXCL) && (t == Type.BOOLEAN || t == Type.INTEGER)) {
			expression_Unary.setType(t);
		}
		else if((op == Kind.OP_PLUS || op == Kind.OP_MINUS) && (t == Type.INTEGER)) {
			expression_Unary.setType(t);
		}
		else
			expression_Unary.setType(null);
		if(expression_Unary.getType() == null) {
			throw new SemanticException(expression_Unary.firstToken, "Not correct type in Epression_Unary");
		}
		return expression_Unary.getType();
}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		index.getE0().visit(this, null);
		index.getE1().visit(this, null);
		Type t1 = index.getE0().getType();
		Type t2 = index.getE1().getType();
		if(t1 == Type.INTEGER && t2 == Type.INTEGER) {
			 index.setCartesian(!(index.e0.getKind() == Kind.KW_r && index.e1.getKind() == Kind.KW_a));//check here if test fails.
			 return index.isCartesian();
		}
		throw new SemanticException(index.firstToken, "not correct type in index");
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		String name = expression_PixelSelector.name;
		Type t = typeMap.get(name);
		if(t == Type.IMAGE){
			expression_PixelSelector.setType(Type.INTEGER);
		}
		else if(expression_PixelSelector.index == null) {
			expression_PixelSelector.setType(t);
		}
		else expression_PixelSelector.setType(null);
		if(expression_PixelSelector.getType() == null)
			throw new SemanticException(expression_PixelSelector.firstToken, "pixel_selector is null");
		return expression_PixelSelector.getType();
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		expression_Conditional.getCondition().visit(this, null);
		expression_Conditional.getTrueExpression().visit(this, null);
		expression_Conditional.getFalseExpression().visit(this, null);
		Type t1 = expression_Conditional.getCondition().getType();
		Type t2 = expression_Conditional.getTrueExpression().getType();
		Type t3 = expression_Conditional.getFalseExpression().getType();
		if(t1 == Type.BOOLEAN && t2 == t3) {
			expression_Conditional.setType(t2);
			return expression_Conditional.getType();
		}
		throw new SemanticException(expression_Conditional.firstToken, "Not correct type in expression_conditional");
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		String name = declaration_Image.name;
		if(typeMap.get(name) != null){
			throw new SemanticException(declaration_Image.firstToken, "Hash map not empty");
		}
		else{
			decMap.put(name, declaration_Image);
			typeMap.put(name, TypeUtils.getType(declaration_Image.firstToken));
			declaration_Image.setType(Type.IMAGE);
		}
		if(declaration_Image.xSize != null && declaration_Image.ySize != null) {
			 declaration_Image.getxSize().visit(this, null);
			 declaration_Image.getySize().visit(this, null);
			 Type t1 = declaration_Image.getxSize().getType();
			 Type t2 = declaration_Image.getySize().getType();
			 if(t1 != Type.INTEGER && t2 != Type.INTEGER) {
					throw new SemanticException(declaration_Image.firstToken, "Error in Declaration_Image"); 
				}
		}
			 if(declaration_Image.source != null) {		//why this needed here.
					declaration_Image.source.visit(this, null);
				}
				return declaration_Image.getType(); 
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		
		String s = source_StringLiteral.fileOrUrl;
		if(isValidURL(s)) {
			source_StringLiteral.setType(Type.URL);
		}
		else source_StringLiteral.setType(Type.FILE);
		return source_StringLiteral.getType();
	}
	
	public boolean isValidURL(String url) {
	    try {
	        new URL(url);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		source_CommandLineParam.getParamNum().visit(this, null);
	//	Type t = source_CommandLineParam.getParamNum().getType();
		source_CommandLineParam.setType(null);
		if(source_CommandLineParam.getParamNum().getType() == Type.INTEGER) {
			return source_CommandLineParam.getType();
		}
		throw new SemanticException(source_CommandLineParam.firstToken, "not correct token in vommandline_param");
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		String name = source_Ident.name;
		source_Ident.setType(typeMap.get(name));
		if(source_Ident.getType() == Type.FILE || source_Ident.getType() == Type.URL) {
			return source_Ident.getType();
		}
		throw new SemanticException(source_Ident.firstToken, "Type in source_Ident is not file or url");
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		String name = declaration_SourceSink.name;
		if(typeMap.get(name) != null){
			throw new SemanticException(declaration_SourceSink.firstToken, "Map not empty");

		}
		else {
			decMap.put(name, declaration_SourceSink);
			typeMap.put(name, TypeUtils.getType(declaration_SourceSink.firstToken));
			declaration_SourceSink.setType(TypeUtils.getType(declaration_SourceSink.firstToken));
		}
			if(declaration_SourceSink.source != null) {
				declaration_SourceSink.getSource().visit(this, null);
				Type t = declaration_SourceSink.getSource().getType();
				if(t == declaration_SourceSink.getType() || t == null) {
				return declaration_SourceSink.getType();
				}
			}
			throw new SemanticException(declaration_SourceSink.firstToken, "Error entry found");
	}
	
	
	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		expression_IntLit.setType(Type.INTEGER);
		return expression_IntLit.getType();
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		expression_FunctionAppWithExprArg.getArg().visit(this, null);
		Type t = expression_FunctionAppWithExprArg.getArg().getType();
		if(t == Type.INTEGER) {
			expression_FunctionAppWithExprArg.setType(Type.INTEGER);
			return expression_FunctionAppWithExprArg.getType();
		}
		throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "not correct token in functionAppWithExprArg");
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		expression_FunctionAppWithIndexArg.getArg().visit(this, null);
		Type t = expression_FunctionAppWithIndexArg.getArg().getType();
		expression_FunctionAppWithIndexArg.setType(Type.INTEGER);
		return expression_FunctionAppWithIndexArg.getType();
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		expression_PredefinedName.setType(Type.INTEGER);
		return expression_PredefinedName.getType();
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		String name = statement_Out.name;
		statement_Out.setDec(decMap.get(name));
		if(decMap.get(name) == null){
			throw new SemanticException(statement_Out.firstToken, "entry in map is empty");
		}
		Type t1 = typeMap.get(name);
		statement_Out.getSink().visit(this, null);
		Type t2 = statement_Out.getSink().getType();
		if(!(((t1 == Type.INTEGER || t1 == Type.BOOLEAN) && (t2 == Type.SCREEN)) ||
				(t1 == Type.IMAGE && (t2 == Type.FILE || t2 == Type.SCREEN)))) {
			throw new SemanticException(statement_Out.firstToken, "require condition of Statement_Out is not satisfied");
		}
		return statement_Out.getDec();
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		String name = statement_In.name;
		statement_In.setDec(decMap.get(name));
		statement_In.getSource().visit(this, null);
		Type t = statement_In.getSource().getType();
		/*if(!((decMap.get(name) != null) && (t == typeMap.get(name)))){
			throw new SemanticException(statement_In.firstToken, "Error in statement_In");
		}*/
		return statement_In.getDec();
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		
		statement_Assign.getLhs().visit(this, null);
		statement_Assign.getE().visit(this, null);
		Type t1 = statement_Assign.getLhs().getType();
		Type t2 = statement_Assign.getE().getType();
		if((t1 == t2) ||(t1 == Type.IMAGE && t2 == Type.INTEGER)) {
			statement_Assign.setCartesian(statement_Assign.lhs.isCartesian());
			return statement_Assign.isCartesian();
		}
		throw new SemanticException(statement_Assign.firstToken, "not cottect type in statement_assign");
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		String name = lhs.name;
		lhs.setDec(decMap.get(name));
		lhs.setType(typeMap.get(name));
		if(lhs.getIndex() != null){
			lhs.index.visit(this, null);
			lhs.setCartesian(lhs.index.isCartesian());
		}
		return lhs.getType();
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		sink_SCREEN.setType(Type.SCREEN);
		return sink_SCREEN.getType();
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		String name = sink_Ident.name;
		sink_Ident.setType(typeMap.get(name));
		if(sink_Ident.getType() == Type.FILE) {
			return sink_Ident.getType();
		}
		throw new SemanticException(sink_Ident.firstToken, "Type in Sink_ident is not File");
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
		expression_BooleanLit.setType(Type.BOOLEAN);
		return expression_BooleanLit.getType();
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		String name = expression_Ident.name;
		expression_Ident.setType(typeMap.get(name));
		return expression_Ident.getType();
	}

}
