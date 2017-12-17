package cop5556fa17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
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
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//import cop5556fa17.image.ImageSupport;


public class CodeGenVisitor implements ASTVisitor, Opcodes {
	Map<Kind,Integer> hmap = new HashMap<>();;
	
	public void setMap(){
	hmap.put(Kind.KW_x, 1);
	hmap.put(Kind.KW_y, 2);
	hmap.put(Kind.KW_X, 3);
	hmap.put(Kind.KW_Y, 4);
	hmap.put(Kind.KW_r, 5);
	hmap.put(Kind.KW_a, 6);
	hmap.put(Kind.KW_R, 7);
	hmap.put(Kind.KW_A, 8);	
	hmap.put(Kind.KW_Z, 16777215);
	hmap.put(Kind.KW_DEF_X,256);
	hmap.put(Kind.KW_DEF_Y, 256);
	}
	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		setMap();
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		
 		mv.visitLocalVariable("x", "I", null, mainStart, mainEnd, 1);
		mv.visitLocalVariable("y", "I", null, mainStart, mainEnd, 2);
		mv.visitLocalVariable("X", "I", null, mainStart, mainEnd, 3);
		mv.visitLocalVariable("Y", "I", null, mainStart, mainEnd, 4);
		mv.visitLocalVariable("r", "I", null, mainStart, mainEnd, 5);
		mv.visitLocalVariable("a", "I", null, mainStart, mainEnd, 6);
		mv.visitLocalVariable("R", "I", null, mainStart, mainEnd, 7);
		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 8);
		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction of main method
		mv.visitEnd();
		
		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		Type type = declaration_Variable.getType();
		switch(type){
		case INTEGER:   FieldVisitor fv;
						fv = cw.visitField(ACC_STATIC, declaration_Variable.name, "I", null, null);
						fv.visitEnd();
						break;
						
		case BOOLEAN:   FieldVisitor fvisitor;
						fvisitor = cw.visitField(ACC_STATIC, declaration_Variable.name, "Z", null, null);
						fvisitor.visitEnd();
						break;
		}
		if(declaration_Variable.getE() != null) {
			declaration_Variable.getE().visit(this, arg);
			if(type == Type.INTEGER)
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, "I");
			if(type == Type.BOOLEAN)
			mv.visitFieldInsn(PUTSTATIC, className, declaration_Variable.name, "Z");
		}
		
		return null;

	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		expression_Binary.getE0().visit(this, arg);
		expression_Binary.getE1().visit(this, arg);
		Kind op = expression_Binary.getOp();
		Expression e0 = expression_Binary.getE0();
		Type t1 = e0.getType();
		Expression e1 = expression_Binary.getE1();
		Type t2 = e1.getType();
			switch (op) {
			case OP_PLUS:
				mv.visitInsn(IADD);
				break;
			case OP_MINUS:
				mv.visitInsn(ISUB);
				break;
			case OP_TIMES:
				mv.visitInsn(IMUL);
				break;
			case OP_DIV:
				mv.visitInsn(IDIV);
				break;
			case OP_MOD:
				mv.visitInsn(IREM);
				break;
			case OP_AND:
				mv.visitInsn(IAND);
				break;
			case OP_OR:
				mv.visitInsn(IOR);
				break;
			case OP_LT:
				Label f = new Label();
				mv.visitJumpInsn(IF_ICMPGE, f);
				mv.visitInsn(ICONST_1);
				Label t = new Label();
				mv.visitJumpInsn(GOTO, t);
				mv.visitLabel(f);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(t);
				break;

			case OP_LE:
				Label f6 = new Label();
				mv.visitJumpInsn(IF_ICMPGT, f6);
				mv.visitInsn(ICONST_1);
				Label t6 = new Label();
				mv.visitJumpInsn(GOTO, t6);
				mv.visitLabel(f6);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(t6);
				break;
			
			case OP_GT:
				Label f5 = new Label();
				mv.visitJumpInsn(IF_ICMPLE, f5);
				mv.visitInsn(ICONST_1);
				Label t5 = new Label();
				mv.visitJumpInsn(GOTO, t5);
				mv.visitLabel(f5);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(t5);
				break;

			case OP_GE:
				Label f7 = new Label();
				mv.visitJumpInsn(IF_ICMPLT, f7);
				mv.visitInsn(ICONST_1);
				Label t7 = new Label();
				mv.visitJumpInsn(GOTO, t7);
				mv.visitLabel(f7);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(t7);
				break;

			case OP_NEQ:
				Label f3 = new Label();
				mv.visitJumpInsn(IF_ICMPEQ, f3);
				mv.visitInsn(ICONST_1);
				Label t3 = new Label();
				mv.visitJumpInsn(GOTO, t3);
				mv.visitLabel(f3);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(t3);
				break;

			case OP_EQ:
				Label f4 = new Label();
				mv.visitJumpInsn(IF_ICMPNE, f4);
				mv.visitInsn(ICONST_1);
				Label t4 = new Label();
				mv.visitJumpInsn(GOTO, t4);
				mv.visitLabel(f4);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(t4);
				break;
				}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.getType());
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		expression_Unary.getE().visit(this, arg);
		Kind op = expression_Unary.getOp();	
		Type t = expression_Unary.getType();
		if(t==Type.INTEGER){
		switch(op){
		
		case OP_MINUS:	
			mv.visitInsn(INEG);
			break;
		case OP_EXCL:				
			mv.visitLdcInsn(Integer.MAX_VALUE);
			mv.visitInsn(IXOR);
			break;
		}
		}
		else if(t==Type.BOOLEAN){
			if(op == Kind.OP_EXCL){
				Label not = new Label();
				mv.visitJumpInsn(IFEQ, not);
				mv.visitInsn(ICONST_0);
				Label not2 = new Label();
				mv.visitJumpInsn(GOTO, not2);
				mv.visitLabel(not);
				mv.visitInsn(ICONST_1);
				mv.visitLabel(not2);
			}
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.getType());
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		index.getE0().visit(this, null);
		index.getE1().visit(this, null);
		if(!index.isCartesian()){
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, expression_PixelSelector.getName(), ImageSupport.ImageDesc);
		expression_PixelSelector.getIndex().visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className,"getPixel", ImageSupport.getPixelSig, false);
		return null;
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		Label falseLabel = new Label();
		Label endLabel = new Label();
		expression_Conditional.condition.visit(this, mv);
		mv.visitJumpInsn(IFEQ, falseLabel);
		expression_Conditional.trueExpression.visit(this, mv);
		mv.visitJumpInsn(GOTO, endLabel);
		mv.visitLabel(falseLabel);
    	expression_Conditional.falseExpression.visit(this, mv);
    	mv.visitLabel(endLabel);
		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		FieldVisitor fv;
		fv = cw.visitField(ACC_STATIC, declaration_Image.getName(), ImageSupport.ImageDesc, null, null);
		fv.visitEnd();
	
		if(declaration_Image.getSource() == null) {
			if(declaration_Image.getxSize() != null && declaration_Image.getySize() != null) {
				declaration_Image.getxSize().visit(this, arg);
				declaration_Image.getySize().visit(this, arg);
			}
			else{
				mv.visitLdcInsn(hmap.get(Kind.KW_DEF_X));
				mv.visitLdcInsn(hmap.get(Kind.KW_DEF_Y));
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig, false);
		}
		else {
			declaration_Image.getSource().visit(this, arg);
			if(declaration_Image.getxSize() != null && declaration_Image.getySize() != null) {
				declaration_Image.getxSize().visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}
			else {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
			}
		mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.getName(), ImageSupport.ImageDesc);
		return null;
	}
	
  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		Type type = source_StringLiteral.getType();
		mv.visitLdcInsn(source_StringLiteral.getFileOrUrl());
		return null;
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.getParamNum().visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		Type type = source_Ident.getType();
		mv.visitFieldInsn(GETSTATIC, className, source_Ident.getName(), ImageSupport.StringDesc);
		//mv.visitLdcInsn(source_Ident.getName());
		return null;
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		Type type = declaration_SourceSink.getType();
		FieldVisitor fv;
		fv = cw.visitField(ACC_STATIC, declaration_SourceSink.getName(), ImageSupport.StringDesc, null, null);
		fv.visitEnd();
		declaration_SourceSink.getSource().visit(this, arg);
		mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.getName(), ImageSupport.StringDesc);
		/*switch(type){
		case INTEGER: {
			fv = cw.visitField(ACC_STATIC, declaration_SourceSink.getName(),ImageSupport.StringDesc,null, null);
			fv.visitEnd();
			declaration_SourceSink.getSource().visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.getName(), ImageSupport.StringDesc);
			break;
		}
		
		case FILE: {
			fv = cw.visitField(ACC_STATIC, declaration_SourceSink.getName(), ImageSupport.StringDesc, null, null);
			fv.visitEnd();
			declaration_SourceSink.getSource().visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.getName(), ImageSupport.StringDesc);
			break;
		}
		
		case URL: {
			fv = cw.visitField(ACC_STATIC, declaration_SourceSink.getName(), ImageSupport.StringDesc, null, null);
			fv.visitEnd();
			declaration_SourceSink.getSource().visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, declaration_SourceSink.getName(), ImageSupport.StringDesc);
			break;
		}
		}*/
		return null;
	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		mv.visitLdcInsn(new Integer(expression_IntLit.getValue()));
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		expression_FunctionAppWithExprArg.getArg().visit(this, null);
		mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "abs", RuntimeFunctions.absSig, false);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		expression_FunctionAppWithIndexArg.getArg().getE0().visit(this, null);
		expression_FunctionAppWithIndexArg.getArg().getE1().visit(this, null);
		Kind kind = expression_FunctionAppWithIndexArg.getFunction();
		switch(kind){
		case KW_cart_x: mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);break;
		case KW_cart_y: mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);break;
		case KW_polar_a: mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);break;
		case KW_polar_r: mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);break;
		}
		return null;
	}
	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		
		Kind  kind = expression_PredefinedName.getKind();
		switch(kind){
		case KW_x: case KW_y: case KW_X: case KW_Y: case KW_R: case KW_A: 	mv.visitVarInsn(ILOAD, hmap.get(kind));break;
		case KW_DEF_X: case KW_DEF_Y: 	mv.visitLdcInsn(256);break;
		case KW_Z: 	mv.visitLdcInsn(hmap.get(Kind.KW_Z)); break;
		case KW_r: {
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_x));
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_y));
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
			mv.visitVarInsn(ISTORE, hmap.get(Kind.KW_r));
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_r));
		}
		break;
		case KW_a:{
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_x));
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_y));
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
			mv.visitVarInsn(ISTORE, hmap.get(Kind.KW_a));
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_a));
		}
		break;
		}
		return null;
	}
	
		

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		
		Type type = statement_Out.getDec().getType();
		switch(type){
		case BOOLEAN: {	
						mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
						mv.visitFieldInsn(GETSTATIC, className, statement_Out.getName(), "Z");
						CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V",false);
						break;
		}
						
		case INTEGER:	{
						mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
						mv.visitFieldInsn(GETSTATIC, className, statement_Out.getName(), "I");
						CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V",false);
						break;
		}
		
		case IMAGE: {
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.getName(),ImageSupport.ImageDesc);
			CodeGenUtils.genLogTOS(GRADE, mv, Type.IMAGE);
			statement_Out.getSink().visit(this, arg);
			break;
		}
		}
		return null;
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		if(statement_In.getSource()!=null){
			statement_In.getSource().visit(this, arg);

		}
		Type type = statement_In.getDec().getType();
		switch(type){
		case BOOLEAN: {	
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
						mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "Z");
						break;
		}
						
		case INTEGER: {
						mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt","(Ljava/lang/String;)I",false);
						mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "I");
						break;
		}
		
		case IMAGE: {
					Declaration_Image dec_Image =(Declaration_Image) statement_In.getDec();
					if(dec_Image.xSize == null && dec_Image.ySize == null) {
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
					mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
					mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, ImageSupport.ImageDesc);
					}
					else {
					dec_Image.xSize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
					dec_Image.ySize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
					mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig, false);
					mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, ImageSupport.ImageDesc);
					}
					break;
		}
		}
		return null;

	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		Type type = statement_Assign.getLhs().getType();
		switch(type){
		case INTEGER: case BOOLEAN: {
			statement_Assign.getE().visit(this, arg);
			statement_Assign.getLhs().visit(this, arg);
			break;
		}
		case IMAGE: {
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);
			mv.visitFieldInsn(GETSTATIC, className, statement_Assign.lhs.name, ImageSupport.ImageDesc);

			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
			mv.visitVarInsn(ISTORE, hmap.get(Kind.KW_X));
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitVarInsn(ISTORE, 4);

			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, hmap.get(Kind.KW_x));
			Label l1 = new Label();
			mv.visitLabel(l1);
			Label l2 = new Label();
			mv.visitJumpInsn(GOTO, l2);
			Label l3 = new Label();
			mv.visitLabel(l3);
			mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, hmap.get(Kind.KW_y));
			Label l4 = new Label();
			mv.visitLabel(l4);
			Label l5 = new Label();
			mv.visitJumpInsn(GOTO, l5);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
			statement_Assign.getE().visit(this, arg);
			statement_Assign.getLhs().visit(this, arg);
			Label l7 = new Label();
			mv.visitLabel(l7);
			mv.visitIincInsn(hmap.get(Kind.KW_y), 1);
			mv.visitLabel(l5);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_y));
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_Y));
			mv.visitJumpInsn(IF_ICMPLT, l6);
			Label l8 = new Label();
			mv.visitLabel(l8);
			mv.visitIincInsn(1, 1);
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_x));
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_X));
			mv.visitJumpInsn(IF_ICMPLT, l3);
			break;
		}
		}
		return null;
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		Type type = lhs.getType();
		switch(type){
		case INTEGER:{
			mv.visitFieldInsn(PUTSTATIC, className, lhs.getName(), "I");
			break;
		}
		
		case BOOLEAN:{
			mv.visitFieldInsn(PUTSTATIC, className, lhs.getName(), "Z");
			break;
		}
		
		case IMAGE: {
			mv.visitFieldInsn(GETSTATIC, className, lhs.getName(), ImageSupport.ImageDesc);
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_x));
			mv.visitVarInsn(ILOAD, hmap.get(Kind.KW_y));
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false);
			break;
		}
		}
		return null;		
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeFrame", ImageSupport.makeFrameSig, false);
		mv.visitInsn(POP);
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.getName(), ImageSupport.StringDesc);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig, false);
		return sink_Ident;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		mv.visitLdcInsn(expression_BooleanLit.isValue());
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		Type type = expression_Ident.getType();
		if(type == Type.INTEGER) {
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.getName(), "I");
		}
		else if(type == Type.BOOLEAN) {
			mv.visitFieldInsn(GETSTATIC, className, expression_Ident.getName(), "Z");
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.getType());
		return null;

	}

}
