/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
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


import java.util.ArrayList;
import java.util.Arrays;

public class Scanner {
	
	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		
		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}
		
		public int getPos() { return pos; }

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, 
		KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, 
		KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, 
		LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}
	
	public static enum State{
		START,IN_EQUAL,IN_GT,IN_LT,IN_EXCL,IN_MINUS,IN_STAR,IN_DIGIT,
		IN_IDENT,IN_DIV,IN_COMMENT,IN_STRINGLIT,IN_ESCCHECK,END_STRINGLIT;
	}

	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;

	private static final Throwable startPos = null;
	
	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;
	
	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  



	
	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
	}


	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		/* TODO  Replace this with a correct and complete implementation!!! */
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		int startPos = 0;
		int startPosInLine = 0;
		State state = State.START;
		System.out.println(chars.length);
		while(pos < chars.length){
		char ch = chars[pos] ;
		switch(state) {
		  case START: {
				 ch = chars[pos];
				 startPos = pos;
				 startPosInLine = posInLine;
				 switch(ch){
				 case '(': {
						tokens.add(new Token(Kind.LPAREN, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                     } break;
                     
				 case ')': {
						tokens.add(new Token(Kind.RPAREN, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                          } break; 
                  
				 case '[': {
						tokens.add(new Token(Kind.LSQUARE, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                         } break; 
               
				 case ']': {
						tokens.add(new Token(Kind.RSQUARE, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                         } break; 
               
                 case ';': {
						tokens.add(new Token(Kind.SEMI, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                      } break;
                      
                 case ',': {
						tokens.add(new Token(Kind.COMMA, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                   } break;
                   
                 case '&': {
						tokens.add(new Token(Kind.OP_AND, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                 	} break; 
                
                 case '|': {
						tokens.add(new Token(Kind.OP_OR, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                 	} break; 
                     
                 case '+': {
						tokens.add(new Token(Kind.OP_PLUS, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                 	} break; 
           
                 case '@': {
						tokens.add(new Token(Kind.OP_AT, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                 	} break; 
                 
                 case '%': {
						tokens.add(new Token(Kind.OP_MOD, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                 } break;
              
                 case '?': {
						tokens.add(new Token(Kind.OP_Q, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
                 } break;
           
                 case ':': {
						tokens.add(new Token(Kind.OP_COLON, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
              } break;
              
                 case '=': {
                	 state = State.IN_EQUAL;
                	 pos++;
                 } break;
                 
                 case '>': {
                	 state = State.IN_GT;
                	 pos++;
                 } break;
              
                 case '<': {
                	 state = State.IN_LT;
                	 pos++;
                 } break;
                 
                 case '!': {
                	 state = State.IN_EXCL;
                	 pos++;
                 } break;
                 
                 case '-': {
                	 state = State.IN_MINUS;
                	 pos++;
                 } break;
                 
                 case '*': {
                	 state = State.IN_STAR;
                	 pos++;
                 } break;
                 
                 case '/': {
                	 state = State.IN_DIV;
                	 pos++;
                 } break;
                 
                 case '"': {
                	 state = State.IN_STRINGLIT;
                	 pos++;
                	 posInLine++;
                 }break;
                 
                
                 
                 default : {
                	 if (Character.isWhitespace(ch)){
                		 if(chars[pos] == '\n'){
                			 posInLine = 1;
                			 line++;
                			 pos++;
                		 }
                		 
                		 else if(chars[pos] == '\r'){
                			 posInLine = 1;
                			 line++;
                			 pos++;
                			 if(chars[pos] == '\n'){
                				 pos++;
                			 }
                			 
                		 }
                		
                		 
                		 else{
                		     pos++;
                		     posInLine++;
                	     }
                   }
                	 
                	 else if(pos == chars.length-1){
            			 tokens.add(new Token(Kind.EOF, startPos, 0, line, posInLine));
                         pos++;
                         posInLine++;
            		 }
                	 
                	 else if(Character.isDigit(ch)){
 						if(ch == '0'){
                             tokens.add(new Token(Kind.INTEGER_LITERAL, startPos, 1, line, posInLine));
 							pos++;
 							posInLine++;
 							state = State.START;
 						}else{
 							state = State.IN_DIGIT;
 							pos++;
 							posInLine++;

 						}
                 }
                	 else if (Character.isJavaIdentifierStart(ch)) {
 						state = State.IN_IDENT;
 						pos++;
 						posInLine++;
 					} 
                	 
                	 else{
                		 throw new LexicalException("Unrecognized character", pos);
                	 }
				 }break; //end of default
				 }//end switch(ch)
				 }break;//end of case start
				 
		  case IN_STRINGLIT: {
				if(ch == '\n' || ch == '\r'){
					throw new LexicalException("Encountered Line break in StringLit", pos);
				}
				
				else if(ch == '\\'){
					state = State.IN_ESCCHECK;
					pos++;
					posInLine++;
				}
				
				else if(pos == chars.length-1 && ch != '"'){
					throw new LexicalException("Missing \" at end in StringLit", pos);
				}
				
				else if(ch == '"'){
					state =State.END_STRINGLIT;
					pos++;
					posInLine++;
				}
				
				else{
					pos++;
					posInLine++;
					state = State.IN_STRINGLIT;
				}
		  }break;
		  
		  
		  case IN_ESCCHECK: {
			
			  if(ch == 'b' || ch == 't' || ch == 'n' || ch == 'f' || ch == 'r' || ch == '"' ||
					  ch == '\'' || ch == '\\'){
				  pos++;
				  posInLine++;
				  state = State.IN_STRINGLIT;
			  }
			 
			  else	  throw new LexicalException("\\ not allowed in StringLit", pos);
			  
		  }break;
		  
		  case END_STRINGLIT: {
			  Token t = new Token(Kind.STRING_LITERAL, startPos, pos-startPos, line, startPosInLine);
			  t.getText();
			  tokens.add(t);
			  state = State.START;
		  }break;
		  
		  case IN_EQUAL: {
			  if(ch == '='){
					tokens.add(new Token(Kind.OP_EQ, startPos, 2, line, posInLine));
					pos++;
					posInLine = posInLine + 2;
			  }
			  else{
					tokens.add(new Token(Kind.OP_ASSIGN, startPos, 1, line, posInLine));
               	    posInLine++;
			  }
			  state = State.START;
		  } break;
		  
		  case IN_GT: {
			  if(ch == '='){
				  tokens.add(new Token(Kind.OP_GE, startPos, 2, line, posInLine));
					pos++;
					posInLine = posInLine + 2;
			  }
			  else{
				  tokens.add(new Token(Kind.OP_GT, startPos, 1, line, posInLine));
             	    posInLine++;
			  }
			  state = State.START;
		  } break;
		  
		  case IN_LT: {
			  if(ch == '='){
				  tokens.add(new Token(Kind.OP_LE, startPos, 2, line, posInLine));
					pos++;
					posInLine = posInLine + 2;
			  }
			  else if(ch == '-'){
				  tokens.add(new Token(Kind.OP_LARROW, startPos, 2, line, posInLine));
					pos++;
					posInLine = posInLine + 2;
			  }
			  else{
				  tokens.add(new Token(Kind.OP_LT, startPos, 1, line, posInLine));
             	    posInLine++;
			  }
			  state = State.START;
		  } break;
		  
		  case IN_EXCL: {
			  if(ch == '='){
				  tokens.add(new Token(Kind.OP_NEQ, startPos, 2, line, posInLine));
					pos++;
					posInLine = posInLine + 2;
			  }
			  else{
				  tokens.add(new Token(Kind.OP_EXCL, startPos, 1, line, posInLine));
             	    posInLine++;
			  }
			  state = State.START;
		  } break;
		  
		  case IN_MINUS: {
			  if(ch == '>'){
				  tokens.add(new Token(Kind.OP_RARROW, startPos, 2, line, posInLine));
					pos++;
					posInLine = posInLine + 2;
			  }
			  else{
				  tokens.add(new Token(Kind.OP_MINUS, startPos, 1, line, posInLine));
             	    posInLine++;
			  }
			  state = State.START;
		  } break;
		  
		  case IN_STAR: {
			  if(ch == '*'){
				  tokens.add(new Token(Kind.OP_POWER, startPos, 2, line, posInLine));
					pos++;
					posInLine = posInLine + 2;
			  }
			  else{
				  tokens.add(new Token(Kind.OP_TIMES, startPos, 1, line, posInLine));
             	    posInLine++;
			  }
			  state = State.START;
		  } break;
		  
		  case IN_DIGIT: {
			  if(Character.isDigit(ch)){
					pos++;
					posInLine++;
				}
			  else {
					Token t = new Token(Kind.INTEGER_LITERAL, startPos, pos - startPos, line, startPosInLine);
					try{
					t.intVal();
					tokens.add(t);
					state = State.START;
					}catch (NumberFormatException e){
						throw new LexicalException("Integer value exceed", startPos);
						
					}
                   }
			 }break;
			 
		  case IN_DIV: {
			  if(ch == '/'){
				  state = State.IN_COMMENT;
				  pos++;
				  posInLine = posInLine + 2;
			  }
			  else{
				  tokens.add(new Token(Kind.OP_DIV, startPos, 1, line, posInLine));
           	      posInLine++;
					state = State.START;
					}
		  }break;
		  
		  case IN_COMMENT: {
				 if(ch == '\n'){
				  pos++;
				  line++;
				  posInLine = 1;
				state = State.START;

			  }
			  else if(ch == '\r'){
				  pos++;
				  line++;
				  posInLine = 1;
				  if(chars[pos] == '\n'){
     				 pos++;
     			 }
				state = State.START;
				}
			  else if(pos == chars.length-1){
				  state = State.START;
			  }
			  else{
				  pos++;
				  posInLine++;
				  state = State.IN_COMMENT;
			  }
		  }break;
			 
		  case IN_IDENT: {
			//  if (Character.isJavaIdentifierPart(ch)) {
			  if(Character.isLetterOrDigit(ch) || ch == '$' || ch == '_'){
				 
					pos++;
					posInLine++;
				}
			 
			  else{

					Token t = new Token(Kind.IDENTIFIER, startPos, pos - startPos, line, startPosInLine);
					String temp = t.getText();

					switch(temp){
					
					case "x": {
						  tokens.add(new Token(Kind.KW_x, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "X": {
						  tokens.add(new Token(Kind.KW_X, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "y": {
						  tokens.add(new Token(Kind.KW_y, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "Y": {
						  tokens.add(new Token(Kind.KW_Y, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "r": {
						  tokens.add(new Token(Kind.KW_r, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "R": {
						  tokens.add(new Token(Kind.KW_R, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "a": {
						  tokens.add(new Token(Kind.KW_a, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "A": {
						  tokens.add(new Token(Kind.KW_A, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "Z": {
						  tokens.add(new Token(Kind.KW_Z, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break; 
						  
					case "DEF_X": {
						  tokens.add(new Token(Kind.KW_DEF_X, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "DEF_Y": {
						  tokens.add(new Token(Kind.KW_DEF_Y, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "SCREEN": {
						  tokens.add(new Token(Kind.KW_SCREEN, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "cart_x": {
						  tokens.add(new Token(Kind.KW_cart_x, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "cart_y": {
						  tokens.add(new Token(Kind.KW_cart_y, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "polar_a": {
						  tokens.add(new Token(Kind.KW_polar_a, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "polar_r": {
						  tokens.add(new Token(Kind.KW_polar_r, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "abs": {
						  tokens.add(new Token(Kind.KW_abs, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "sin": {
						  tokens.add(new Token(Kind.KW_sin, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "cos": {
						  tokens.add(new Token(Kind.KW_cos, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "atan": {
						  tokens.add(new Token(Kind.KW_atan, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "log": {
						  tokens.add(new Token(Kind.KW_log, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "image": {
						  tokens.add(new Token(Kind.KW_image, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "int": {
						  tokens.add(new Token(Kind.KW_int, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "boolean": {
						  tokens.add(new Token(Kind.KW_boolean, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "url": {
						  tokens.add(new Token(Kind.KW_url, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "file": {
						  tokens.add(new Token(Kind.KW_file, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "true": {
						  tokens.add(new Token(Kind.BOOLEAN_LITERAL, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						  
					case "false": {
						  tokens.add(new Token(Kind.BOOLEAN_LITERAL, startPos, pos - startPos, line, startPosInLine));
						  state = State.START;
						  } break;
						    
					default:{
						tokens.add(t);
						state = State.START;

					}break;
					}//end switch(temp)

			  }
		  }break;
		
		}//end switch(state)
		
		}//end while
        return this;
	}


	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}
	
	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}
	
	
	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
