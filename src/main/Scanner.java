package main;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
	
	static final String INT_CHARS = "0123456789";
	static final String FLOAT_CHARS = "0123456789.";
	static final String IGNORE_CHARS = " \t\n";
	static final String NAME_FIRST_CHARS = "abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final String NAME_CHARS = ".abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	static String OPERATOR_CHARS = "=<>|&!";
	
	static final char[] ONECHARTOKEN_CHAR = new char[] {
		'+', '-', '*', '/', '^', '(', ')', '\n', ';', '=', '[', ']', ',',
		':', '{' , '}', '!'
	};
	static final TokenType[] ONECHARTOKEN_TYPE = new TokenType[] {
		TokenType.PLUS, TokenType.MINUS, TokenType.MUL, TokenType.DIV,
		TokenType.POW, TokenType.LPAREN, TokenType.RPAREN, TokenType.EOF,
		TokenType.EOF, TokenType.SET, TokenType.LHOOK, TokenType.RHOOK,
		TokenType.COMMA, TokenType.TWO_POINTS, TokenType.LCURLYBRACKET,
		TokenType.RCURLYBRACKET, TokenType.NOT,
	};
	static final String[] STRINGTOKEN_STRING = new String[] {
		"function", "return", "true", "false", "if", "for", "break",
	};
	static final TokenType[] STRINGTOKEN_TYPE = new TokenType[] {
		TokenType.FUNCTION, TokenType.RETURN, TokenType.TRUE,
		TokenType.FALSE, TokenType.IF, TokenType.FOR, TokenType.BREAK,
	};
	static final String[] OPERATORTOKEN_STRING = new String[] {
		"==", "<=", ">=", "!=", "&&", "||", "<", ">"
	};
	static final TokenType[] OPERATORTOKEN_TYPE = new TokenType[] {
		TokenType.EQ, TokenType.INFEQ, TokenType.SUPEQ,
		TokenType.NOTEQ, TokenType.AND, TokenType.OR,
		TokenType.INF, TokenType.SUP,
	};
	
	
	final String source;
	
	char current_char = '_';
	private int line = 0;
	private int col = -1;
	private int idx = -1;
	private boolean advanceResult = false;
	public Scanner(String source) {
		this.source = source;
	}
	
	public boolean advance() {
		if (current_char == '\n') {
			this.line += 1;
			this.col = -1;
		}
		
		this.col += 1;
		this.idx += 1;
		
		if (this.source.length() > this.idx) {
			this.current_char = this.source.charAt(this.idx);
			
			advanceResult = true;
			return true;
		}
		advanceResult = false;
		return false;
	}
	
	public List<Token> scanTokens() {
		ArrayList<Token> tokens = new ArrayList<>();
		
		boolean hasToAdvance = true;
		int index;
		this.advance();
		while(this.advanceResult) {
			Token data = null;
			hasToAdvance = true;
			
			
			if (INT_CHARS.contains(String.valueOf(this.current_char))) {
				data = this.scanNumber();
				hasToAdvance = false;
			} else if ((index = this.scanOperator()) != -1) {
				data = new Token(OPERATORTOKEN_TYPE[index], this.col, this.line);
				hasToAdvance = false;
			} else if (this.current_char == '\"') {
				int baseLine = this.line;
				int baseCol = this.col;
				data = this.scanString();
				if (data == null) {
					System.out.println("String Not Finished");
					EntryPoint.raiseToken(new Token(null, baseLine, baseCol));
					return null;
				}
			} else if (NAME_FIRST_CHARS.contains(String.valueOf(this.current_char))) {
				data = this.scanName();
				hasToAdvance = false;
			} else if ((index = this.scanCharIndex()) != -1) {
				data = new Token(ONECHARTOKEN_TYPE[index], this.col, this.line);
			} else if (!this.isCharIgnore()) {
				System.out.println("Unexpected Character : \'" + this.current_char + "\'");
				EntryPoint.raiseToken(new Token(null, this.col, this.line));
				return null;
			}
			
			if (data != null) {
				tokens.add(data);
			}
			if(hasToAdvance) {
				this.advance();
			}
		}
		
		tokens.add(new Token(TokenType.EOF, this.col, this.line));
		
		return tokens;
	}

	private Token scanName() {
		StringBuffer string = new StringBuffer();
		
		while(this.advanceResult && NAME_CHARS.contains(String.valueOf(this.current_char))) {
			string.append(this.current_char);
			this.advance();
		}
		
		String s = string.toString();
		// Check Known Tokens (class, function, ...)
		for(int i = 0; i < STRINGTOKEN_STRING.length; i++) {
			if (s.equals(STRINGTOKEN_STRING[i])) {
				return new Token(STRINGTOKEN_TYPE[i], this.col, this.line);
			}
		}
		
		return new Token(TokenType.NAME, s, this.col, this.line);
	}
	
	private int scanOperator() {
		int sv_idx = this.idx;
		int sv_col = this.col;
		int sv_line = this.line;

		StringBuffer string = new StringBuffer();
		
		while(this.advanceResult && OPERATOR_CHARS.contains(String.valueOf(this.current_char))) {
			string.append(this.current_char);
			this.advance();
		}
		
		String s = string.toString();
		// Check Known Tokens (class, function, ...)
		for(int i = 0; i < OPERATORTOKEN_STRING.length; i++) {
			if (s.equals(OPERATORTOKEN_STRING[i])) {
				return i;
			}
		}
		
		this.idx = sv_idx - 1;
		this.col = sv_col - 1;
		this.line = sv_line;
		this.advance();
		
		return -1;
	}

	private Token scanString() {
		StringBuffer string = new StringBuffer();
		
		char lchar = this.current_char;
		this.advance();
		while(this.advanceResult) {
			if (this.current_char == '\"' && lchar != '\\') {
				return new Token(TokenType.STRING, string.toString(), this.col, this.line);
			}
			
			string.append(this.current_char);
			
			lchar = this.current_char;
			this.advance();
		}
		return null;
	}

	private boolean isCharIgnore() {
		return IGNORE_CHARS.contains(String.valueOf(this.current_char));
	}

	private int scanCharIndex() {
		int i = 0;
		for(char c:ONECHARTOKEN_CHAR) {
			if (this.current_char == c) {
				return i;
			}
			i++;
		}
		
		return -1;
	}

	public Token scanNumber() {
		StringBuffer numberString = new StringBuffer();
		int dot_count = 0;
		
		while (this.advanceResult && FLOAT_CHARS.contains(String.valueOf(this.current_char))) {
			if (this.current_char == '.') {
				if (dot_count == 1) {
					break;
				}
				
				dot_count += 1;
			}
			numberString.append(this.current_char);
			this.advance();
		}
		
		if (dot_count == 0) {
			return new Token(TokenType.NUMBER, Integer.parseInt(numberString.toString()), this.col, this.line);
		}
		return new Token(TokenType.NUMBER, Float.parseFloat(numberString.toString()), this.col, this.line);
	}
	
}