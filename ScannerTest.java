/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	@Test
	public void testLeftParenth() throws LexicalException {
		String input = "(";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, LPAREN, 0, 1, 1, 1);
	}
	

	@Test
	public void testRightParenth() throws LexicalException {
		String input = ")";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, RPAREN, 0, 1, 1, 1);
	}
	
	@Test
	public void testLeftSqaure() throws LexicalException {
		String input = "[";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, LSQUARE, 0, 1, 1, 1);
	}
	
	@Test
	public void testRightSqaure() throws LexicalException {
		String input = "]";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, RSQUARE, 0, 1, 1, 1);
	}
	
	@Test
	public void testComma() throws LexicalException {
		String input = ",";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, COMMA, 0, 1, 1, 1);
	}
	
	@Test
	public void testAnd() throws LexicalException {
		String input = "&";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_AND, 0, 1, 1, 1);
	}
	
	@Test
	public void testOr() throws LexicalException {
		String input = "|";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_OR, 0, 1, 1, 1);
	}
	
	@Test
	public void testOperator() throws LexicalException {
		String input = "+@%?:";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_PLUS, 0, 1, 1, 1);
		checkNext(scanner, OP_AT, 1, 1, 1, 2);
		checkNext(scanner, OP_MOD, 2, 1, 1, 3);
		checkNext(scanner, OP_Q, 3, 1, 1, 4);
		checkNext(scanner, OP_COLON, 4, 1, 1, 5);


	}
	
	@Test
	public void testAssign() throws LexicalException {
		String input = "=";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_ASSIGN, 0, 1, 1, 1);
	}
	
	@Test
	public void testEqual() throws LexicalException {
		String input = "==";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_EQ, 0, 2, 1, 1);
	}
	
	@Test
	public void testEqualWithSpace() throws LexicalException {
		String input = "=\n=";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_ASSIGN, 0, 1, 1, 1);
		checkNext(scanner, OP_ASSIGN, 2, 1, 2, 1);

	}
	
	@Test
	public void testGT() throws LexicalException {
		String input = ">";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_GT, 0, 1, 1, 1);
	}
	
	@Test
	public void testGE() throws LexicalException {
		String input = ">=";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_GE, 0, 2, 1, 1);
	}
	
	@Test
	public void testLE() throws LexicalException {
		String input = "<=";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_LE, 0, 2, 1, 1);
	}
	
	@Test
	public void testLT() throws LexicalException {
		String input = "<";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_LT, 0, 1, 1, 1);
	}
	
	@Test
	public void testExcl() throws LexicalException {
		String input = "!";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_EXCL, 0, 1, 1, 1);
	}
	
	@Test
	public void testNotEqual() throws LexicalException {
		String input = "!=";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_NEQ, 0, 2, 1, 1);
	}
	
	@Test
	public void testRightArrow() throws LexicalException {
		String input = "->";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_RARROW, 0, 2, 1, 1);
	}
	
	@Test
	public void testLeftArrow() throws LexicalException {
		String input = "<-";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_LARROW, 0, 2, 1, 1);
	}
	
	@Test
	public void testPower() throws LexicalException {
		String input = "**";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_POWER, 0, 2, 1, 1);
	}
	
	@Test
	public void testTimes() throws LexicalException {
		String input = "*";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_TIMES, 0, 1, 1, 1);
	}
	
	@Test
	public void testDiv() throws LexicalException {
		String input = "/";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, OP_DIV, 0, 1, 1, 1);
	}
	
	@Test
	public void testZero() throws LexicalException {
		String input = "0";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
	}
	
	@Test
	public void testDigit() throws LexicalException {
		String input = "123";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, INTEGER_LITERAL, 0, 3, 1, 1);
	}
	
	@Test
	public void testZeroAndDigit() throws LexicalException {
		String input = "0123";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, INTEGER_LITERAL, 0, 1, 1, 1);
        checkNext(scanner, INTEGER_LITERAL, 1, 3, 1, 2);
	}
	
	@Test
	public void testIdentifier1() throws LexicalException {
		String input = "abc";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, IDENTIFIER, 0, 3, 1, 1);
	}
	
	@Test
	public void testIdentifier2() throws LexicalException {
		String input = "_0123abc";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, IDENTIFIER, 0, 8, 1, 1);
	}
	
	@Test
	public void testKeyword1() throws LexicalException {
		String input = "boolean";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, KW_boolean, 0, 7, 1, 1);

	}
	
	@Test
	public void testKeyword2() throws LexicalException {
		String input = "boolean\nurl\natan";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, KW_boolean, 0, 7, 1, 1);
		checkNext(scanner, KW_url, 8, 3, 2, 1);
		checkNext(scanner, KW_atan, 12, 4, 3, 1);
    }
	
	@Test
	public void testBoolean() throws LexicalException {
		String input = "true";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1);

	}
	
	@Test
	public void testComment1() throws LexicalException {
		String input = "//Hi\ntrue";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, BOOLEAN_LITERAL, 5, 4, 2, 1);

	}
	
	@Test
	public void testComment2() throws LexicalException {
		String input = "//";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNextIsEOF(scanner);
    }
	
	@Test
	public void testComment3() throws LexicalException {
		String input = "//Hi How are you? true false\ntrue";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		check(scanner, BOOLEAN_LITERAL, 4);
		checkNextIsEOF(scanner);
    }
	
	@Test
	public void testSpace() throws LexicalException {
		String input = "a b";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		checkNext(scanner, KW_a, 0, 1, 1, 1);
		checkNext(scanner, IDENTIFIER, 2, 1, 1, 3);
		checkNextIsEOF(scanner);
	}
		
	@Test
	public void testStringLit1() throws LexicalException {
		String input = "\"Hi\"";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, STRING_LITERAL, 0, 4, 1, 1);
   }
	
	@Test
	public void testStringLit2() throws LexicalException {
		String input = "\"Hi \\t hi\"";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, STRING_LITERAL, 0, 10, 1, 1);
   }
	
	@Test
	public void testStringLit3() throws LexicalException {
		String input = "\"a\'\\\\j\"";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);  
		checkNext(scanner, STRING_LITERAL, 0, 7, 1, 1);
   }
	
	@Test
	public void testComplex1() throws LexicalException {
		String input = "\n\rabc\ntrue//Hi";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		checkNext(scanner, IDENTIFIER, 2, 3, 3, 1);
		checkNext(scanner, BOOLEAN_LITERAL, 6, 4, 4, 1);
   }
	
	@Test
	public void testComplex2() throws LexicalException {
		String input = "X\r\nplp012\n012x//Hi";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		checkNext(scanner, KW_X, 0, 1, 1, 1);
		checkNext(scanner, IDENTIFIER, 3, 6, 2, 1);
		checkNext(scanner, INTEGER_LITERAL, 10, 1, 3, 1);
		checkNext(scanner, INTEGER_LITERAL, 11, 2, 3, 2);
		checkNext(scanner, KW_x, 13, 1, 3, 4);
	}
	
	@Test
	public void testComplex4() throws LexicalException {
		String input = "boolean true\nsin x=1;\ncos y=0;";  
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		checkNext(scanner, KW_boolean, 0, 7, 1, 1);
		checkNext(scanner, BOOLEAN_LITERAL, 8, 4, 1, 9);
		checkNext(scanner, KW_sin, 13, 3, 2, 1);
		checkNext(scanner, KW_x, 17, 1, 2, 5);
		checkNext(scanner, OP_ASSIGN, 18, 1, 2, 6);
		checkNext(scanner, INTEGER_LITERAL, 19, 1, 2, 7);
		checkNext(scanner, SEMI,20, 1, 2, 8);
		checkNext(scanner, KW_cos,22, 3, 3, 1);
		checkNext(scanner, KW_y, 26, 1, 3, 5);
		checkNext(scanner, OP_ASSIGN, 27, 1, 3, 6);
		checkNext(scanner, INTEGER_LITERAL, 28, 1, 3, 7);
		checkNext(scanner, SEMI,29, 1, 3, 8);
	}
	
	@Test
	public void testMix1() throws LexicalException {
		String input = "\r\n//This is comment\nabc";
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		checkNext(scanner, IDENTIFIER, 20, 3, 3, 1);
   }
	
	@Test
	public void testMix2() throws LexicalException {
		String input = "\n\n\n//comment !@# \r\n \"string\\\\\\t\\b\\n\\f\\r\\\'\\\"\"";
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		checkNext(scanner, STRING_LITERAL, 20, 24, 5, 2);
   }
	
	@Test
	public void testMix3() throws LexicalException {
		String input = "\"abc\"def";
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		checkNext(scanner, STRING_LITERAL, 0, 5, 1, 1);
		checkNext(scanner, IDENTIFIER, 5, 3, 1, 6);

   }
	@Test
	public void testMix4() throws LexicalException {
		String input = "\"\bABC\"";
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		checkNext(scanner, STRING_LITERAL, 0, 6, 1, 1);

   }
	
	@Test
	public void testMix5() throws LexicalException {
		String input = "E";
		show(input);        
		Scanner scanner = new Scanner(input).scan();  
		show(scanner); 
		checkNext(scanner, IDENTIFIER, 0, 1, 1, 1);

   }
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void testWhiteSpace() throws LexicalException {
		String input = "\n\r\t\r\n\n\r";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNextIsEOF(scanner);
	}
	
	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "\" greetings  ";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failStringLiteral1() throws LexicalException {
		String input = "\"a\nb\"";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(2,e.getPos());
			throw e;
		}
	}
	
	
	
	@Test
	public void failStringLiteral3() throws LexicalException {
		String input = "\"a\"gc\"";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(6,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failIntegerLiteral() throws LexicalException {
		String input = "999999999999999999999999";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(0,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failIdentifier() throws LexicalException {
		String input = "SCREEN(\"a\nb\");";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(9,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failUnrecCharacter() throws LexicalException {
		String input = "^";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(0,e.getPos());
			throw e;
		}
	}
	
	
	
	@Test
	public void failUnrecCharacter1() throws LexicalException {
		String input = "\\This is comment/nabc";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(0,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failString1() throws LexicalException {
		String input = "\" greetings \\ \"";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(13,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failString2() throws LexicalException {
		String input = "\"Hari\\\"";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(7,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failString3() throws LexicalException {
		String input = "\"abc\\\"a";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(7,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failString4() throws LexicalException {
		String input = "\"greet\\ings\"";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(7,e.getPos());
			throw e;
		}
	}
	
	@Test
	public void failString5() throws LexicalException {
		String input = "\"\nABC\"";
		show(input);
		thrown.expect(LexicalException.class);  
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  
			show(e);
			assertEquals(1,e.getPos());
			throw e;
		}
	}
		
		
}
