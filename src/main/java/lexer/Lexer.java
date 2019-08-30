package lexer;

import model.TokenType;
import model.Token;
import model.TokenTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Lexer {
	
	public TokenTable table;
	private String[] keywords = {"procedure", "var", "integer", "real", "program", "for", "while", "do", "if", "then", "write", "read", "else", "begin", "end"};
	
	private BufferedReader br;
	private Character currChar;
	private int intVal;
	private String token = "";
	private int state = 1;
	private int line = 1;
	private int column = 1;
	
	public Lexer(String source) {
		StringReader sr = new StringReader(source);
		br = new BufferedReader(sr);
		table = new TokenTable();
	}
	
	public void analyze() throws LexerException {
		currChar = ' ';
		
		readChar();
		line = 1;
		column = 1;
		
		while (intVal != -1) {
			runState();
		}
		
		if (state != 1) {
			runState();
		}
	}
	
	private void runState() throws LexerException {
		switch (state) {
			case 1:
				state1();
				break;
			case 2:
				state2();
				break;
			case 4:
				state4();
				break;
			case 6:
				state6();
				break;
			case 7:
				state7();
				break;
			case 9:
				state9();
				break;
			case 11:
				state11();
				break;
			case 12:
				state12();
				break;
			case 14:
				state14();
				break;
			case 17:
				state17();
				break;
			case 20:
				state20();
				break;
			case 22:
				state22();
				break;
		}
	}
	
	private void state1() throws LexerException {
		if (currChar == ' ' || currChar == '\n' || currChar == '\t' || currChar == '\r') {
			a0();
			state = 1;
			return;
		}
		if ("()=+-*$;,.".indexOf(currChar) >= 0) {
			a2();
			state = 1;
			return;
		}
		if (Character.isLetter(currChar)) {
			a1();
			state = 2;
			return;
		}
		if (Character.isDigit(currChar)) {
			a1();
			state = 4;
			return;
		}
		if (currChar == '/') {
			a1();
			state = 9;
			return;
		}
		if (currChar == '<') {
			a1();
			state = 14;
			return;
		}
		if (currChar == '>') {
			a1();
			state = 17;
			return;
		}
		if (currChar == '{') {
			a0();
			state = 20;
			return;
		}
		if (currChar == ':') {
			a1();
			state = 22;
			return;
		}
		throw new LexerException(String.format("Unknown char: %c", currChar), line, column);
	}
	
	private void state2() {
		if (Character.isLetterOrDigit(currChar)) {
			a3();
			state = 2;
		} else {
			a4();
			state = 1;
		}
	}
	
	private void state4() {
		if (Character.isDigit(currChar)) {
			a3();
			state = 4;
		} else {
			if (currChar == '.') {
				a3();
				state = 6;
			} else {
				a5();
				state = 1;
			}
		}
	}
	
	private void state6() throws LexerException {
		if (Character.isDigit(currChar)) {
			a3();
			state = 7;
		} else {
			throw new LexerException("Float must be in the format '0.0'", line, column);
		}
	}
	
	private void state7() {
		if (Character.isDigit(currChar)) {
			a3();
			state = 7;
		} else {
			a6();
			state = 1;
		}
	}
	
	private void state9() {
		if (currChar == '*') {
			a0();
			state = 11;
		} else {
			a7();
			state = 1;
		}
	}
	
	private void state11() {
		if (currChar == '*') {
			a0();
			state = 12;
		} else {
			a0();
			state = 11;
		}
	}
	
	private void state12() {
		if (currChar == '/') {
			a0();
			state = 1;
		} else {
			a0();
			state = 11;
		}
	}
	
	private void state14() {
		if (currChar == '=' || currChar == '>') {
			a2();
			state = 1;
		} else {
			a7();
			state = 1;
		}
	}
	
	private void state17() {
		if (currChar == '=') {
			a2();
			state = 1;
		} else {
			a7();
			state = 1;
		}
	}
	
	private void state20() {
		if (currChar == '}') {
			a0();
			state = 1;
		} else {
			a0();
			state = 20;
		}
	}
	
	private void state22() {
		if (currChar == '=') {
			a2();
			state = 1;
		} else {
			a7();
			state = 1;
		}
	}
	
	private void a0() {
		readChar();
	}
	
	private void a1() {
		token = "" + currChar;
		readChar();
	}
	
	private void a2() {
		token += currChar;
		readChar();
		table.addToken(new Token(token, TokenType.SYMBOL, line, column));
		token = "";
	}
	
	private void a3() {
		token += currChar;
		readChar();
	}
	
	private void a4() {
		for (String pr : keywords) {
			if (pr.contentEquals(token)) {
				table.addToken(new Token(token, TokenType.KEYWORD, line, column));
				token = "";
				return;
			}
		}
		table.addToken(new Token(token, TokenType.IDENTIFIER, line, column));
		token = "";
	}
	
	private void a5() {
		table.addToken(new Token(token, TokenType.LITERAL_INTEGER, line, column));
		token = "";
	}
	
	private void a6() {
		table.addToken(new Token(token, TokenType.LITERAL_REAL, line, column));
		token = "";
	}
	
	private void a7() {
		table.addToken(new Token(token, TokenType.SYMBOL, line, column));
		token = "";
	}
	
	private void readChar() {
		try {
			Character prevChar = currChar;
			intVal = br.read();
			currChar = (char) intVal;
			
			if (prevChar == '\n') {
				line++;
				column = 1;
			} else {
				column++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
